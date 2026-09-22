package com.example.ui.components

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ReelEntity
import com.example.data.model.VideoFilter
import com.example.ui.theme.CinematicTeal
import com.example.ui.theme.GoldenHourAmber
import com.example.ui.theme.NeonCyanMagenta
import com.example.ui.theme.NoirDark
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.VintageSepia
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ReelVideoPlayer(
    reel: ReelEntity,
    isActive: Boolean,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember(reel.id) { mutableStateOf(isActive) }
    var isMuted by remember { mutableStateOf(false) }
    var showPlayPauseIcon by remember { mutableStateOf(false) }
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var heartTapPosition by remember { mutableStateOf(Offset.Zero) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    // Simulate video progress while active and playing
    LaunchedEffect(isPlaying, isActive) {
        if (isPlaying && isActive) {
            while (true) {
                delay(100)
                playbackProgress = (playbackProgress + 0.015f) % 1f
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(reel.id) {
                detectTapGestures(
                    onTap = {
                        isPlaying = !isPlaying
                        showPlayPauseIcon = true
                        coroutineScope.launch {
                            delay(800)
                            showPlayPauseIcon = false
                        }
                    },
                    onDoubleTap = { offset ->
                        heartTapPosition = offset
                        showDoubleTapHeart = true
                        onDoubleTapLike()
                        coroutineScope.launch {
                            delay(1000)
                            showDoubleTapHeart = false
                        }
                    }
                )
            }
            .testTag("reel_video_player_${reel.id}")
    ) {
        // Video Renderer / Animated Cinematic Scene
        if (reel.videoUri.startsWith("content://") || reel.videoUri.startsWith("file://")) {
            // Real device video
            AndroidVideoSurface(
                videoUri = reel.videoUri,
                isPlaying = isPlaying && isActive,
                isMuted = isMuted,
                filterType = reel.filterType
            )
        } else {
            // High-fidelity procedural cinematic visuals for bundled reels
            ProceduralCinematicCanvas(
                reelId = reel.id,
                filterType = reel.filterType,
                isPlaying = isPlaying && isActive
            )
        }

        // Live Video Filter Overlay Layer
        VideoFilterOverlay(filterType = reel.filterType)

        // Top Vignette & Bottom Scrim Gradients for subtitle readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Play/Pause Overlay indicator
        AnimatedVisibility(
            visible = showPlayPauseIcon,
            enter = fadeIn(tween(150)) + scaleIn(tween(150)),
            exit = fadeOut(tween(300)) + scaleOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Playing" else "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Double-Tap Animated Heart Popup
        AnimatedVisibility(
            visible = showDoubleTapHeart,
            enter = scaleIn(tween(300, easing = FastOutSlowInEasing)) + fadeIn(tween(150)),
            exit = scaleOut(tween(400)) + fadeOut(tween(400)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = ReelPink,
                modifier = Modifier.size(110.dp)
            )
        }

        // Mute / Unmute Button in Top Right
        IconButton(
            onClick = { isMuted = !isMuted },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Bottom Continuous Progress Bar
        LinearProgressIndicator(
            progress = { playbackProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp)
                .align(Alignment.BottomCenter),
            color = ReelPink,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun VideoFilterOverlay(filterType: String) {
    val overlayColor = when (filterType) {
        "VINTAGE" -> VintageSepia
        "NOIR" -> NoirDark
        "VIBRANT" -> Color(0x18FF0055)
        "NEON" -> NeonCyanMagenta
        "CINEMATIC" -> CinematicTeal
        "GOLDEN_HOUR" -> GoldenHourAmber
        else -> Color.Transparent
    }

    if (overlayColor != Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
    }
}

@Composable
fun AndroidVideoSurface(
    videoUri: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    filterType: String
) {
    val context = LocalContext.current
    var mediaPlayer by remember(videoUri) { mutableStateOf<MediaPlayer?>(null) }
    var currentSurface by remember(videoUri) { mutableStateOf<Surface?>(null) }

    DisposableEffect(videoUri) {
        onDispose {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.stop()
                    mp.reset()
                    mp.release()
                }
                mediaPlayer = null
                currentSurface?.release()
                currentSurface = null
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    LaunchedEffect(isPlaying, isMuted, mediaPlayer) {
        mediaPlayer?.let { mp ->
            try {
                if (isPlaying) {
                    if (!mp.isPlaying) mp.start()
                } else {
                    if (mp.isPlaying) mp.pause()
                }
                if (isMuted) {
                    mp.setVolume(0f, 0f)
                } else {
                    mp.setVolume(1f, 1f)
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                        try {
                            val surface = Surface(st)
                            currentSurface = surface
                            val mp = MediaPlayer().apply {
                                setDataSource(ctx, Uri.parse(videoUri))
                                setSurface(surface)
                                isLooping = true
                                if (isMuted) setVolume(0f, 0f) else setVolume(1f, 1f)
                                setOnPreparedListener { prepMp ->
                                    if (isPlaying) {
                                        try {
                                            prepMp.start()
                                        } catch (e: Exception) {
                                            // ignore
                                        }
                                    }
                                }
                                prepareAsync()
                            }
                            mediaPlayer = mp
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}

                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        try {
                            mediaPlayer?.let { mp ->
                                if (mp.isPlaying) mp.stop()
                                mp.reset()
                                mp.release()
                            }
                            mediaPlayer = null
                            currentSurface?.release()
                            currentSurface = null
                        } catch (e: Exception) {
                            // ignore
                        }
                        return true
                    }

                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                }
            }
        },
        onRelease = {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.stop()
                    mp.reset()
                    mp.release()
                }
                mediaPlayer = null
                currentSurface?.release()
                currentSurface = null
            } catch (e: Exception) {
                // ignore
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProceduralCinematicCanvas(
    reelId: Long,
    filterType: String,
    isPlaying: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cinematic_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val zoomAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val effectivePhase = if (isPlaying) phase else 0f

        // Dynamic multi-layer gradient themed to reelId
        val baseGradients = when (reelId % 5L) {
            0L -> listOf(Color(0xFF0F0C20), Color(0xFF2E0854), Color(0xFFE94057), Color(0xFFF27121))
            1L -> listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77), Color(0xFFE0E1DD))
            2L -> listOf(Color(0xFF130912), Color(0xFF2B092B), Color(0xFF6B114D), Color(0xFFC72C41))
            3L -> listOf(Color(0xFF051923), Color(0xFF003554), Color(0xFF006494), Color(0xFF0582CA))
            else -> listOf(Color(0xFF140826), Color(0xFF3B156B), Color(0xFF8A2BE2), Color(0xFFFF1361))
        }

        // Draw deep cinematic background
        drawRect(
            brush = Brush.verticalGradient(
                colors = baseGradients,
                startY = 0f,
                endY = height * zoomAnim
            )
        )

        // Draw animated energy light beams & atmosphere orbs
        val centerX = width * 0.5f + sin(effectivePhase) * 60f
        val centerY = height * 0.45f + cos(effectivePhase * 0.8f) * 80f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ReelCyan.copy(alpha = 0.45f),
                    ReelPink.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = width * 0.65f * zoomAnim
            ),
            radius = width * 0.65f * zoomAnim,
            center = Offset(centerX, centerY)
        )

        // Draw glowing horizontal light rays
        val rayY = height * 0.5f + sin(effectivePhase * 1.5f) * 120f
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.5f),
                    ReelGold.copy(alpha = 0.4f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, rayY),
            end = Offset(width, rayY),
            strokeWidth = 4f
        )
    }
}
