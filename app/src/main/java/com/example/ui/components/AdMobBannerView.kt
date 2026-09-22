package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ads.AdMobManager
import com.example.ui.theme.ReelBorder
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurfaceVariant

@Composable
fun AdMobBannerView(
    onAdImpression: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReelSurfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(1.dp, ReelBorder, RoundedCornerShape(12.dp))
            .clickable {
                onAdImpression()
                Toast.makeText(context, "AdMob Test Ad: Click recorded (+₹2.50 creator revenue)", Toast.LENGTH_SHORT).show()
            }
            .testTag("admob_banner_view")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ad Badge & Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(listOf(ReelPink, ReelPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = ReelGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFB300))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Ad",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sponsored • Google AdMob",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Discover Top Creator Gear 2026",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )

                Text(
                    text = "Ad Unit: ${AdMobManager.TEST_BANNER_AD_UNIT_ID.takeLast(12)}",
                    fontSize = 10.sp,
                    color = ReelCyan.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            ElevatedButton(
                onClick = {
                    onAdImpression()
                    Toast.makeText(context, "Opening Sponsored Sponsor link...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ReelPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
            }
        }
    }
}
