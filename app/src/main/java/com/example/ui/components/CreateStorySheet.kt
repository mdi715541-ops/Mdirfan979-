package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface

@Composable
fun CreateStorySheet(
    initialAudioTitle: String = "Nigahen Kyon Churaati Hai",
    initialAudioArtist: String = "Udit Narayan",
    onPostStory: (mediaType: String, mediaUri: String, audioTitle: String, audioArtist: String, caption: String, stickerText: String, location: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var mediaType by remember { mutableStateOf("PHOTO") } // "PHOTO" or "VIDEO"
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var audioTitle by remember { mutableStateOf(initialAudioTitle) }
    var audioArtist by remember { mutableStateOf(initialAudioArtist) }
    var captionText by remember { mutableStateOf("") }
    var stickerText by remember { mutableStateOf("I ❤️ फखरपुर") }
    var locationText by remember { mutableStateOf("अपना फखरपुर बहराइच") }
    var showAudioPicker by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Media Picker Launcher (Zero permissions modern Photo Picker)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri
            Toast.makeText(context, "Media selected for status! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    val presetStickers = listOf(
        "I ❤️ फखरपुर",
        "Morning Vibe ☀️",
        "देसी स्वैग 🔥",
        "Viral Reels ✨",
        "Good Vibes 🌸",
        "Mood 🎧",
        "जय हिन्द 🇮🇳"
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
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("create_story_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add to Status • स्टेटस लगाएं",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Done / Post button
                    Button(
                        onClick = {
                            val uriString = selectedMediaUri?.toString() ?: "sample://status_created"
                            onPostStory(
                                mediaType,
                                uriString,
                                audioTitle,
                                audioArtist,
                                captionText,
                                stickerText,
                                locationText
                            )
                            Toast.makeText(context, "Status added with music! 🎵✨ (स्टेटस लग गया)", Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ReelPink
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("post_story_button")
                    ) {
                        Text("Post", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Status Media Type Toggle: Photo vs Video
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ReelSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mediaType == "PHOTO") ReelPink else Color.Transparent)
                            .clickable { mediaType = "PHOTO" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Photo Status (फोटो)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mediaType == "VIDEO") ReelCyan else Color.Transparent)
                            .clickable { mediaType = "VIDEO" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = if (mediaType == "VIDEO") Color.Black else Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Video Status (वीडियो)", color = if (mediaType == "VIDEO") Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Select Gallery / Camera Media Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                if (mediaType == "PHOTO") listOf(Color(0xFF2A0845), Color(0xFF6441A5))
                                else listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable {
                            val mediaTypeFilter = if (mediaType == "PHOTO") {
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            } else {
                                ActivityResultContracts.PickVisualMedia.VideoOnly
                            }
                            mediaPickerLauncher.launch(PickVisualMediaRequest(mediaTypeFilter))
                        }
                        .testTag("pick_status_media_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedMediaUri != null) Icons.Default.AutoAwesome else Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = ReelGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedMediaUri != null) "✓ Media Loaded from Gallery (बदलने के लिए टैप करें)"
                            else "Tap to Choose ${if (mediaType == "PHOTO") "Photo" else "Video"} from Gallery",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Add Song / Music Section (गाना वाना लगाएं)
                Text(
                    text = "Song & Music • गाना वाना लगाएं 🎵",
                    color = ReelGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ReelSurface)
                        .border(1.dp, ReelPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { showAudioPicker = true }
                        .padding(14.dp)
                        .testTag("choose_status_music_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ReelPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = audioTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$audioArtist • Tap to change song",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ReelCyan, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Sticker / Floating Text (स्टिकर जोड़ें)
                Text(
                    text = "Floating Sticker • स्टिकर लगाएं ✨",
                    color = ReelCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(presetStickers) { preset ->
                        val isSelected = stickerText == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ReelPink else ReelSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ReelGold else Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { stickerText = preset }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Sticker / Text input
                OutlinedTextField(
                    value = stickerText,
                    onValueChange = { stickerText = it },
                    placeholder = { Text("Or write custom sticker text...", color = Color.White.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurface,
                        unfocusedContainerColor = ReelSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelCyan,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Caption Input
                Text(
                    text = "Caption • स्टेटस कैप्शन ✍️",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    placeholder = { Text("What's on your mind? (आज का मिजाज)", color = Color.White.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurface,
                        unfocusedContainerColor = ReelSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelPink,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Location Tag
                OutlinedTextField(
                    value = locationText,
                    onValueChange = { locationText = it },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ReelGold) },
                    placeholder = { Text("Tag Location", color = Color.White.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurface,
                        unfocusedContainerColor = ReelSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelGold,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Legal & Safety Disclaimer
                LegalSafetyDisclaimerCard(
                    showDetailedPoints = false,
                    onOpenTermsDialog = { showTermsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Share Action Button
                Button(
                    onClick = {
                        val uriString = selectedMediaUri?.toString() ?: "sample://status_created"
                        onPostStory(
                            mediaType,
                            uriString,
                            audioTitle,
                            audioArtist,
                            captionText,
                            stickerText,
                            locationText
                        )
                        Toast.makeText(context, "Status added with music! 🎵✨ (स्टेटस लग गया)", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("post_status_full_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReelPink
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share to Status (गाना लगाकर स्टेटस लगाएं) 🚀",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // Audio Picker Sheet Modal
            if (showAudioPicker) {
                AudioPickerSheet(
                    currentTitle = audioTitle,
                    onSelectAudio = { title, artist ->
                        audioTitle = title
                        audioArtist = artist
                        showAudioPicker = false
                        Toast.makeText(context, "Selected: $title 🎵", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showAudioPicker = false }
                )
            }

            // Terms & Conditions Dialog
            if (showTermsDialog) {
                TermsAndConditionsDialog(
                    onDismiss = { showTermsDialog = false }
                )
            }
        }
    }
}
