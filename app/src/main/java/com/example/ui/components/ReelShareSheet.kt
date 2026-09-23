package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReelEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelShareSheet(
    reel: ReelEntity,
    onAddToStatus: (ReelEntity) -> Unit,
    onRecordShare: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ReelSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp)
                .testTag("reel_share_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Share Reel • शेयर करें",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PROMINENT FEATURE: "Add to Status • अपने स्टेटस पर लगाएं"
            // Allows the user to directly post this video to their 24h status with music!
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2E0854), Color(0xFF1B0533))
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.sweepGradient(
                            listOf(ReelPink, ReelPurple, ReelGold, ReelCyan, ReelPink)
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        onAddToStatus(reel)
                        onRecordShare(reel.id)
                        Toast.makeText(
                            context,
                            "✨ Video Reel added to your Status with music! (स्टेटस पर लगा दिया गया)",
                            Toast.LENGTH_LONG
                        ).show()
                        onDismiss()
                    }
                    .padding(16.dp)
                    .testTag("add_reel_to_status_option")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Instagram Story Gradient Circle with Play Icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ReelPink, ReelPurple, ReelGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Add to Story",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Add to Your Status • स्टेटस पर लगाएं",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ReelGold, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Post this video with song '${reel.audioTitle}' to your 24h Status",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "More Share Options • अन्य विकल्प",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Share to WhatsApp
            ShareOptionItem(
                icon = Icons.Default.Send,
                iconBg = Color(0xFF25D366),
                title = "Share to WhatsApp • व्हाट्सएप",
                subtitle = "Share video link with friends and WhatsApp Status",
                onClick = {
                    onRecordShare(reel.id)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        `package` = "com.whatsapp"
                        putExtra(Intent.EXTRA_TEXT, "Watch this viral reel by ${reel.creatorHandle} on ReelVibe! 🎬✨\nhttps://reelvibe.app/reel/${reel.id}\n\n${reel.caption}")
                    }
                    try {
                        context.startActivity(shareIntent)
                    } catch (e: Exception) {
                        // If WhatsApp not installed, launch general share
                        val fallback = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Watch this viral reel by ${reel.creatorHandle} on ReelVibe! 🎬✨\nhttps://reelvibe.app/reel/${reel.id}")
                        }
                        context.startActivity(Intent.createChooser(fallback, "Share via"))
                    }
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Copy Link
            ShareOptionItem(
                icon = Icons.Default.ContentCopy,
                iconBg = ReelCyan,
                title = "Copy Link • लिंक कॉपी करें",
                subtitle = "https://reelvibe.app/reel/${reel.id}",
                onClick = {
                    onRecordShare(reel.id)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Reel Link", "https://reelvibe.app/reel/${reel.id}")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Share to other apps (Native Android Intent)
            ShareOptionItem(
                icon = Icons.Default.Share,
                iconBg = ReelPurple,
                title = "Share via other apps • अन्य ऐप्स पर शेयर करें",
                subtitle = "Instagram, Telegram, Messages, and more",
                onClick = {
                    onRecordShare(reel.id)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Watch on ReelVibe: ${reel.caption}")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${reel.caption}\n\n📍 ${if (reel.location.isNotBlank()) reel.location else "India"}\nWatch this viral reel by ${reel.creatorHandle} on ReelVibe! ✨\nhttps://reelvibe.app/reel/${reel.id}"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Reel via"))
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ShareOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
