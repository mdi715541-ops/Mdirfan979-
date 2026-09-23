package com.example.ui.screens.monetization

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.AdMobInterstitialDialog
import com.example.ui.theme.ReelBorder
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelGreen
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun MonetizationScreen(
    stats: MonetizationEntity?,
    payouts: List<PayoutRecordEntity>,
    userUpiId: String,
    onRequestPayout: (Double, String, String) -> Unit,
    onAddAdReward: (Double, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentStats = stats ?: MonetizationEntity()

    var showPayoutDialog by remember { mutableStateOf(false) }
    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var showInterstitialAdDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090710))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("monetization_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Creator Studio • मोनेटाइजेशन",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Track your earnings, views & ad revenue",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ReelGreen.copy(alpha = 0.2f))
                        .border(1.dp, ReelGreen, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "● Verified Partner",
                        color = ReelGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Wallet Balance Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3B105E), Color(0xFF1B072B), Color(0xFF0E0417))
                            )
                        )
                        .border(1.dp, ReelPink.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AVAILABLE BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReelCyan,
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = ReelGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "₹${String.format("%,.2f", currentStats.totalBalance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = ReelGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+₹${String.format("%,.2f", currentStats.monthlyEarnings)} earned this month",
                                fontSize = 12.sp,
                                color = ReelGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showPayoutDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("withdraw_payout_button")
                            ) {
                                Text("Withdraw • पैसे निकालें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { showRewardedAdDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("boost_earnings_button")
                            ) {
                                Text("Boost +₹50", color = ReelCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Creator Partner Program Eligibility & Guidelines Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140D24)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, ReelGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .testTag("monetization_guidelines_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ReelGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = ReelGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Creator Monetization Criteria & Rules",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "मोनेटाइजेशन पात्रता व महत्वपूर्ण नियम",
                                color = ReelGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Followers Criterion (500 Followers)
                    val followersTarget = 500
                    val currentFollowers = 845 // User has 845 followers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("1. Followers • 500 फॉलोवर", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (currentFollowers >= followersTarget) "Eligible (पात्र $currentFollowers/500) ✓" else "$currentFollowers/$followersTarget",
                            color = if (currentFollowers >= followersTarget) ReelGreen else ReelGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentFollowers.toFloat() / followersTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ReelGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Views Criterion (1,000 Views)
                    val viewsTarget = 1000L
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("2. Reel Views • 1,000 व्यूज", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (currentStats.totalViews >= viewsTarget) "Eligible (पात्र ${currentStats.totalViews}/1000) ✓" else "${currentStats.totalViews}/$viewsTarget",
                            color = if (currentStats.totalViews >= viewsTarget) ReelGreen else ReelGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentStats.totalViews.toFloat() / viewsTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ReelGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Likes Criterion (1,000 Likes)
                    val likesTarget = 1000
                    val currentLikes = 2450 // User total likes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("3. Reel Likes • 1,000 लाइक", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (currentLikes >= likesTarget) "Eligible (पात्र $currentLikes/1000) ✓" else "$currentLikes/$likesTarget",
                            color = if (currentLikes >= likesTarget) ReelGreen else ReelGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentLikes.toFloat() / likesTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ReelGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Monetization Status Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ReelGreen.copy(alpha = 0.15f))
                            .border(1.dp, ReelGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ReelGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("मोनेटाइजेशन चालू है (Monetization Active)", color = ReelGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("रील्स पर विज्ञापन चल रहे हैं और प्रति व्यू/इम्प्रेशन कमाई सीधे वॉलेट में जुड़ रही है।", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Company Safe-Harbor & Security Anti-Hack Disclaimer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, ReelPink.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ReelPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "सुरक्षा, प्राइवेसी व कंपनी अस्वीकरण (Legal & Safety):",
                                    color = ReelPink,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "• कंपनी ज़िम्मेदार नहीं है: यदि कोई यूजर गलत, अश्लील या भ्रामक वीडियो पोस्ट करता है, तो वह स्वयं व्यक्तिगत रूप से जिम्मेदार होगा। कंपनी इसके लिए उत्तरदायी नहीं है।\n• एंटी-हैक सुरक्षा: आपका अकाउंट, डेटा व कमाई 256-बिट सुरक्षित एन्क्रिप्शन व 2FA से सुरक्षित है ताकि अकाउंट कभी हैक न हो सके।",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Metrics Grid (Views, Impressions, RPM, CPM)
        item {
            Text(
                text = "Performance Metrics • रील्स एनालिटिक्स",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Views",
                    value = formatLargeNumber(currentStats.totalViews),
                    subtitle = "All Reels",
                    icon = Icons.Default.Visibility,
                    tint = ReelCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Ad Impressions",
                    value = formatLargeNumber(currentStats.adImpressions),
                    subtitle = "Google AdMob",
                    icon = Icons.Default.AdsClick,
                    tint = ReelGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "RPM Rate",
                    value = "₹${currentStats.rpm}",
                    subtitle = "per 1,000 Views",
                    icon = Icons.Default.MonetizationOn,
                    tint = ReelGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Watch Time",
                    value = "${currentStats.watchTimeHours.toInt()} hrs",
                    subtitle = "Audience Retention",
                    icon = Icons.Default.QueryStats,
                    tint = ReelPink,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Visual Revenue Chart (Mon - Sun)
        item {
            Text(
                text = "Weekly Earnings Graph • साप्ताहिक आय",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, ReelBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Revenue (Last 7 Days)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("Avg: ₹1,200/day", color = ReelGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val dailyValues = listOf(
                        "Mon" to 0.45f,
                        "Tue" to 0.75f,
                        "Wed" to 0.60f,
                        "Thu" to 0.90f,
                        "Fri" to 0.82f,
                        "Sat" to 1.0f,
                        "Sun" to 0.95f
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyValues.forEach { (day, fraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height((80 * fraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(ReelPink, ReelPurple)
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(day, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        // AdMob Live Tools & Simulation Controls
        item {
            Text(
                text = "AdMob Monetization Engine • विज्ञापन सेटअप",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, ReelBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Google Mobile Ads (AdMob) Integration Active",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "App ID: ${AdMobManager.TEST_APP_ID}",
                        fontSize = 11.sp,
                        color = ReelCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showRewardedAdDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ReelPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rewarded Ad (+₹50)", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showInterstitialAdDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2546)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Interstitial (+₹15)", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onAddAdReward(185.0, 10000)
                            Toast.makeText(context, "Simulated 10,000 reel views & ad impressions! +₹185 credited", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate 10K Viral Views (+₹185.00)", color = ReelGold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Payout / Transaction History
        item {
            Text(
                text = "Payout History • निकासी इतिहास",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )

            if (payouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No payouts yet. Request a withdrawal once you reach ₹1,000!", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
            }
        }

        items(payouts) { payout ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (payout.status == "Completed") ReelGreen.copy(alpha = 0.2f) else ReelGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (payout.status == "Completed") Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = if (payout.status == "Completed") ReelGreen else ReelGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "₹${String.format("%,.2f", payout.amount)}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${payout.method} • ${payout.destination}",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = payout.status,
                            color = if (payout.status == "Completed") ReelGreen else ReelGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = payout.date,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }

    // Payout Request Dialog
    if (showPayoutDialog) {
        var withdrawAmount by remember { mutableStateOf("2000") }
        var selectedMethod by remember { mutableStateOf("UPI") }
        var destinationField by remember { mutableStateOf(userUpiId.ifBlank { "irafan@okaxis" }) }

        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = {
                Text(
                    text = "Request Payout • पैसे निकालें",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Current Balance: ₹${String.format("%,.2f", currentStats.totalBalance)}",
                        color = ReelGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Amount (₹) • राशि") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelPink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedMethod = "UPI"
                                destinationField = userUpiId.ifBlank { "irafan@okaxis" }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedMethod == "UPI") ReelPink else ReelSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("UPI (Instant)")
                        }

                        Button(
                            onClick = {
                                selectedMethod = "Bank"
                                destinationField = "HDFC Bank (•••• 6493)"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedMethod == "Bank") ReelPink else ReelSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bank Transfer")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = destinationField,
                        onValueChange = { destinationField = it },
                        label = { Text(if (selectedMethod == "UPI") "UPI ID" else "Bank Account Details") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Minimum withdrawal limit is ₹500. Processing takes under 2 hours via UPI.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmount.toDoubleOrNull() ?: 0.0
                        if (amount in 100.0..currentStats.totalBalance) {
                            onRequestPayout(amount, selectedMethod, destinationField)
                            showPayoutDialog = false
                            Toast.makeText(context, "🎉 Payout request submitted successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Invalid amount or insufficient balance", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReelPink)
                ) {
                    Text("Confirm Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = ReelSurface
        )
    }

    // Rewarded Ad Dialog
    if (showRewardedAdDialog) {
        AdMobInterstitialDialog(
            isRewarded = true,
            onRewardEarned = { reward ->
                onAddAdReward(reward, 1)
            },
            onDismiss = { showRewardedAdDialog = false }
        )
    }

    // Interstitial Ad Dialog
    if (showInterstitialAdDialog) {
        AdMobInterstitialDialog(
            isRewarded = false,
            onRewardEarned = { reward ->
                onAddAdReward(reward, 1)
            },
            onDismiss = { showInterstitialAdDialog = false }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ReelSurfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(1.dp, ReelBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = tint.copy(alpha = 0.8f), fontSize = 10.sp)
        }
    }
}

fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}
