package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple

data class TrackItem(
    val title: String,
    val artist: String,
    val duration: String,
    val tag: String,
    val reelsCount: String
)

val INDIAN_AUDIO_COLLECTION = listOf(
    TrackItem("Nigahen Kyon Churaati Hai", "Udit Narayan • Bollywood Classic", "0:30", "Viral", "1.4M reels"),
    TrackItem("Kesariya (Dance Mix)", "Arijit Singh • Brahmastra", "0:45", "Trending", "2.8M reels"),
    TrackItem("Tum Hi Ho (Unplugged)", "Arijit Singh • Romantic Lo-Fi", "0:30", "Lo-Fi", "950K reels"),
    TrackItem("Chaleya (Remix)", "Anirudh Ravichander • Jawan", "0:30", "Trending", "3.1M reels"),
    TrackItem("Dil Diyan Gallan (Acoustic)", "Atif Aslam • Lo-Fi Collective", "0:30", "Lo-Fi", "1.1M reels"),
    TrackItem("Lut Gaye (Drop Beat)", "Jubin Nautiyal • T-Series", "0:45", "Trending", "1.7M reels"),
    TrackItem("Apna Bana Le", "Arijit Singh & Sachin-Jigar • Bhediya", "0:30", "Viral", "2.2M reels"),
    TrackItem("Kala Chashma (Club Bass)", "Badshah, Neha Kakkar", "0:30", "Desi Beats", "4.5M reels"),
    TrackItem("Raataan Lambiyan", "Jubin Nautiyal & Asees Kaur", "0:30", "Romantic", "1.8M reels"),
    TrackItem("Tere Vaaste", "Varun Jain, Sachin-Jigar", "0:30", "Viral", "2.9M reels"),
    TrackItem("Desi Swag फखरपुर Beat", "Dehat Folk & Dholak Beats", "0:30", "Desi Beats", "840K reels"),
    TrackItem("Bhojpuri Bass Tadka", "DJ Shashi Remix", "0:30", "Desi Beats", "620K reels"),
    TrackItem("Midnight Drive • Synthwave", "RetroWave Studios", "0:30", "Instrumental", "410K reels"),
    TrackItem("Unstoppable Bassline Boost", "GymPhonk Records", "0:30", "Instrumental", "530K reels")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPickerSheet(
    currentTitle: String,
    onSelectAudio: (title: String, artist: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    val tags = listOf("All", "Viral", "Trending", "Lo-Fi", "Desi Beats", "Romantic")

    val filteredList = remember(searchQuery, selectedTag) {
        val query = searchQuery.trim().lowercase()
        INDIAN_AUDIO_COLLECTION.filter { track ->
            val matchesTag = selectedTag == "All" || track.tag == selectedTag
            val matchesQuery = query.isEmpty() ||
                    track.title.lowercase().contains(query) ||
                    track.artist.lowercase().contains(query)
            matchesTag && matchesQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF130D22),
        modifier = Modifier.testTag("audio_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ReelPink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = null,
                            tint = ReelPink,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Add Audio • म्यूज़िक / ऑडियो चुनें",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "बॉलीवुड, देसी और अनलिमिटेड ऑडियो लाइब्रेरी",
                            color = ReelCyan,
                            fontSize = 11.5.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search songs, artists (Udit Narayan, Arijit, Desi...)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = ReelPink)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.06f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                    focusedIndicatorColor = ReelPink,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(tags) { tag ->
                    val isSelected = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ReelPink else Color.White.copy(alpha = 0.08f))
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (tag) {
                                "All" -> "All • सभी"
                                "Viral" -> "🔥 Viral"
                                "Trending" -> "⚡ Trending"
                                "Desi Beats" -> "🪘 देसी बीट्स"
                                else -> tag
                            },
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Unlimited Custom Audio Title (If searched query not found)
            if (searchQuery.trim().isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ReelPink.copy(alpha = 0.15f))
                        .border(1.dp, ReelPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            onSelectAudio(searchQuery.trim(), "Original Audio")
                            onDismiss()
                        }
                        .padding(12.dp)
                        .testTag("custom_audio_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ReelPink,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Use custom sound: \"${searchQuery.trim()}\"",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "कस्टम ऑडियो नाम सेट करें (टैप करें)",
                                color = ReelCyan,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Audio Track List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredList) { track ->
                    val isSelected = currentTitle.contains(track.title, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ReelPink.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                onSelectAudio(track.title, track.artist)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(ReelPurple, ReelPink)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = track.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${track.artist} • ${track.reelsCount}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ReelPink,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Use • लगाएं",
                                    color = ReelCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet shown when tapping the spinning Vinyl Album cover or Audio pill (matching user's video at 00:22).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDetailsSheet(
    audioTitle: String,
    audioArtist: String,
    onUseAudio: () -> Unit,
    onRemixAudio: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isAudioSaved by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF130D22),
        modifier = Modifier.testTag("audio_details_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header with Album Art & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E0854), ReelPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audioTitle,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = audioArtist,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "🎵 1.4M Reels made with this sound",
                        color = ReelCyan,
                        fontSize = 11.5.sp
                    )
                }

                IconButton(onClick = { isAudioSaved = !isAudioSaved }) {
                    Icon(
                        imageVector = if (isAudioSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Audio",
                        tint = if (isAudioSaved) ReelGold else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Options list (exact from user video at 00:22)
            // 1. Use as reference / Use Audio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .clickable {
                        onUseAudio()
                        onDismiss()
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = ReelPink)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Use this audio in your Reel",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "इस गाने पर अपनी रील बनाएं",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Remix and sequence
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .clickable {
                        onRemixAudio()
                        onDismiss()
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = ReelCyan)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Remix and sequence",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "रीमिक्स करें और साथ में वीडियो जोड़ें",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    onUseAudio()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Audio • रील बनाएं", fontWeight = FontWeight.Bold)
            }
        }
    }
}
