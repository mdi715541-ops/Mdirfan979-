package com.example.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentEntity
import com.example.data.model.ReelEntity
import com.example.data.model.VideoFilter
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ReelSideActions
import com.example.ui.components.ReelVideoPlayer
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple

@Composable
fun ReelsFeedScreen(
    reels: List<ReelEntity>,
    activeComments: List<CommentEntity>,
    onLikeToggle: (Long, Boolean) -> Unit,
    onRecordView: (Long) -> Unit,
    onRecordShare: (Long) -> Unit,
    onDownloadReel: (Long) -> Unit,
    onDeleteReel: (Long) -> Unit,
    onOpenCommentsForReel: (Long) -> Unit,
    onAddComment: (Long, String) -> Unit,
    onLikeComment: (Long) -> Unit,
    onAdImpression: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Reels yet",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap + to record or upload your first viral reel!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })
    var selectedFeedTab by remember { mutableStateOf("For You") }
    var activeCommentsReelId by remember { mutableStateOf<Long?>(null) }
    var showCommentsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        if (reels.isNotEmpty() && pagerState.currentPage < reels.size) {
            val currentReel = reels[pagerState.currentPage]
            onRecordView(currentReel.id)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_feed_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
                // 1. Full-screen Video Player with Filter Engine
                ReelVideoPlayer(
                    reel = reel,
                    isActive = isCurrentPage,
                    onDoubleTapLike = {
                        onLikeToggle(reel.id, reel.isLikedByMe)
                    }
                )

                // 2. Creator Info & Captions Overlay (Bottom Left)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .padding(start = 16.dp, bottom = 80.dp)
                ) {
                    // Creator Handle & Verified Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reel.creatorHandle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(ReelCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        // Filter Tag if custom filter applied
                        if (reel.filterType != "NORMAL") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ReelPink.copy(alpha = 0.35f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ReelGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = reel.filterType,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Caption & Hashtags
                    Text(
                        text = reel.caption,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = reel.hashtags,
                        fontSize = 12.sp,
                        color = ReelCyan,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio Marquee Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ReelPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${reel.audioTitle} • ${reel.audioArtist}",
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Native Sponsored AdMob Banner on every 3rd reel
                    if (page % 3 == 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AdMobBannerView(
                            onAdImpression = onAdImpression,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                }

                // 3. Side Actions Column (Bottom Right)
                ReelSideActions(
                    reel = reel,
                    onLikeToggle = { onLikeToggle(reel.id, reel.isLikedByMe) },
                    onOpenComments = {
                        activeCommentsReelId = reel.id
                        onOpenCommentsForReel(reel.id)
                        showCommentsSheet = true
                    },
                    onShare = { onRecordShare(reel.id) },
                    onDownload = { onDownloadReel(reel.id) },
                    onDeleteReel = { onDeleteReel(reel.id) },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }

        // Top Navigation Header with Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 10.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FeedTab(
                title = "Following",
                isSelected = selectedFeedTab == "Following",
                onClick = { selectedFeedTab = "Following" }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(14.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            FeedTab(
                title = "For You",
                isSelected = selectedFeedTab == "For You",
                onClick = { selectedFeedTab = "For You" }
            )
        }

        // Comments Bottom Sheet
        if (showCommentsSheet && activeCommentsReelId != null) {
            val reelId = activeCommentsReelId!!
            CommentsBottomSheet(
                comments = activeComments,
                onDismiss = { showCommentsSheet = false },
                onAddComment = { text -> onAddComment(reelId, text) },
                onLikeComment = onLikeComment
            )
        }
    }
}

@Composable
fun FeedTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ReelPink)
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}
