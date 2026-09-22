package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReelEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReelSideActions(
    reel: ReelEntity,
    onLikeToggle: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onDeleteReel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isFollowing by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadFinished by remember { mutableStateOf(reel.isDownloaded) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Heart bounce animation
    val heartScale = remember { Animatable(1f) }

    // Spinning Vinyl Animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Creator Avatar with Follow (+) Button
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(ReelPink, ReelCyan)),
                        shape = CircleShape
                    )
                    .background(ReelPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reel.creatorName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (!isFollowing) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomCenter)
                        .clip(CircleShape)
                        .background(ReelPink)
                        .clickable {
                            isFollowing = true
                            Toast.makeText(context, "Followed ${reel.creatorName}", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("follow_creator_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // 2. Like Button & Count
        ActionButton(
            icon = if (reel.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            iconTint = if (reel.isLikedByMe) ReelPink else Color.White,
            countText = formatCount(reel.likesCount),
            testTag = "like_reel_button",
            scale = heartScale.value,
            onClick = {
                scope.launch {
                    heartScale.animateTo(1.35f, spring())
                    heartScale.animateTo(1f, spring())
                }
                onLikeToggle()
            }
        )

        // 3. Comment Button & Count
        ActionButton(
            icon = Icons.Default.Comment,
            iconTint = Color.White,
            countText = formatCount(reel.commentsCount),
            testTag = "comment_reel_button",
            onClick = onOpenComments
        )

        // 4. Share Button & Count
        ActionButton(
            icon = Icons.Default.Share,
            iconTint = Color.White,
            countText = formatCount(reel.sharesCount),
            testTag = "share_reel_button",
            onClick = {
                onShare()
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Watch on ReelVibe: ${reel.caption}")
                    putExtra(Intent.EXTRA_TEXT, "${reel.caption}\n\nWatch this viral reel by ${reel.creatorHandle} on ReelVibe! ✨\nhttps://reelvibe.app/reel/${reel.id}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Reel via"))
            }
        )

        // 5. Download Reel to Phone Storage
        Box(
            modifier = Modifier
                .clickable {
                    if (!isDownloading && !downloadFinished) {
                        isDownloading = true
                        scope.launch {
                            delay(1500)
                            isDownloading = false
                            downloadFinished = true
                            onDownload()
                            Toast.makeText(context, "Reel downloaded to phone gallery! 📥", Toast.LENGTH_LONG).show()
                        }
                    } else if (downloadFinished) {
                        Toast.makeText(context, "Already saved to your phone! 📁", Toast.LENGTH_SHORT).show()
                    }
                }
                .testTag("download_reel_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            color = ReelCyan,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (downloadFinished) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = "Downloaded",
                            tint = ReelCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Reel",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (downloadFinished) "Saved" else "Save",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // 6. Delete Reel Button (for user's own uploads)
        if (reel.isUserUpload) {
            Box(
                modifier = Modifier
                    .clickable { showDeleteConfirmDialog = true }
                    .testTag("delete_reel_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Reel",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Delete",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }
        }

        // 7. Spinning Vinyl Disc with Music Note
        Box(
            modifier = Modifier
                .size(44.dp)
                .rotate(discRotation)
                .clip(CircleShape)
                .background(Color.Black)
                .border(2.dp, Color(0xFF333333), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(ReelPink)
            )
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Delete Reel? • रील डिलीट करें?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete this reel? It will be removed from your profile and the public feed.",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteReel()
                        Toast.makeText(context, "Reel deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1A2E)
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    iconTint: Color,
    countText: String,
    testTag: String,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .scale(scale)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = countText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
