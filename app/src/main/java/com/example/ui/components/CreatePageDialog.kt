package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePageDialog(
    initialUpiId: String = "irafan@okaxis",
    onDismiss: () -> Unit,
    onPageCreated: (pageName: String, category: String, bio: String, upiId: String) -> Unit
) {
    val context = LocalContext.current
    var pageName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Digital Creator") }
    var pageBio by remember { mutableStateOf("") }
    var upiIdInput by remember { mutableStateOf(initialUpiId) }

    val categories = listOf(
        "Digital Creator",
        "Comedy & Entertainment",
        "Music & Dance",
        "Regional & News",
        "Business & Brand",
        "Tech & Gaming",
        "Food & Travel"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ReelPink, ReelPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "नया क्रिएटर पेज बनाएं (Create Page)",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "पेज बनाकर रील्स से सीधे पैसे कमाएं 💰",
                        color = ReelGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Earning Highlights Banner
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = ReelGold.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ReelGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = ReelGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "पेज मोनेटाइजेशन तुरंत सक्रिय होगा!",
                                color = ReelGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "रील्स विज्ञापनों और स्टार्स से होने वाली कमाई सीधे आपके UPI में आएगी।",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Page Name Input
                OutlinedTextField(
                    value = pageName,
                    onValueChange = { pageName = it },
                    label = { Text("Page Name • पेज का नाम *") },
                    placeholder = { Text("उदा. फखरपुर कॉमेडी रील्स, Irafan Official") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurfaceVariant,
                        unfocusedContainerColor = ReelSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelPink
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_page_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selection
                Text(
                    text = "Category • पेज की श्रेणी चुनें:",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ReelPink else ReelSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) ReelPink else Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = cat,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bio Description Input
                OutlinedTextField(
                    value = pageBio,
                    onValueChange = { pageBio = it },
                    label = { Text("Page Bio • पेज विवरण (ऑप्शनल)") },
                    placeholder = { Text("पेज के बारे में जानकारी लिखें...") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null, tint = ReelCyan)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurfaceVariant,
                        unfocusedContainerColor = ReelSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_page_bio_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // UPI ID for Earning Payouts
                OutlinedTextField(
                    value = upiIdInput,
                    onValueChange = { upiIdInput = it },
                    label = { Text("UPI ID for Payouts • कमाई बैंक/UPI खाता") },
                    leadingIcon = {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ReelGold)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurfaceVariant,
                        unfocusedContainerColor = ReelSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelGold
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_page_upi_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Normal Mode switch reminder
                Text(
                    text = "💡 नोट: आप प्रोफ़ाइल से कभी भी 'सामान्य प्रोफ़ाइल' में वापस बदल सकते हैं।",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.5.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pageName.isNotBlank()) {
                        onPageCreated(
                            pageName.trim(),
                            selectedCategory,
                            pageBio.trim(),
                            upiIdInput.trim()
                        )
                        Toast.makeText(
                            context,
                            "🎉 '$pageName' पेज सफलतापूर्वक बन गया! मोनेटाइजेशन सक्रिय है।",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, "कृपया पेज का नाम दर्ज करें", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_create_page_button")
            ) {
                Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Page • पेज बनाएं", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel • रद्द करें", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = ReelSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
