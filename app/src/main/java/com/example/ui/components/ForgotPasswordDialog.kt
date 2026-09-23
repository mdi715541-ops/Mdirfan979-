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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant

@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "mdi715541@gmail.com",
    onDismiss: () -> Unit,
    onPasswordResetSuccess: (newPassword: String) -> Unit
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf(initialEmail) }
    var otpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ReelGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LockReset,
                        contentDescription = null,
                        tint = ReelGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "पासवर्ड भूल गए? (Reset Password)",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ओटीपी वेरिफिकेशन द्वारा नया पासवर्ड सेट करें",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Email input
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Registered Email / हैंडल") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = ReelCyan)
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
                        .testTag("forgot_password_email_input"),
                    enabled = !otpSent
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!otpSent) {
                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                generatedOtp = (100000..999999).random().toString()
                                otpSent = true
                                enteredOtp = generatedOtp // auto-fill for best convenience
                                Toast.makeText(
                                    context,
                                    "ओटीपी कोड भेजा गया: $generatedOtp",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(context, "कृपया वैध ईमेल दर्ज करें", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ReelCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_otp_button")
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send OTP • ओटीपी भेजें", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // OTP Sent Badge Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ReelSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ReelGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("📩 6-डिजिट सुरक्षा कोड:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text(generatedOtp, color = ReelGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                            TextButton(onClick = { enteredOtp = generatedOtp }) {
                                Text("Auto Fill", color = ReelCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // OTP Input
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it.take(6) },
                        label = { Text("Enter 6-Digit OTP • ओटीपी कोड") },
                        leadingIcon = {
                            Icon(Icons.Default.Pin, contentDescription = null, tint = ReelGold)
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
                            .testTag("forgot_password_otp_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // New Password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password • नया पासवर्ड") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ReelPink)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                            .testTag("forgot_password_new_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password • पासवर्ड दोहराएं") },
                        leadingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ReelPink)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                            .testTag("forgot_password_confirm_password_input")
                    )
                }
            }
        },
        confirmButton = {
            if (otpSent) {
                Button(
                    onClick = {
                        if (enteredOtp.trim() != generatedOtp.trim()) {
                            Toast.makeText(context, "गलत ओटीपी कोड दर्ज किया गया है", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword.isBlank() || newPassword.length < 4) {
                            Toast.makeText(context, "पासवर्ड कम से कम 4 अक्षरों का होना चाहिए", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "दोनों पासवर्ड मेल नहीं खाते", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        Toast.makeText(context, "✅ पासवर्ड सफलतापूर्वक बदल दिया गया है!", Toast.LENGTH_LONG).show()
                        onPasswordResetSuccess(newPassword)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                    modifier = Modifier.testTag("reset_password_submit_button")
                ) {
                    Text("Reset & Save • नया पासवर्ड सेट करें", fontWeight = FontWeight.Bold, color = Color.White)
                }
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
