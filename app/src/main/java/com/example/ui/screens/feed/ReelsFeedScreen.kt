package com.example.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ModerationReportEntity
import com.example.data.model.ReelEntity
import com.example.data.model.StoryEntity
import com.example.data.model.VideoFilter
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.AudioTrackDetailSheet
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreateStorySheet
import com.example.ui.components.ModeratorControlSheet
import com.example.ui.components.ReelShareSheet
import com.example.ui.components.ReelSideActions
import com.example.ui.components.ReelVideoPlayer
import com.example.ui.components.StoriesTray
import com.example.ui.components.StoryViewerSheet
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple

@Composable
fun ReelsFeedScreen(
    reels: List<ReelEntity>,
    activeComments: List<CommentEntity>,
    stories: List<StoryEntity> = emptyList(),
    blacklistedUsers: List<BlacklistedUserEntity> = emptyList(),
    pendingReports: List<ModerationReportEntity> = emptyList(),
    currentUserHandle: String = "@irafan_creator",
    followedCreators: Set<String> = emptySet(),
    onToggleFollow: (String, String) -> Unit = { _, _ -> },
    onLikeToggle: (Long, Boolean) -> Unit,
    onRecordView: (Long) -> Unit,
    onRecordShare: (Long) -> Unit,
    onDownloadReel: (Long) -> Unit,
    onDeleteReel: (Long) -> Unit,
    onOpenCommentsForReel: (Long) -> Unit,
    onAddComment: (Long, String) -> Unit,
    onLikeComment: (Long) -> Unit,
    onDeleteComment: (Long) -> Unit = {},
    onBanCommenter: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    onBlacklistUser: (String, String, String) -> Unit = { _, _, _ -> },
    onUnblacklistUser: (String) -> Unit = {},
    onDeleteBlacklistEntry: (Long) -> Unit = {},
    onResolveReportAndBan: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    onDismissReport: (Long) -> Unit = {},
    onReportReel: (ReelEntity, String) -> Unit = { _, _ -> },
    onAdImpression: () -> Unit,
    onAddStory: (mediaType: String, mediaUri: String, audioTitle: String, audioArtist: String, caption: String, stickerText: String, location: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddReelToStatus: (ReelEntity) -> Unit = {},
    onStoryViewed: (Long) -> Unit = {},
    onLikeStory: (Long) -> Unit = {},
    onDeleteStory: (Long) -> Unit = {},
    onCreateReelWithAudio: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedFeedTab by remember { mutableStateOf("For You") }
    var showModeratorZoneSheet by remember { mutableStateOf(false) }

    val blacklistedHandles = remember(blacklistedUsers) {
        blacklistedUsers.map { it.userHandle }.toSet()
    }

    val displayReels = remember(reels, selectedFeedTab, followedCreators, blacklistedHandles) {
        val nonBanned = reels.filterNot { blacklistedHandles.contains(it.creatorHandle) }
        if (selectedFeedTab == "Following") {
            val filtered = nonBanned.filter { followedCreators.contains(it.creatorHandle) }
            if (filtered.isNotEmpty()) filtered else nonBanned
        } else {
            nonBanned
        }
    }

    // Modal Sheet & Dialog States
    var activeCommentsReelId by remember { mutableStateOf<Long?>(null) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var activeViewingStory by remember { mutableStateOf<StoryEntity?>(null) }
    var showCreateStoryDialog by remember { mutableStateOf(false) }
    var activeAudioReel by remember { mutableStateOf<ReelEntity?>(null) }
    var activeShareReel by remember { mutableStateOf<ReelEntity?>(null) }
    var preSelectedStoryAudio by remember { mutableStateOf<Pair<String, String>?>(null) }

    if (displayReels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (selectedFeedTab == "Following") "No Followed Creators Yet" else "No Reels yet",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (selectedFeedTab == "Following") "Tap 'Follow' on creators to see their reels here!" else "Tap + to record or upload your first viral reel!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { displayReels.size })

    LaunchedEffect(pagerState.currentPage, displayReels) {
        if (displayReels.isNotEmpty() && pagerState.currentPage < displayReels.size) {
            val currentReel = displayReels[pagerState.currentPage]
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
            val reel = displayReels[page]
            val isCurrentPage = pagerState.currentPage == page
            val isFollowing = followedCreators.contains(reel.creatorHandle)

            Box(modifier = Modifier.fillMaxSize()) {
                // 1. Full-screen Video Player with Filter Engine
                ReelVideoPlayer(
                    reel = reel,
                    isActive = isCurrentPage,
                    onDoubleTapLike = {
                        onLikeToggle(reel.id, !reel.isLikedByMe)
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Reel Video Sticker Overlay (if configured)
                if (reel.videoStickerText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 135.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, ReelGold.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = reel.videoStickerText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3. Bottom Gradient Shadow Protection for Text Readability
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // 4. Reel Information Layer (Bottom Left)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .navigationBarsPadding()
                        .padding(start = 16.dp, bottom = 24.dp)
                ) {
                    // Creator Handle + Follow Button + Sponsored / India Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar initial
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ReelPink, ReelPurple))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = reel.creatorName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = reel.creatorHandle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Follow / Following Capsule Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isFollowing) Color.White.copy(alpha = 0.2f)
                                            else ReelPink
                                        )
                                        .clickable {
                                            onToggleFollow(reel.creatorHandle, reel.creatorName)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                        .testTag("follow_creator_button_${reel.creatorHandle}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isFollowing) "Following" else "Follow",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            if (reel.isMonetized) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ReelGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Monetized • ReelVibe Creator",
                                        fontSize = 11.sp,
                                        color = ReelGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location Tag (e.g. अपना फखरपुर बहराइच / India)
                    if (reel.location.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "📍 ${reel.location}",
                                fontSize = 11.5.sp,
                                color = ReelCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Reel Caption
                    Text(
                        text = reel.caption,
                        fontSize = 13.5.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    // Hashtags
                    if (reel.hashtags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = reel.hashtags,
                            fontSize = 12.sp,
                            color = ReelCyan,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // =========================================================================
                    // Audio Marquee Bar + "Use Sound • गाना लें" Direct Quick Chip
                    // Fulfills: "reel Se uski gane ki copy karke apni real mein jod sake"
                    // =========================================================================
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clickable Audio Marquee
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable {
                                    activeAudioReel = reel
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("audio_marquee_${reel.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ReelPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${reel.audioTitle} • ${reel.audioArtist}",
                                fontSize = 11.5.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 135.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Quick Button: "Use Sound • गाना कॉपी करें"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(listOf(ReelPink, ReelPurple))
                                )
                                .clickable {
                                    activeAudioReel = reel
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("use_sound_chip_${reel.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Use Sound • गाना लें",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Native Sponsored AdMob Banner on every 3rd reel
                    if (page % 3 == 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AdMobBannerView(
                            onAdImpression = onAdImpression,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                }

                // 5. Side Actions Column (Bottom Right)
                ReelSideActions(
                    reel = reel,
                    isFollowing = isFollowing,
                    onToggleFollow = { onToggleFollow(reel.creatorHandle, reel.creatorName) },
                    onLikeToggle = { onLikeToggle(reel.id, reel.isLikedByMe) },
                    onOpenComments = {
                        activeCommentsReelId = reel.id
                        onOpenCommentsForReel(reel.id)
                        showCommentsSheet = true
                    },
                    onShare = {
                        activeShareReel = reel
                    },
                    onDownload = { onDownloadReel(reel.id) },
                    onOpenAudioDetails = {
                        activeAudioReel = reel
                    },
                    onDeleteReel = { onDeleteReel(reel.id) },
                    onBlacklistCreator = { handle, name ->
                        onBlacklistUser(handle, name, "कम्युनिटी गाइडलाइंस और सुरक्षा नीति के तहत ब्लैकलिस्ट")
                    },
                    onReportReel = { r ->
                        onReportReel(r, "COMMUNITY_GUIDELINE_VIOLATION")
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }

        // =========================================================================
        // Top Navigation Header with Tabs + Moderator Zone + Instagram Stories
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 4.dp)
        ) {
            // Tabs: Following | For You + Moderator Shield Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(36.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                IconButton(
                    onClick = { showModeratorZoneSheet = true },
                    modifier = Modifier.size(36.dp).testTag("open_moderator_zone_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (pendingReports.isNotEmpty()) {
                                Badge(containerColor = Color(0xFFFF1744)) {
                                    Text(pendingReports.size.toString(), fontSize = 9.sp, color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Moderator Zone",
                            tint = if (pendingReports.isNotEmpty()) Color(0xFFFF5252) else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Stories / Status Tray (Instagram aesthetic with rainbow rings)
            StoriesTray(
                stories = stories,
                currentUserHandle = currentUserHandle,
                onStoryClick = { clickedStory ->
                    activeViewingStory = clickedStory
                },
                onCreateStoryClick = {
                    preSelectedStoryAudio = null
                    showCreateStoryDialog = true
                }
            )
        }

        // =========================================================================
        // Bottom Sheets & Overlays
        // =========================================================================

        // 1. Comments Bottom Sheet with Moderation / Ban options
        if (showCommentsSheet && activeCommentsReelId != null) {
            val reelId = activeCommentsReelId!!
            CommentsBottomSheet(
                comments = activeComments,
                onDismiss = { showCommentsSheet = false },
                onAddComment = { text -> onAddComment(reelId, text) },
                onLikeComment = onLikeComment,
                onDeleteComment = onDeleteComment,
                onBanCommenter = onBanCommenter,
                onOpenModeratorZone = {
                    showCommentsSheet = false
                    showModeratorZoneSheet = true
                }
            )
        }

        // 2. Audio Track Detail Sheet ("गाना कॉपी करके अपनी रील में जोड़ें" / "स्टेटस लगाएं")
        if (activeAudioReel != null) {
            val reel = activeAudioReel!!
            AudioTrackDetailSheet(
                audioTitle = reel.audioTitle,
                audioArtist = reel.audioArtist,
                onUseAudioInReel = { title, artist ->
                    onCreateReelWithAudio(title, artist)
                },
                onUseAudioInStory = { title, artist ->
                    preSelectedStoryAudio = Pair(title, artist)
                    showCreateStoryDialog = true
                },
                onDismiss = { activeAudioReel = null }
            )
        }

        // 3. Reel Share Sheet ("Add Reel to Your Status • अपने स्टेटस पर लगाएं")
        if (activeShareReel != null) {
            val reel = activeShareReel!!
            ReelShareSheet(
                reel = reel,
                onAddToStatus = { reelToShare ->
                    onAddReelToStatus(reelToShare)
                },
                onRecordShare = { id ->
                    onRecordShare(id)
                },
                onDismiss = { activeShareReel = null }
            )
        }

        // 4. Story Viewer Sheet (24h Status with progress timer, music playback & reply)
        if (activeViewingStory != null) {
            StoryViewerSheet(
                initialStory = activeViewingStory!!,
                allStories = stories,
                onDismiss = { activeViewingStory = null },
                onStoryViewed = onStoryViewed,
                onLikeStory = onLikeStory,
                onDeleteStory = onDeleteStory
            )
        }

        // 5. Create Story Dialog (गाना वाना लगाकर फोटो या वीडियो स्टेटस लगाएं)
        if (showCreateStoryDialog) {
            CreateStorySheet(
                initialAudioTitle = preSelectedStoryAudio?.first ?: "Nigahen Kyon Churaati Hai",
                initialAudioArtist = preSelectedStoryAudio?.second ?: "Udit Narayan",
                onPostStory = { mediaType, mediaUri, audioTitle, audioArtist, caption, stickerText, location ->
                    onAddStory(mediaType, mediaUri, audioTitle, audioArtist, caption, stickerText, location)
                },
                onDismiss = {
                    showCreateStoryDialog = false
                    preSelectedStoryAudio = null
                }
            )
        }

        // 6. Moderator Control Zone Sheet (ब्लैकलिस्ट और मॉडरेटर कंट्रोल)
        if (showModeratorZoneSheet) {
            ModeratorControlSheet(
                blacklistedUsers = blacklistedUsers,
                pendingReports = pendingReports,
                onBlacklistUser = onBlacklistUser,
                onUnblacklistUser = onUnblacklistUser,
                onDeleteBlacklistEntry = onDeleteBlacklistEntry,
                onResolveReportAndBan = onResolveReportAndBan,
                onDismissReport = onDismissReport,
                onDismiss = { showModeratorZoneSheet = false }
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
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .testTag("feed_tab_$title")
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.5.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (isSelected) ReelPink else Color.Transparent)
        )
    }
}
