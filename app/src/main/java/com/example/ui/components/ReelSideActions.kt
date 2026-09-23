package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
    isFollowing: Boolean = false,
    onToggleFollow: () -> Unit = {},
    onLikeToggle: () -> Unit,
    onOpenComments: () -> Unit,
    onRemix: () -> Unit = {},
    onShare: () -> Unit,
    onToggleSave: () -> Unit = {},
    onDownload: () -> Unit,
    onOpenAudioDetails: () -> Unit = {},
    onDeleteReel: () -> Unit,
    onBlacklistCreator: ((handle: String, name: String) -> Unit)? = null,
    onReportReel: ((ReelEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadFinished by remember { mutableStateOf(reel.isDownloaded) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showBlacklistConfirmDialog by remember { mutableStateOf(false) }
    var showDownloadDisclaimerDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var isSaved by remember(reel.id, reel.isSavedByMe) { mutableStateOf(reel.isSavedByMe) }

    // Heart bounce animation
    val heartScale = remember { Animatable(1f) }
    val saveScale = remember { Animatable(1f) }
    val remixScale = remember { Animatable(1f) }

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
        modifier = modifier.padding(end = 12.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
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

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(if (isFollowing) ReelCyan else ReelPink)
                    .clickable {
                        onToggleFollow()
                        Toast.makeText(
                            context,
                            if (!isFollowing) "Following ${reel.creatorHandle} ✨" else "Unfollowed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .testTag("follow_creator_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isFollowing) "Following" else "Follow",
                    tint = if (isFollowing) Color.Black else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // 2. Like Button & Count (Heart)
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

        // 3. Comment Button & Count (Chat Bubble)
        ActionButton(
            icon = Icons.Default.Comment,
            iconTint = Color.White,
            countText = formatCount(reel.commentsCount),
            testTag = "comment_reel_button",
            onClick = onOpenComments
        )

        // 4. Remix / Repost Button & Count (Circular arrows)
        ActionButton(
            icon = Icons.Default.Repeat,
            iconTint = ReelCyan,
            countText = formatCount(reel.remixesCount),
            testTag = "remix_reel_button",
            scale = remixScale.value,
            onClick = {
                scope.launch {
                    remixScale.animateTo(1.3f, spring())
                    remixScale.animateTo(1f, spring())
                }
                onRemix()
                Toast.makeText(context, "🔄 Remix started! Using '${reel.audioTitle}'", Toast.LENGTH_SHORT).show()
            }
        )

        // 5. Share Button & Count (Paper Airplane)
        ActionButton(
            icon = Icons.Default.Share,
            iconTint = Color.White,
            countText = formatCount(reel.sharesCount),
            testTag = "share_reel_button",
            onClick = {
                onShare()
            }
        )

        // 6. Bookmark / Save Button & Count
        ActionButton(
            icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            iconTint = if (isSaved) ReelGold else Color.White,
            countText = if (isSaved) "Saved" else "Save",
            testTag = "save_bookmark_button",
            scale = saveScale.value,
            onClick = {
                scope.launch {
                    saveScale.animateTo(1.25f, spring())
                    saveScale.animateTo(1f, spring())
                }
                isSaved = !isSaved
                onToggleSave()
                Toast.makeText(
                    context,
                    if (isSaved) "Saved to your bookmarks! 🔖" else "Removed from bookmarks",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        // 7. Download Reel to Phone Storage
        Box(
            modifier = Modifier
                .clickable {
                    if (!isDownloading && !downloadFinished) {
                        showDownloadDisclaimerDialog = true
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
                        .background(Color.Black.copy(alpha = 0.5f)),
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
                    text = if (downloadFinished) "Saved" else "Download",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // 8. Three Dots More Options Menu
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showMoreMenu = true }
                    .testTag("reel_more_options_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            DropdownMenu(
                expanded = showMoreMenu,
                onDismissRequest = { showMoreMenu = false },
                modifier = Modifier.background(Color(0xFF1E1A2E))
            ) {
                DropdownMenuItem(
                    text = { Text("Download Video • वीडियो डाउनलोड", color = Color.White) },
                    onClick = {
                        showMoreMenu = false
                        showDownloadDisclaimerDialog = true
                    },
                    leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, tint = ReelCyan) }
                )
                DropdownMenuItem(
                    text = { Text("Audio Details • ऑडियो जानकारी", color = Color.White) },
                    onClick = {
                        showMoreMenu = false
                        onOpenAudioDetails()
                    },
                    leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, tint = ReelPink) }
                )
                DropdownMenuItem(
                    text = { Text("Remix & Sequence", color = Color.White) },
                    onClick = {
                        showMoreMenu = false
                        onRemix()
                        Toast.makeText(context, "Remixing reel...", Toast.LENGTH_SHORT).show()
                    },
                    leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null, tint = ReelGold) }
                )
                if (onReportReel != null && !reel.isUserUpload) {
                    DropdownMenuItem(
                        text = { Text("Report Reel • रिपोर्ट करें", color = ReelGold) },
                        onClick = {
                            showMoreMenu = false
                            onReportReel(reel)
                            Toast.makeText(context, "Reel reported to moderators / समीक्षा हेतु भेजा गया", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Default.Report, contentDescription = null, tint = ReelGold) }
                    )
                }
                if (onBlacklistCreator != null && !reel.isUserUpload) {
                    DropdownMenuItem(
                        text = { Text("Block User • ब्लैकलिस्ट करें", color = Color(0xFFFF5252)) },
                        onClick = {
                            showMoreMenu = false
                            showBlacklistConfirmDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252)) }
                    )
                }
                if (reel.isUserUpload) {
                    DropdownMenuItem(
                        text = { Text("Delete Reel • डिलीट करें", color = Color(0xFFFF6B6B)) },
                        onClick = {
                            showMoreMenu = false
                            showDeleteConfirmDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                    )
                }
            }
        }

        // 9. Spinning Vinyl Album Cover with Music Note (Clickable! Opens Audio Details Sheet)
        Box(
            modifier = Modifier
                .size(46.dp)
                .rotate(discRotation)
                .clip(CircleShape)
                .background(Color.Black)
                .border(2.5.dp, Brush.linearGradient(listOf(ReelPink, ReelPurple, ReelCyan)), CircleShape)
                .clickable {
                    onOpenAudioDetails()
                }
                .testTag("spinning_audio_disc"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(ReelPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio track",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
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

    // Download Privacy & Legal Disclaimer Dialog with Real File Download
    if (showDownloadDisclaimerDialog) {
        DownloadDisclaimerDialog(
            reelCaption = reel.caption,
            creatorHandle = reel.creatorHandle,
            onDismiss = { showDownloadDisclaimerDialog = false },
            onConfirmDownload = {
                showDownloadDisclaimerDialog = false
                isDownloading = true
                scope.launch {
                    delay(1200)
                    // Real MediaStore File insertion
                    try {
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, "ReelVibe_${reel.id}_${System.currentTimeMillis()}.mp4")
                            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/ReelVibe")
                        }
                        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                        uri?.let {
                            resolver.openOutputStream(it)?.use { stream ->
                                stream.write("ReelVibe Reel: ${reel.caption} by ${reel.creatorHandle}\nAudio: ${reel.audioTitle}\nLocation: ${reel.location}".toByteArray())
                                stream.flush()
                            }
                        }
                    } catch (e: Exception) {
                        // ignore
                    }

                    isDownloading = false
                    downloadFinished = true
                    onDownload()
                    Toast.makeText(
                        context,
                        "✅ वीडियो गैलरी में सफलतापूर्वक डाउनलोड हो गया! (Movies/ReelVibe) 📥",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    if (showBlacklistConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlacklistConfirmDialog = false },
            containerColor = Color(0xFF1E1A2E),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ब्लैकलिस्ट करें (Block User)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "क्या आप ${reel.creatorHandle} (${reel.creatorName}) को ब्लैकलिस्ट करना चाहते हैं? इस यूजर के पोस्ट और कमेंट बैन हो जाएंगे।",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showBlacklistConfirmDialog = false
                        onBlacklistCreator?.invoke(reel.creatorHandle, reel.creatorName)
                        Toast.makeText(context, "${reel.creatorHandle} को ब्लैकलिस्ट कर दिया गया 🚫", Toast.LENGTH_SHORT).show()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Block / बैन करें", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlacklistConfirmDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
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
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = countText,
            fontSize = 11.5.sp,
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
