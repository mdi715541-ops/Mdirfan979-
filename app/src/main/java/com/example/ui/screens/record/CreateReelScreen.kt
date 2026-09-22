package com.example.ui.screens.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.AudioTrack
import com.example.data.model.ReelEntity
import com.example.data.model.VideoFilter
import com.example.ui.components.FilterPickerBar
import com.example.ui.components.VideoFilterOverlay
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreateReelScreen(
    currentUserName: String,
    currentUserHandle: String,
    onPublishReel: (ReelEntity) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Mode: "CAMERA" or "UPLOADED_PREVIEW"
    var mode by remember { mutableStateOf("CAMERA") }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var recordSeconds by remember { mutableIntStateOf(0) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isFlashOn by remember { mutableStateOf(false) }

    // Reel custom attributes
    var selectedFilter by remember { mutableStateOf(VideoFilter.NORMAL) }
    var captionText by remember { mutableStateOf("Vibing with ReelVibe! ✨🔥") }
    var hashtagsText by remember { mutableStateOf("#ReelVibe #Trending #Creative #Viral") }
    var selectedAudioTitle by remember { mutableStateOf("Trending Beat • ReelVibe Vibes") }
    var selectedAudioArtist by remember { mutableStateOf("Original Sound") }

    // Dialogs
    var showAudioDialog by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }

    // Check camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    // Gallery Video Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            mode = "UPLOADED_PREVIEW"
            Toast.makeText(context, "Video loaded from Gallery! 🎥", Toast.LENGTH_SHORT).show()
        }
    }

    // Video Picker for Audio Extraction (रील्स में बैकग्राउंड या वीडियो से ऑडियो लेने का ऑप्शन)
    val extractAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "ExtractedTrack"
            selectedAudioTitle = "Extracted Audio • ${fileName.takeLast(14)}"
            selectedAudioArtist = "Sound from Video"
            Toast.makeText(context, "Audio extracted from video successfully! 🎵", Toast.LENGTH_LONG).show()
        }
    }

    // Record Timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordSeconds = 0
            while (isRecording && recordSeconds < 60) {
                delay(1000)
                recordSeconds += 1
            }
            if (recordSeconds >= 60) {
                isRecording = false
                mode = "UPLOADED_PREVIEW"
                Toast.makeText(context, "Recording completed (60s limit)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("create_reel_screen")
    ) {
        // Camera Viewfinder or Uploaded Preview
        if (mode == "CAMERA") {
            if (hasCameraPermission) {
                // Ensure camera is unbound if user leaves or switches lens
                DisposableEffect(lifecycleOwner, lensFacing) {
                    onDispose {
                        try {
                            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                            cameraProvider.unbindAll()
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                // COMPATIBLE uses TextureView instead of SurfaceView to avoid BufferQueue abandonment in Compose
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = CameraSelector.Builder()
                                        .requireLensFacing(lensFacing)
                                        .build()

                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        onRelease = { previewView ->
                            try {
                                val cameraProvider = ProcessCameraProvider.getInstance(previewView.context).get()
                                cameraProvider.unbindAll()
                            } catch (e: Exception) {
                                // ignore
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Video filter live preview overlay on top of camera!
                    VideoFilterOverlay(filterType = selectedFilter.name)
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = ReelPink,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Camera Permission Required",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ReelPink)
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
        } else {
            // Uploaded / Recorded Preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF140826), Color(0xFF380E54), Color(0xFF090610))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = ReelCyan,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedVideoUri != null) "Video Ready from Gallery" else "Video Recorded Successfully!",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Filter applied: ${selectedFilter.displayName} ✨",
                        color = ReelGold,
                        fontSize = 13.sp
                    )
                }

                VideoFilterOverlay(filterType = selectedFilter.name)
            }
        }

        // Top Controls Bar (Close, Flip, Torch, Audio Tag)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Audio Selector pill in top center
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { showAudioDialog = true }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("open_audio_selector_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = ReelPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedAudioTitle.take(18) + if (selectedAudioTitle.length > 18) "..." else "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Right action buttons (Camera switch & Flash)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (mode == "CAMERA") {
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                    }

                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) ReelGold else Color.White
                        )
                    }
                }
            }
        }

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                    )
                )
                .padding(bottom = 16.dp)
        ) {
            // Video Filters Picker Bar
            FilterPickerBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // If video is recorded or picked, show Caption & Hashtag builder
            if (mode == "UPLOADED_PREVIEW") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        label = { Text("Reel Caption • विवरण") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelPink,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("caption_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = hashtagsText,
                        onValueChange = { hashtagsText = it },
                        label = { Text("Hashtags • हैशटैग्स") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelCyan,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hashtags_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Bottom Actions
            if (mode == "CAMERA") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Upload from Gallery button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                            .testTag("gallery_upload_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gallery", color = Color.White, fontSize = 11.sp)
                    }

                    // Recording Shutter Button with Pulsing Glow
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .clickable {
                                isRecording = !isRecording
                                if (!isRecording && recordSeconds > 0) {
                                    mode = "UPLOADED_PREVIEW"
                                }
                            }
                            .testTag("record_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isRecording) 32.dp else 56.dp)
                                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                                .background(ReelPink)
                        )
                    }

                    // Extract Audio Quick Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                extractAudioLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                            .testTag("extract_audio_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ReelPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Extract Audio",
                                tint = ReelCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Extract Audio", color = ReelCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isRecording) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recording: 00:${if (recordSeconds < 10) "0$recordSeconds" else recordSeconds}",
                        color = ReelPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                // Publish & Retake Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = {
                            mode = "CAMERA"
                            selectedVideoUri = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retake • दोबारा लें", color = Color.White)
                    }

                    Button(
                        onClick = {
                            isPublishing = true
                            scope.launch {
                                delay(1200)
                                isPublishing = false
                                val newReel = ReelEntity(
                                    creatorName = currentUserName,
                                    creatorHandle = currentUserHandle,
                                    caption = captionText.trim(),
                                    hashtags = hashtagsText.trim(),
                                    videoUri = selectedVideoUri?.toString() ?: "sample://user_recorded_${System.currentTimeMillis()}",
                                    audioTitle = selectedAudioTitle,
                                    audioArtist = selectedAudioArtist,
                                    filterType = selectedFilter.name,
                                    isUserUpload = true,
                                    likesCount = 0,
                                    commentsCount = 0,
                                    sharesCount = 0,
                                    viewsCount = 1
                                )
                                onPublishReel(newReel)
                                Toast.makeText(context, "🎉 Reel Published Successfully!", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("publish_reel_button")
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Reel • पोस्ट करें", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Audio & Sound Track Selector Dialog (Trending Tracks + Extract Audio)
    if (showAudioDialog) {
        val presetTracks = listOf(
            AudioTrack("1", "Bollywood Dhamaka 2026", "DJ Chetas Beats", "0:30"),
            AudioTrack("2", "Desi Hip Hop • Mumbai Flow", "Gully Beats", "0:45"),
            AudioTrack("3", "Dil Diyan Gallan (Lo-Fi)", "Acoustic Cafe", "0:30"),
            AudioTrack("4", "Cyberwave Neon Night", "RetroWave Studios", "0:40"),
            AudioTrack("5", "Never Quit • Phonk Boost", "Gym Records", "0:35")
        )

        AlertDialog(
            onDismissRequest = { showAudioDialog = false },
            title = {
                Text(
                    text = "Select Audio • ऑडियो चुनें",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    // Extract Audio from Device Video Option
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ReelPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAudioDialog = false
                                extractAudioLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = ReelCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Extract Audio from Video",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Use background sound from your video",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Trending Soundtracks",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReelGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(presetTracks) { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAudioTitle = track.title
                                        selectedAudioArtist = track.artist
                                        showAudioDialog = false
                                        Toast.makeText(context, "Selected: ${track.title}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = null,
                                    tint = ReelPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(track.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(track.artist, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                Text(track.duration, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                            Divider(color = Color.White.copy(alpha = 0.08f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAudioDialog = false }) {
                    Text("Close", color = ReelPink)
                }
            },
            containerColor = ReelSurface
        )
    }
}
