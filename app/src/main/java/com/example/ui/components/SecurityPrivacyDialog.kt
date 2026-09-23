package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.data.model.UserEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelGreen
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelSurfaceVariant

@Composable
fun SecurityPrivacyDialog(
    user: UserEntity,
    onUpdateSecurity: (UserEntity) -> Unit,
    onRequestDeleteAccount: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var is2FA by remember { mutableStateOf(user.is2FAEnabled) }
    var isAppLock by remember { mutableStateOf(user.isAppLockEnabled) }
    var isAntiHackShield by remember { mutableStateOf(user.isAntiHackShieldActive) }
    var isPrivateAccount by remember { mutableStateOf(user.isPrivateAccount) }
    var allowDownloads by remember { mutableStateOf(user.allowReelDownloads) }
    var autoFilterComments by remember { mutableStateOf(user.autoFilterComments) }
    var showTermsDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .testTag("security_privacy_dialog"),
        containerColor = Color(0xFF0F0B18),
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ReelCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ReelCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Security & Privacy",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "सुरक्षा, गोपनीयता एवं एंटी-हैक सुरक्षा",
                        color = ReelCyan,
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
                // Anti-Hack Active Shield Status Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF07241A), Color(0xFF0C3D2B))
                            )
                        )
                        .border(1.dp, ReelGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ReelGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Anti-Hack Shield: ACTIVE (100% Secure)",
                                color = ReelGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "आपका डेटा, लॉगिन टोकन व रील्स एंड-टू-एंड 256-बिट एन्क्रिप्टेड हैं। कोई भी आईडी हैक नहीं कर सकता।",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.5.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Security Controls (आईडी व अकाउंट सुरक्षा)
                Text(
                    text = "ACCOUNT & DATA SECURITY • आईडी सुरक्षा",
                    color = ReelGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 1. Two-Factor Authentication (2FA)
                SecurityToggleRow(
                    title = "Two-Factor Auth (2FA)",
                    description = "लॉगिन पर OTP वेरिफिकेशन अनिवार्य रखें (हैक प्रूफ)",
                    icon = Icons.Default.VpnKey,
                    checked = is2FA,
                    onCheckedChange = { is2FA = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Anti-Hack Session Shield
                SecurityToggleRow(
                    title = "Anti-Hack Session Shield",
                    description = "अज्ञात डिवाइस या अनधिकृत एक्सेस को तुरंत ब्लॉक करें",
                    icon = Icons.Default.Security,
                    checked = isAntiHackShield,
                    onCheckedChange = { isAntiHackShield = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Biometric / PIN App Lock
                SecurityToggleRow(
                    title = "Biometric & PIN Lock",
                    description = "ऐप खोलने पर फ़िंगरप्रिंट या पिन अनिवार्य करें",
                    icon = Icons.Default.Lock,
                    checked = isAppLock,
                    onCheckedChange = { isAppLock = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Privacy Controls (प्राइवेसी सेटिंग्स)
                Text(
                    text = "PRIVACY PREFERENCES • प्राइवेसी सेटिंग्स",
                    color = ReelPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Private Account
                SecurityToggleRow(
                    title = "Private Account (निजी खाता)",
                    description = "केवल स्वीकृत फ़ॉलोवर्स ही आपकी रील्स देख सकेंगे",
                    icon = Icons.Default.PrivacyTip,
                    checked = isPrivateAccount,
                    onCheckedChange = { isPrivateAccount = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Allow Video Downloads
                SecurityToggleRow(
                    title = "Allow Reel Downloads",
                    description = "अन्य यूज़र्स को आपकी रील डाउनलोड करने की अनुमति दें",
                    icon = Icons.Default.Download,
                    checked = allowDownloads,
                    onCheckedChange = { allowDownloads = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Legal Liability Notice (कंपनी जिम्मेदार नहीं है)
                // Legal & Safety Disclaimer Card
                LegalSafetyDisclaimerCard(
                    showDetailedPoints = true,
                    onOpenTermsDialog = { showTermsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Account Deletion (अकाउंट डिलीट करने का ऑप्शन)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0D15)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Danger Zone • खाता डिलीट करें",
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "अपना अकाउंट, रील्स, पेजेस व कमाई डेटा हमेशा के लिए हटाएं। यह क्रिया वापस नहीं ली जा सकती।",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onRequestDeleteAccount()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("danger_zone_delete_account_button")
                        ) {
                            Text("Delete Account Permanently • अकाउंट हटाएं", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedUser = user.copy(
                        is2FAEnabled = is2FA,
                        isAppLockEnabled = isAppLock,
                        isAntiHackShieldActive = isAntiHackShield,
                        isPrivateAccount = isPrivateAccount,
                        allowReelDownloads = allowDownloads,
                        autoFilterComments = autoFilterComments
                    )
                    onUpdateSecurity(updatedUser)
                    onDismiss()
                    Toast.makeText(context, "🔒 सुरक्षा एवं प्राइवेसी सेटिंग्स सुरक्षित कर दी गई हैं!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_security_settings_button")
            ) {
                Text("Save Settings • सहेजें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
            ) {
                Text("Close • बंद करें", fontSize = 12.sp)
            }
        }
    )

    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onDismiss = { showTermsDialog = false }
        )
    }
}

@Composable
private fun SecurityToggleRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) ReelCyan else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ReelPink,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                ),
                modifier = Modifier.testTag("toggle_${title.take(6)}")
            )
        }
    }
}
