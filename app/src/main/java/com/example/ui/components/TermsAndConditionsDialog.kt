package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun TermsAndConditionsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .testTag("terms_and_conditions_dialog"),
        containerColor = Color(0xFF100A16),
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5252).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "नियम, शर्तें व कानूनी सुरक्षा",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Terms of Service & Legal Safe Zone",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Primary Mandatory Disclaimer
                LegalSafetyDisclaimerCard(
                    showDetailedPoints = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Individual Liability
                TermsSection(
                    title = "1. व्यक्तिगत कानूनी दायित्व (Individual Responsibility)",
                    icon = Icons.Default.Policy,
                    content = "ReelVibe प्लेटफॉर्म पर वीडियो, ऑडियो, स्टेटस अथवा कमेंट साझा करते समय यूजर यह स्वीकार करता है कि उसकी पोस्ट की गई किसी भी सामग्री की संपूर्ण जिम्मेदारी केवल उसकी स्वयं की होगी। किसी भी प्रकार की आपत्तिजनक, भ्रामक अथवा कानून विरोधी गतिविधि के लिए कंपनी किसी भी रूप में उत्तरदायी नहीं होगी।"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 2: Prohibited Content & Zero Tolerance
                TermsSection(
                    title = "2. सख्त कम्युनिटी गाइडलाइंस (Community Standards)",
                    icon = Icons.Default.Shield,
                    content = "गाली-गलौज, अभद्र भाषा, अश्लीलता, नफ़रती बयान, कॉपीराइट उल्लंघन या मानहानिकारक पोस्ट पूर्णतः प्रतिबंधित हैं। ऐसा करने वाले यूजर को मॉडरेटर कंट्रोल ज़ोन द्वारा तुरंत ब्लैकलिस्ट/बैन कर दिया जाएगा।"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 3: Safe Zone & Immediate Take-Down
                TermsSection(
                    title = "3. रिपोर्टिंग व सामग्री हटाना (Takedown Policy)",
                    icon = Icons.Default.Security,
                    content = "यदि आपको कोई अनुचित रील या कमेंट दिखता है, तो 3-डॉट मेन्यू से तुरंत 'रिपोर्ट रील' अथवा 'कमेंट डिलीट व ब्लॉक' का उपयोग करें। हमारी एडमिन टीम तुरंत जांच कर अनुचित सामग्री को हटा देगी।"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("मैं स्वीकार करता हूँ (I Understand & Accept)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }
        }
    )
}

@Composable
private fun TermsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ReelCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
