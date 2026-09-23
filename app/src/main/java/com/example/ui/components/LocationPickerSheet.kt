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
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelSurfaceVariant

data class LocationItem(
    val title: String,
    val subtitle: String,
    val category: String
)

val INDIAN_LOCATIONS_DATABASE = listOf(
    // Featured / User Region & viral locations
    LocationItem("अपना फखरपुर बहराइच", "उत्तर प्रदेश • Fakharpur, Bahraich", "Popular"),
    LocationItem("फखरपुर, बहराइच", "Fakharpur, Bahraich, Uttar Pradesh", "UP"),
    LocationItem("शाह कटरा, जैदपुर", "बारा बंकी, उत्तर प्रदेश • Zaidpur, Bara Banki", "UP"),
    LocationItem("बहराइच शहर", "Bahraich City, Uttar Pradesh", "UP"),
    LocationItem("हजरतगंज, लखनऊ", "Hazratganj, Lucknow, Uttar Pradesh", "UP"),
    LocationItem("गोमती नगर, लखनऊ", "Gomti Nagar, Lucknow, UP", "UP"),
    LocationItem("राम जन्मभूमि मंदिर, अयोध्या", "Ram Mandir, Ayodhya, Uttar Pradesh", "Heritage"),
    LocationItem("दशाश्वमेध घाट, वाराणसी", "Dashashwamedh Ghat, Kashi, Varanasi", "Heritage"),
    LocationItem("अस्सी घाट, वाराणसी", "Assi Ghat, Varanasi, Uttar Pradesh", "Heritage"),
    LocationItem("ताज महल, आगरा", "Taj Mahal, Agra, Uttar Pradesh", "Heritage"),
    LocationItem("कानपुर सेंट्रल", "Kanpur, Uttar Pradesh", "UP"),
    LocationItem("इलाहाबाद संगम, प्रयागराज", "Sangam, Prayagraj, Uttar Pradesh", "Heritage"),
    LocationItem("गोरखनाथ मंदिर, गोरखपुर", "Gorakhpur, Uttar Pradesh", "UP"),

    // Delhi NCR & North
    LocationItem("इंडिया गेट, नई दिल्ली", "India Gate, New Delhi", "Metro"),
    LocationItem("कनॉट प्लेस, नई दिल्ली", "Connaught Place (CP), Delhi", "Metro"),
    LocationItem("लाल किला, पुरानी दिल्ली", "Red Fort, Old Delhi", "Heritage"),
    LocationItem("हौज़ खास विलेज, दिल्ली", "Hauz Khas, New Delhi", "Popular"),
    LocationItem("गोल्डन टेम्पल, अमृतसर", "Golden Temple, Amritsar, Punjab", "Heritage"),
    LocationItem("डल झील, श्रीनगर", "Dal Lake, Srinagar, Kashmir", "Heritage"),
    LocationItem("माल रोड, शिमला", "Mall Road, Shimla, Himachal Pradesh", "Popular"),
    LocationItem("ऋषिकेश, लक्ष्मण झूला", "Rishikesh, Uttarakhand", "Heritage"),
    LocationItem("सुखना लेक, चंडीगढ़", "Sukhna Lake, Chandigarh", "Popular"),

    // Mumbai & West
    LocationItem("मरीन ड्राइव, मुंबई", "Marine Drive, Mumbai, Maharashtra", "Metro"),
    LocationItem("गेटवे ऑफ इंडिया, मुंबई", "Gateway of India, Colaba, Mumbai", "Heritage"),
    LocationItem("बांद्रा बैंडस्टैंड, मुंबई", "Bandra, Mumbai", "Popular"),
    LocationItem("जुहू बीच, मुंबई", "Juhu Beach, Mumbai", "Popular"),
    LocationItem("हवा महल, जयपुर", "Hawa Mahal, Jaipur, Rajasthan", "Heritage"),
    LocationItem("जल महल, जयपुर", "Pink City, Jaipur, Rajasthan", "Heritage"),
    LocationItem("बाघा बीच, गोवा", "Baga Beach, North Goa", "Popular"),
    LocationItem("साबरमती रिवरफ्रंट, अहमदाबाद", "Sabarmati, Ahmedabad, Gujarat", "Metro"),
    LocationItem("शनिवार वाड़ा, पुणे", "Pune, Maharashtra", "Popular"),

    // East & South
    LocationItem("हावड़ा ब्रिज, कोलकाता", "Howrah Bridge, Kolkata, West Bengal", "Metro"),
    LocationItem("पार्क स्ट्रीट, कोलकाता", "Park Street, Kolkata", "Popular"),
    LocationItem("चारमीनार, हैदराबाद", "Charminar, Old City, Hyderabad", "Heritage"),
    LocationItem("हाईटेक सिटी, हैदराबाद", "HITEC City, Hyderabad, Telangana", "Metro"),
    LocationItem("एमजी रोड, बेंगलुरु", "MG Road, Bengaluru, Karnataka", "Metro"),
    LocationItem("मरीना बीच, चेन्नई", "Marina Beach, Chennai, Tamil Nadu", "Metro"),
    LocationItem("मीनाक्षी मंदिर, मदुरै", "Madurai, Tamil Nadu", "Heritage"),
    LocationItem("फोर्ट कोच्चि, केरल", "Fort Kochi, Kerala", "Popular"),
    LocationItem("गांधी मैदान, पटना", "Patna, Bihar", "Popular"),
    LocationItem("महाबोधि मंदिर, बोधगया", "Bodh Gaya, Bihar", "Heritage")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    currentLocation: String,
    onSelectLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Popular", "UP", "Heritage", "Metro")

    val filteredList = remember(searchQuery, selectedCategory) {
        val query = searchQuery.trim().lowercase()
        INDIAN_LOCATIONS_DATABASE.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesQuery = query.isEmpty() ||
                    item.title.lowercase().contains(query) ||
                    item.subtitle.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF130D22),
        modifier = Modifier.testTag("location_picker_sheet")
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
                            .background(ReelCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ReelCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Add Location • लोकेशन जोड़ें",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "इंडिया या अनलिमिटेड कोई भी लोकेशन सर्च करें",
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

            // Search Bar ("एक अच्छा डालें सारे नाम आ जाए")
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search location (e.g. फखरपुर, बहराइच, लखनऊ, मुंबई...)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = ReelCyan)
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
                    focusedIndicatorColor = ReelCyan,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ReelPink else Color.White.copy(alpha = 0.08f))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (cat) {
                                "All" -> "All • सभी"
                                "Popular" -> "🔥 Popular"
                                "UP" -> "📍 UP / बहराइच"
                                "Heritage" -> "🛕 Heritage"
                                "Metro" -> "🏙️ Metro"
                                else -> cat
                            },
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Unlimited Custom Location Option (When user enters anything custom)
            if (searchQuery.trim().isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ReelCyan.copy(alpha = 0.15f))
                        .border(1.dp, ReelCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            onSelectLocation(searchQuery.trim())
                            onDismiss()
                        }
                        .padding(12.dp)
                        .testTag("custom_location_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddLocation,
                            contentDescription = null,
                            tint = ReelCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Use custom location: \"${searchQuery.trim()}\"",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "अनलिमिटेड कस्टम लोकेशन जोड़ें (टैप करें)",
                                color = ReelCyan,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Locations List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredList) { location ->
                    val isSelected = currentLocation == location.title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ReelPink.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                onSelectLocation(location.title)
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
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = if (isSelected) ReelPink else ReelCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = location.title,
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = location.subtitle,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ReelPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
