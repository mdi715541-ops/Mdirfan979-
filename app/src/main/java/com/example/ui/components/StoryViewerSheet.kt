package com.example.ui.components

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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StoryEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoryViewerSheet(
    initialStory: StoryEntity,
    allStories: List<StoryEntity>,
    onDismiss: () -> Unit,
    onStoryViewed: (Long) -> Unit,
    onLikeStory: (Long) -> Unit,
    onDeleteStory: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentIndex by remember {
        val idx = allStories.indexOfFirst { it.id == initialStory.id }
        mutableIntStateOf(if (idx >= 0) idx else 0)
    }

    if (allStories.isEmpty() || currentIndex !in allStories.indices) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    val currentStory = allStories[currentIndex]

    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var hasLiked by remember { mutableStateOf(false) }
    val heartScale = remember { Animatable(1f) }
    var showHeartBurst by remember { mutableStateOf(false) }

    // Mark current story as viewed
    LaunchedEffect(currentStory.id) {
        progress = 0f
        hasLiked = false
        onStoryViewed(currentStory.id)
    }

    // Story 5-second progress timer
    LaunchedEffect(currentStory.id, isPaused) {
        if (!isPaused) {
            val stepTime = 50L
            val totalSteps = 100
            while (progress < 1f) {
                delay(stepTime)
                if (!isPaused) {
                    progress += (1f / totalSteps)
                }
            }
            // Auto advance
            if (currentIndex < allStories.lastIndex) {
                currentIndex++
            } else {
                onDismiss()
            }
        }
    }

    // Infinite rotation for spinning audio badge
    val infiniteTransition = rememberInfiniteTransition(label = "audio_spin")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(currentStory.id) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            val screenWidth = size.width
                            if (offset.x < screenWidth * 0.3f) {
                                // Tap left: previous story
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
                            } else {
                                // Tap right: next story
                                if (currentIndex < allStories.lastIndex) {
                                    currentIndex++
                                } else {
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
                .testTag("story_viewer_screen")
        ) {
            // Background Canvas / Media Display
            StoryMediaBackground(story = currentStory)

            // Top Gradient Protection Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
            )

            // Bottom Gradient Protection Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // 1. Top Story Progress Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allStories.forEachIndexed { index, _ ->
                        val barProgress = when {
                            index < currentIndex -> 1f
                            index == currentIndex -> progress
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { barProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.25f),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Creator Header + Audio Marquee + Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Creator Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(ReelPink, ReelPurple))
                            )
                            .border(1.5.dp, ReelGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentStory.creatorName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentStory.creatorName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Just now",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.5.sp
                            )
                        }

                        // Audio Playing Tag with Music Note
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ReelGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentStory.audioTitle} • ${currentStory.audioArtist}",
                                color = ReelGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Mute / Unmute
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // If user's own story, allow delete
                    if (currentStory.isUserStory) {
                        IconButton(
                            onClick = {
                                onDeleteStory(currentStory.id)
                                Toast.makeText(context, "Status deleted • स्टेटस हटा दिया गया", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Status",
                                tint = Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. Middle Story Stickers & Location
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Floating Sticker
                    if (currentStory.stickerText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.5.dp, ReelGold, RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentStory.stickerText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Location Tag
                    if (currentStory.location.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ReelCyan.copy(alpha = 0.25f))
                                .border(1.dp, ReelCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "📍 ${currentStory.location}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Story Caption
                    if (currentStory.caption.isNotBlank()) {
                        Text(
                            text = currentStory.caption,
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Audio Equalizer Waveform & Vinyl Disc
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, ReelPink.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(discRotation)
                                .clip(CircleShape)
                                .background(ReelPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = ReelCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentStory.audioTitle,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Bottom Interaction Bar (Reply + Like + Share)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStory.isUserStory) {
                        // User's own story: Views indicator
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = ReelCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Seen by ${currentStory.likesCount * 3 + 24} viewers • अपना स्टेटस",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Reply to story text input
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = {
                                Text(
                                    text = "Send message • मैसेज भेजें...",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.5.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("story_reply_input"),
                            trailingIcon = {
                                if (replyText.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Message sent to ${currentStory.creatorName} ✨", Toast.LENGTH_SHORT).show()
                                            replyText = ""
                                        }
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = ReelCyan, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Like Heart Button
                    IconButton(
                        onClick = {
                            scope.launch {
                                heartScale.animateTo(1.4f, spring())
                                heartScale.animateTo(1f, spring())
                            }
                            hasLiked = !hasLiked
                            showHeartBurst = true
                            if (hasLiked) {
                                onLikeStory(currentStory.id)
                                Toast.makeText(context, "Liked status! ❤️", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(
                            imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (hasLiked) ReelPink else Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(if (hasLiked) -10f else 0f)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Story link copied! Share anywhere 📤", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Big Heart Burst Animation when liked
            AnimatedVisibility(
                visible = showHeartBurst,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                LaunchedEffect(showHeartBurst) {
                    delay(800)
                    showHeartBurst = false
                }
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = ReelPink,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}

@Composable
fun StoryMediaBackground(story: StoryEntity) {
    val gradientColors = when (story.id.toInt() % 4) {
        0 -> listOf(Color(0xFF1A0A2A), Color(0xFF2E0854), Color(0xFF140520))
        1 -> listOf(Color(0xFF041C32), Color(0xFF04293A), Color(0xFF064663))
        2 -> listOf(Color(0xFF281005), Color(0xFF4A1C0A), Color(0xFF1F0800))
        else -> listOf(Color(0xFF1F0024), Color(0xFF38084A), Color(0xFF100018))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(gradientColors))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Decorative background artistic circles
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(ReelPurple.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(canvasWidth * 0.2f, canvasHeight * 0.3f),
                    radius = canvasWidth * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(ReelCyan.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(canvasWidth * 0.8f, canvasHeight * 0.6f),
                    radius = canvasWidth * 0.5f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(ReelPink.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.8f),
                    radius = canvasWidth * 0.7f
                )
            )
        }
    }
}
