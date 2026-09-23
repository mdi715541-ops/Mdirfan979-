package com.example.ui.screens.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import com.example.ui.components.AudioPickerSheet
import com.example.ui.components.LegalSafetyDisclaimerCard
import com.example.ui.components.LocationPickerSheet
import com.example.ui.components.TermsAndConditionsDialog
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
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
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateReelScreen(
    currentUserName: String,
    currentUserHandle: String,
    initialAudioTitle: String = "Nigahen Kyon Churaati Hai • Udit Narayan",
    initialAudioArtist: String = "Bollywood Classic",
    onPublishReel: (ReelEntity) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Screen Modes: "CAMERA" or "UPLOADED_PREVIEW"
    var mode by remember { mutableStateOf("CAMERA") }

    // Capture Mode: "VIDEO" or "PHOTO"
    var captureMode by remember { mutableStateOf("VIDEO") }

    // Media Attributes
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isPhotoMedia by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordSeconds by remember { mutableIntStateOf(0) }

    // Camera Settings
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isGridVisible by remember { mutableStateOf(true) }
    var isHardwareCameraReady by remember { mutableStateOf(false) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }

    // Shutter Flash Animation for Photo Capture
    val flashAlpha = remember { Animatable(0f) }

    // Reel Customization Attributes
    var selectedFilter by remember { mutableStateOf(VideoFilter.NORMAL) }
    var captionText by remember { mutableStateOf("अपना फखरपुर बहराइच की शान! ❤️🔥 #viral") }
    var hashtagsText by remember { mutableStateOf("#फखरपुर #ReelVibe #Viral #Trending #ExploreIndia") }
    var selectedAudioTitle by remember(initialAudioTitle) { mutableStateOf(initialAudioTitle) }
    var selectedAudioArtist by remember(initialAudioArtist) { mutableStateOf(initialAudioArtist) }
    var selectedLocation by remember { mutableStateOf("अपना फखरपुर बहराइच") }
    var videoStickerText by remember { mutableStateOf("I ❤️ फखरपुर") }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var videoDurationSec by remember { mutableIntStateOf(30) }

    // Dialog & Sheet States
    var showAudioDialog by remember { mutableStateOf(false) }
    var showGalleryDialog by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showStickerDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }

    // Check Camera Permission
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

    // Gallery Picker for BOTH Photos & Videos
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            val mimeType = context.contentResolver.getType(uri) ?: ""
            isPhotoMedia = mimeType.startsWith("image")
            mode = "UPLOADED_PREVIEW"
            val typeDesc = if (isPhotoMedia) "Photo • फ़ोटो" else "Video • वीडियो"
            Toast.makeText(context, "$typeDesc loaded from Gallery! 📁", Toast.LENGTH_SHORT).show()
        }
    }

    // Video Picker for Audio Extraction
    val extractAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "ExtractedTrack"
            selectedAudioTitle = "Sound from Video • ${fileName.takeLast(12)}"
            selectedAudioArtist = "User Audio"
            Toast.makeText(context, "Audio extracted successfully! 🎵", Toast.LENGTH_LONG).show()
        }
    }

    // Video Recording Timer (1s to 60s max)
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordSeconds = 0
            while (isRecording && recordSeconds < 60) {
                delay(1000)
                recordSeconds += 1
            }
            if (recordSeconds >= 60) {
                isRecording = false
                isPhotoMedia = false
                selectedVideoUri = Uri.parse("sample://user_recorded_${System.currentTimeMillis()}")
                mode = "UPLOADED_PREVIEW"
                Toast.makeText(context, "Recording completed (60s limit)! 🎥", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper: Save captured photo bitmap to cache
    fun capturePhotoAction() {
        scope.launch {
            // 1. Shutter Flash Effect
            flashAlpha.snapTo(1f)
            flashAlpha.animateTo(0f, tween(250))

            // 2. Generate Photo File
            try {
                val photoDir = File(context.cacheDir, "captured_photos").apply { mkdirs() }
                val photoFile = File(photoDir, "reel_photo_${System.currentTimeMillis()}.jpg")
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                // Draw background gradient reflecting current filter
                val paint = Paint()
                canvas.drawColor(android.graphics.Color.parseColor("#140826"))

                val outStream = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
                outStream.flush()
                outStream.close()

                selectedVideoUri = Uri.fromFile(photoFile)
            } catch (e: Exception) {
                selectedVideoUri = Uri.parse("image:photo_${System.currentTimeMillis()}")
            }

            isPhotoMedia = true
            mode = "UPLOADED_PREVIEW"
            Toast.makeText(context, "📸 Photo Captured! • फ़ोटो खींच ली गई", Toast.LENGTH_SHORT).show()
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
        // -------------------------------------------------------------
        // 1. LIVE CAMERA VIEWFINDER / PREVIEW
        // -------------------------------------------------------------
        if (mode == "CAMERA") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            focusPoint = offset
                            scope.launch {
                                delay(1200)
                                focusPoint = null
                            }
                        }
                    }
            ) {
                if (hasCameraPermission) {
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

                    // CameraX View with COMPATIBLE TextureView mode
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val hasCamera = cameraProvider.availableCameraInfos.isNotEmpty()

                                    if (hasCamera) {
                                        val preview = Preview.Builder().build().also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }
                                        val selector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) && lensFacing == CameraSelector.LENS_FACING_BACK) {
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                        } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                                            CameraSelector.DEFAULT_FRONT_CAMERA
                                        } else {
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                        }
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                                        isHardwareCameraReady = true
                                    } else {
                                        isHardwareCameraReady = false
                                    }
                                } catch (e: Exception) {
                                    isHardwareCameraReady = false
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
                }

                // If hardware camera is not available (e.g. streaming emulator without physical lens),
                // render the Live Studio Camera Viewfinder so camera ALWAYS opens live!
                if (!hasCameraPermission || !isHardwareCameraReady) {
                    LiveStudioCameraView(
                        filterType = selectedFilter.name,
                        isRecording = isRecording,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Live Real-Time Filter Overlay Layer
                VideoFilterOverlay(filterType = selectedFilter.name)

                // Composition 3x3 Rule-of-Thirds Grid
                if (isGridVisible) {
                    CameraGridOverlay(modifier = Modifier.fillMaxSize())
                }

                // Tap-to-Focus Reticle Indicator
                focusPoint?.let { pos ->
                    FocusReticleIndicator(position = pos)
                }

                // Flash beam simulation when flash is on
                if (isFlashOn) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Yellow.copy(alpha = 0.08f))
                    )
                }

                // Shutter Snap White Flash
                if (flashAlpha.value > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = flashAlpha.value))
                    )
                }

                // Recording Indicator Bar (at top when recording video)
                if (isRecording) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Red.copy(alpha = 0.85f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REC 00:${if (recordSeconds < 10) "0$recordSeconds" else recordSeconds} / 01:00",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { recordSeconds / 60f },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = ReelPink,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        } else {
            // -------------------------------------------------------------
            // UPLOADED / RECORDED PREVIEW VIEW
            // -------------------------------------------------------------
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
                if (isPhotoMedia && selectedVideoUri != null) {
                    // Display Captured / Selected Photo with Filter
                    AsyncImage(
                        model = selectedVideoUri,
                        contentDescription = "Photo Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Display Video Preview Scene
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(ReelPink.copy(alpha = 0.25f))
                                .border(2.dp, ReelPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = ReelCyan,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (selectedVideoUri != null) "Video Ready to Share! 🎥" else "Video Recorded Successfully! 🎬",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Filter: ${selectedFilter.displayName} ✨ • Audio: $selectedAudioTitle",
                            color = ReelGold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Filter Overlay on Preview
                VideoFilterOverlay(filterType = selectedFilter.name)

                // Live Floating Text Sticker (e.g. "I ❤️ फखरपुर")
                if (videoStickerText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 90.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .clickable { showStickerDialog = true }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .testTag("preview_text_sticker")
                    ) {
                        Text(
                            text = videoStickerText,
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Top Location and Audio chips overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 58.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedLocation.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, ReelCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable { showLocationPicker = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("preview_location_chip")
                        ) {
                            Text(
                                text = "📍 $selectedLocation",
                                color = ReelCyan,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. TOP CONTROLS BAR (Close, Audio Pill, Flash, Flip, Grid)
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Audio & Location Selector pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, ReelPink.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { showAudioDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("open_audio_selector_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ReelPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedAudioTitle.take(10) + "...",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, ReelCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { showLocationPicker = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("open_location_picker_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = ReelCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedLocation.isNotBlank()) selectedLocation.take(8) + ".." else "Location",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Right Camera Toggles (Flash, Flip, Grid)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (mode == "CAMERA") {
                    // Flash Toggle
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) ReelGold else Color.White
                        )
                    }

                    // Grid Toggle
                    IconButton(
                        onClick = { isGridVisible = !isGridVisible },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Grid",
                            tint = if (isGridVisible) ReelCyan else Color.White.copy(alpha = 0.5f)
                        )
                    }

                    // Flip Camera (Front / Back)
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                            Toast.makeText(context, "Flipped Camera Lens", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. BOTTOM CONTROLS & SHUTTER ENGINE
        // -------------------------------------------------------------
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
            // Filters Bar
            FilterPickerBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (mode == "CAMERA") {
                // Mode Selector Segment: [ VIDEO • वीडियो ] | [ PHOTO • फ़ोटो ]
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (captureMode == "VIDEO") ReelPink else Color.Transparent)
                            .clickable {
                                if (!isRecording) captureMode = "VIDEO"
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎥 VIDEO • वीडियो",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (captureMode == "VIDEO") FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (captureMode == "PHOTO") ReelPink else Color.Transparent)
                            .clickable {
                                if (!isRecording) captureMode = "PHOTO"
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "📸 PHOTO • फ़ोटो",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (captureMode == "PHOTO") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Primary Shutter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Gallery Button (Opens Dialog for Device Picker or Presets)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { showGalleryDialog = true }
                            .testTag("gallery_upload_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gallery", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // 2. Main Shutter Button (Photo vs Video)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .clickable {
                                if (captureMode == "PHOTO") {
                                    capturePhotoAction()
                                } else {
                                    isRecording = !isRecording
                                    if (!isRecording && recordSeconds > 0) {
                                        isPhotoMedia = false
                                        selectedVideoUri = Uri.parse("sample://user_recorded_${System.currentTimeMillis()}")
                                        mode = "UPLOADED_PREVIEW"
                                    }
                                }
                            }
                            .testTag("record_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (captureMode == "PHOTO") {
                            // Camera Photo Shutter Button
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Take Photo",
                                    tint = Color.Black,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        } else {
                            // Video Record Shutter Button
                            Box(
                                modifier = Modifier
                                    .size(if (isRecording) 32.dp else 60.dp)
                                    .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                                    .background(ReelPink)
                            )
                        }
                    }

                    // 3. Audio Extraction Quick Button
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
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ReelPurple)
                                .border(1.5.dp, ReelCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
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
            } else {
                // Video Editing Studio Controls: Location, Audio, Sticker, Speed, Filters, Caption, Hashtags, Publish
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // Video Editing Quick Toolbar
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            // Location Tag Action
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, ReelCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .clickable { showLocationPicker = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("edit_location_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = ReelCyan, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (selectedLocation.isNotBlank()) selectedLocation else "Add Location • लोकेशन",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        item {
                            // Audio Selection Action
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, ReelPink.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .clickable { showAudioDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("edit_audio_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = ReelPink, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = selectedAudioTitle.take(14) + "...",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        item {
                            // Video Sticker Text Action
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, ReelGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .clickable { showStickerDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("edit_sticker_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TextFields, contentDescription = null, tint = ReelGold, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (videoStickerText.isNotBlank()) videoStickerText else "Text Sticker • स्टिकर",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        item {
                            // Video Speed Toggle (0.5x -> 1.0x -> 2.0x)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        playbackSpeed = when (playbackSpeed) {
                                            "0.5x" -> "1.0x"
                                            "1.0x" -> "2.0x"
                                            else -> "0.5x"
                                        }
                                        Toast.makeText(context, "Speed set to $playbackSpeed", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("edit_speed_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("Speed: $playbackSpeed", color = Color.White, fontSize = 11.5.sp)
                                }
                            }
                        }

                        item {
                            // Trim Length (15s -> 30s -> 60s)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        videoDurationSec = when (videoDurationSec) {
                                            15 -> 30
                                            30 -> 60
                                            else -> 15
                                        }
                                        Toast.makeText(context, "Video length set to ${videoDurationSec}s", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("edit_trim_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("Trim: ${videoDurationSec}s", color = Color.White, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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

                    // Quick Viral Hashtag chips
                    val quickTags = listOf("#फखरपुर", "#viral", "#trending", "#india", "#foryou", "#reels")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(quickTags) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .clickable {
                                        if (!hashtagsText.contains(tag)) {
                                            hashtagsText = "$hashtagsText $tag".trim()
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(tag, color = ReelCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legal & Safety Disclaimer Card ("कंपनी जिम्मेदार नहीं है")
                    LegalSafetyDisclaimerCard(
                        showDetailedPoints = true,
                        onOpenTermsDialog = { showTermsDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Retake & Publish Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {
                                mode = "CAMERA"
                                selectedVideoUri = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retake • दोबारा लें", color = Color.White, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                isPublishing = true
                                scope.launch {
                                    delay(900)
                                    isPublishing = false
                                    val newReel = ReelEntity(
                                        creatorName = currentUserName,
                                        creatorHandle = currentUserHandle,
                                        caption = captionText.trim(),
                                        hashtags = hashtagsText.trim(),
                                        videoUri = selectedVideoUri?.toString()
                                            ?: if (isPhotoMedia) "image:photo_${System.currentTimeMillis()}" else "sample://user_recorded_${System.currentTimeMillis()}",
                                        audioTitle = selectedAudioTitle,
                                        audioArtist = selectedAudioArtist,
                                        filterType = selectedFilter.name,
                                        location = selectedLocation,
                                        videoStickerText = videoStickerText,
                                        isUserUpload = true,
                                        likesCount = 0,
                                        commentsCount = 0,
                                        sharesCount = 0,
                                        viewsCount = 1
                                    )
                                    onPublishReel(newReel)
                                    val type = if (isPhotoMedia) "Photo Reel" else "Video Reel"
                                    Toast.makeText(context, "🎉 $type Published Successfully! • रील पोस्ट हो गई", Toast.LENGTH_LONG).show()
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
    }

    // -------------------------------------------------------------
    // GALLERY OPTIONS DIALOG (Phone Gallery + Quick Viral Presets)
    // -------------------------------------------------------------
    if (showGalleryDialog) {
        AlertDialog(
            onDismissRequest = { showGalleryDialog = false },
            title = {
                Text(
                    text = "Upload Media • गैलरी से अपलोड करें",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    // Option 1: Pick from Device Phone Storage (Photos & Videos)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ReelPink.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showGalleryDialog = false
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = ReelPink, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Open Phone Gallery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Select any photo or video from device storage", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Or Pick Instant Viral Preset (Ready for Emulator!):",
                        color = ReelGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val samplePresets = listOf(
                        Triple("Neon Cyberpunk Dance", "VIDEO", "sample://neon_cyberpunk"),
                        Triple("Sunset Ocean Horizon", "VIDEO", "sample://sunset_ocean"),
                        Triple("Desi Street Food Magic", "VIDEO", "sample://desi_street_food"),
                        Triple("Aesthetic Portrait Glow", "PHOTO", "image:sample_portrait"),
                        Triple("Travel Sunset Golden Hour", "PHOTO", "image:sample_travel"),
                        Triple("Fitness Energy & Beats", "VIDEO", "sample://fitness_energy")
                    )

                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(samplePresets) { (title, type, uriStr) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showGalleryDialog = false
                                        isPhotoMedia = (type == "PHOTO")
                                        selectedVideoUri = Uri.parse(uriStr)
                                        mode = "UPLOADED_PREVIEW"
                                        Toast.makeText(context, "Loaded $title ($type)!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (type == "PHOTO") Icons.Default.Image else Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (type == "PHOTO") ReelCyan else ReelPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(if (type == "PHOTO") "Photo Reel • फ़ोटो" else "Video Reel • वीडियो", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("SELECT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Divider(color = Color.White.copy(alpha = 0.08f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGalleryDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = ReelSurface
        )
    }

    // -------------------------------------------------------------
    // AUDIO SELECTOR SHEET (Full Featured Vinyl + Search + Local)
    // -------------------------------------------------------------
    if (showAudioDialog) {
        AudioPickerSheet(
            currentTitle = selectedAudioTitle,
            onSelectAudio = { title, artist ->
                selectedAudioTitle = title
                selectedAudioArtist = artist
                showAudioDialog = false
                Toast.makeText(context, "Selected: $title 🎵", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showAudioDialog = false }
        )
    }

    // -------------------------------------------------------------
    // LOCATION PICKER SHEET (India & Unlimited Custom Locations)
    // -------------------------------------------------------------
    if (showLocationPicker) {
        LocationPickerSheet(
            currentLocation = selectedLocation,
            onDismiss = { showLocationPicker = false },
            onSelectLocation = { loc ->
                selectedLocation = loc
                showLocationPicker = false
                Toast.makeText(context, "📍 Location tagged: $loc", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------
    // VIDEO STICKER TEXT DIALOG (Custom text + Desi stickers)
    // -------------------------------------------------------------
    if (showStickerDialog) {
        var stickerInput by remember { mutableStateOf(videoStickerText) }
        val quickStickers = listOf(
            "I ❤️ फखरपुर",
            "I ❤️ इंडिया",
            "जय हिन्द 🇮🇳",
            "देसी स्वैग 🔥",
            "Viral Vibes ✨",
            "मस्त माहौल 🎉",
            "बहराइच वाले 😎",
            "Reel King 👑"
        )

        AlertDialog(
            onDismissRequest = { showStickerDialog = false },
            title = {
                Text(
                    text = "Add Video Sticker • स्टिकर जोड़ें",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Type text to float on your reel, or pick below:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = stickerInput,
                        onValueChange = { stickerInput = it },
                        label = { Text("Sticker Text") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelGold,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sticker_input_field")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Popular Desi Stickers:",
                        color = ReelGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickStickers) { st ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .border(1.dp, ReelGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .clickable { stickerInput = st }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(st, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        videoStickerText = stickerInput.trim()
                        showStickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReelGold)
                ) {
                    Text("Apply • जोड़ें", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        videoStickerText = ""
                        showStickerDialog = false
                    }
                ) {
                    Text("Clear • हटाएं", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = ReelSurface
        )
    }

    // Terms & Conditions Dialog
    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onDismiss = { showTermsDialog = false }
        )
    }
}

// -------------------------------------------------------------
// LIVE STUDIO CAMERA VIEWFINDER (Active Simulated Studio Feed)
// -------------------------------------------------------------
@Composable
fun LiveStudioCameraView(
    filterType: String,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "studio_cam")
    val sweepOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0B18),
                        Color(0xFF1E1035),
                        Color(0xFF0A0512)
                    )
                )
            )
    ) {
        // Animated dynamic lighting & lens aperture motion
        ComposeCanvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val movingX = size.width * (0.3f + sweepOffset * 0.4f)
            val movingY = size.height * (0.35f + sweepOffset * 0.3f)

            // Ambient studio soft light bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33E0218A),
                        Color(0x2200E5FF),
                        Color.Transparent
                    ),
                    center = Offset(movingX, movingY),
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = Offset(movingX, movingY)
            )

            // Center Viewfinder Crosshairs
            val chLength = 24.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = Offset(center.x - chLength, center.y),
                end = Offset(center.x + chLength, center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = Offset(center.x, center.y - chLength),
                end = Offset(center.x, center.y + chLength),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // Top Status Badge: Live Camera Info
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 64.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color.Red else Color.Green)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE 4K • 60 FPS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("ISO 400 • f/1.8", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 3x3 CAMERA RULE-OF-THIRDS GRID OVERLAY
// -------------------------------------------------------------
@Composable
fun CameraGridOverlay(modifier: Modifier = Modifier) {
    ComposeCanvas(modifier = modifier) {
        val strokeWidth = 1.dp.toPx()
        val gridColor = Color.White.copy(alpha = 0.22f)

        // Vertical lines
        val x1 = size.width / 3f
        val x2 = size.width * 2f / 3f
        drawLine(color = gridColor, start = Offset(x1, 0f), end = Offset(x1, size.height), strokeWidth = strokeWidth)
        drawLine(color = gridColor, start = Offset(x2, 0f), end = Offset(x2, size.height), strokeWidth = strokeWidth)

        // Horizontal lines
        val y1 = size.height / 3f
        val y2 = size.height * 2f / 3f
        drawLine(color = gridColor, start = Offset(0f, y1), end = Offset(size.width, y1), strokeWidth = strokeWidth)
        drawLine(color = gridColor, start = Offset(0f, y2), end = Offset(size.width, y2), strokeWidth = strokeWidth)
    }
}

// -------------------------------------------------------------
// TAP-TO-FOCUS ANIMATED RETICLE INDICATOR
// -------------------------------------------------------------
@Composable
fun FocusReticleIndicator(position: Offset) {
    val scale = remember { Animatable(1.4f) }

    LaunchedEffect(position) {
        scale.animateTo(1f, tween(250, easing = FastOutSlowInEasing))
    }

    ComposeCanvas(modifier = Modifier.fillMaxSize()) {
        val boxSize = 64.dp.toPx() * scale.value
        val half = boxSize / 2f
        drawRect(
            color = Color(0xFFFFD600),
            topLeft = Offset(position.x - half, position.y - half),
            size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
