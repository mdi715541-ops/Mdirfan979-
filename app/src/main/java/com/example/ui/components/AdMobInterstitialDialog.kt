package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ads.AdMobManager
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import kotlinx.coroutines.delay

@Composable
fun AdMobInterstitialDialog(
    isRewarded: Boolean = true,
    onRewardEarned: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var secondsLeft by remember { mutableIntStateOf(5) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        isCompleted = true
        val reward = if (isRewarded) AdMobManager.REWARDED_AD_CREATOR_PAYOUT else AdMobManager.INTERSTITIAL_AD_PAYOUT
        onRewardEarned(reward)
        Toast.makeText(context, "🎉 Reward Earned: +₹$reward credited to creator balance!", Toast.LENGTH_LONG).show()
    }

    Dialog(
        onDismissRequest = {
            if (isCompleted || secondsLeft <= 0) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C0A14))
                .testTag("admob_interstitial_dialog"),
            color = Color(0xFF0C0A14)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ReelGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isRewarded) "REWARDED AD" else "INTERSTITIAL AD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }

                    if (secondsLeft > 0) {
                        Text(
                            text = "Reward in ${secondsLeft}s",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ad Content Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF241442), Color(0xFF130924))
                            )
                        )
                        .border(1.dp, ReelPink.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CardGiftcard else Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = if (isCompleted) ReelGold else ReelPink,
                            modifier = Modifier.size(76.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isCompleted) "Reward Unlocked! ✨" else "Google AdMob Sponsored Stream",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isCompleted)
                                "₹${if (isRewarded) 50 else 15} has been deposited to your creator wallet! 💰"
                            else
                                "Supporting ReelVibe creators with high-yield video advertising.\nAd Unit ID: ${AdMobManager.TEST_REWARDED_AD_UNIT_ID.takeLast(10)}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (!isCompleted) {
                            LinearProgressIndicator(
                                progress = { (5 - secondsLeft) / 5f },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ReelCyan,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action
                if (isCompleted) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Claim Reward & Return",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Please wait ${secondsLeft}s to earn reward...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
