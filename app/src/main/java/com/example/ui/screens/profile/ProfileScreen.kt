package com.example.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.CreatorPageEntity
import com.example.data.model.ModerationReportEntity
import com.example.data.model.ReelEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CreatePageDialog
import com.example.ui.components.ModeratorControlSheet
import com.example.ui.components.SecurityPrivacyDialog
import com.example.ui.components.TermsAndConditionsDialog
import com.example.ui.components.VideoFilterOverlay
import com.example.ui.theme.ReelBorder
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant

@Composable
fun ProfileScreen(
    user: UserEntity?,
    userReels: List<ReelEntity>,
    allReels: List<ReelEntity>,
    blacklistedUsers: List<BlacklistedUserEntity> = emptyList(),
    pendingReports: List<ModerationReportEntity> = emptyList(),
    allPages: List<CreatorPageEntity> = emptyList(),
    onUpdateProfile: (UserEntity) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToMonetization: () -> Unit,
    onDeleteReel: (Long) -> Unit,
    onCreatePage: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onSwitchToPage: (CreatorPageEntity) -> Unit = {},
    onSwitchToNormalProfile: () -> Unit = {},
    onDeletePage: (Long) -> Unit = {},
    onBlacklistUser: (String, String, String) -> Unit = { _, _, _ -> },
    onUnblacklistUser: (String) -> Unit = {},
    onDeleteBlacklistEntry: (Long) -> Unit = {},
    onResolveReportAndBan: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    onDismissReport: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser = user ?: UserEntity()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showModeratorSheet by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showCreatePageDialog by remember { mutableStateOf(false) }
    var showPagesListDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = My Reels, 1 = Saved

    val savedReels = allReels.filter { it.isDownloaded }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090710))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Profile Header Section
        item(span = { GridItemSpan(3) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentUser.handle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {
                        IconButton(
                            onClick = { showSecurityDialog = true },
                            modifier = Modifier.testTag("security_privacy_top_icon")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Security & Privacy", tint = ReelCyan)
                        }
                        IconButton(onClick = onNavigateToMonetization) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Studio", tint = ReelGold)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar & Counts Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.5.dp,
                                brush = Brush.linearGradient(listOf(ReelPink, ReelCyan)),
                                shape = CircleShape
                            )
                            .background(ReelPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.take(1).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Stats Columns
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(count = userReels.size.toString(), label = "Reels")
                        StatItem(count = formatNumber(currentUser.followersCount), label = "Followers")
                        StatItem(count = formatNumber(currentUser.followingCount), label = "Following")
                        StatItem(count = formatNumber(currentUser.totalLikesCount), label = "Likes")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name & Bio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentUser.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = ReelCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentUser.bio,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Moderator & Safety Zone Banner
                Card(
                    onClick = { showModeratorSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1528)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color(0xFFE53935), Color(0xFFFF9800)))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_profile_moderator_zone")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "मॉडरेटर कंट्रोल ज़ोन (Admin Zone)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "ब्लैकलिस्ट: ${blacklistedUsers.size} यूजर | रिपोर्ट: ${pendingReports.size} पेंडिंग",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = ReelGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Page Mode / Normal Profile Controller Banner (पेज बनाने व सामान्य करने का ऑप्शन - कमाई के लिए)
                if (currentUser.isPageMode) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1028)),
                        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ReelGold, ReelPink))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("page_mode_active_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(ReelGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PostAdd, contentDescription = null, tint = ReelGold, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = currentUser.activePageName.ifBlank { "क्रिएटर पेज (Creator Page)" },
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = ReelGold, modifier = Modifier.size(14.dp))
                                        }
                                        Text(
                                            text = "कैटेगरी: ${currentUser.activePageCategory} • मोनेटाइजेशन चालू 💰",
                                            fontSize = 11.sp,
                                            color = ReelGold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ReelGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("PAGE ACTIVE", color = ReelGold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Page Earnings snippet
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("पेज कमाई (Revenue)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                    Text("₹${currentUser.activePageEarnings}", color = ReelGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color.White.copy(alpha = 0.1f)))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("पेज फॉलोअर्स (Followers)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                    Text("${currentUser.activePageFollowers} जुड़े", color = ReelCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Page Action Buttons: Switch to Normal Profile (पेज को नॉर्मल करने का ऑप्शन) + Monetization
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onSwitchToNormalProfile()
                                        Toast.makeText(context, "✅ सामान्य (पर्सनल) प्रोफ़ाइल में बदल दिया गया!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(38.dp)
                                        .testTag("switch_to_normal_profile_button")
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("सामान्य प्रोफ़ाइल करें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = onNavigateToMonetization,
                                    colors = ButtonDefaults.buttonColors(containerColor = ReelGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("page_earnings_button")
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("पेज कमाई देखें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF140E20)),
                        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(ReelCyan.copy(alpha = 0.5f), ReelPink.copy(alpha = 0.5f)))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("normal_profile_mode_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(ReelCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ReelCyan, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "सामान्य प्रोफ़ाइल (Personal Mode)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "पेज बनाकर रील्स से सीधे पैसे कमाएं 💰",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showCreatePageDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ReelPink),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("create_new_page_button")
                                ) {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("पेज बनाएं • पैसे कमाएं", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                if (allPages.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = { showPagesListDialog = true },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReelGold),
                                        border = BorderStroke(1.dp, ReelGold.copy(alpha = 0.6f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .testTag("switch_to_creator_page_button")
                                    ) {
                                        Icon(Icons.Default.Layers, contentDescription = null, tint = ReelGold, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("पेज खोलें (${allPages.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ReelGold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons (Edit Profile, Security & Privacy, Terms & Conditions, Delete Account)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { showSecurityDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReelCyan),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(40.dp)
                            .testTag("security_privacy_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ReelCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Security", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ReelCyan)
                    }

                    OutlinedButton(
                        onClick = { showTermsDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReelGold),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("terms_conditions_button")
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = ReelGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Terms", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ReelGold)
                    }

                    OutlinedButton(
                        onClick = { showDeleteAccountDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("delete_account_button")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs (My Reels vs Saved)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = ReelPink,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ReelPink,
                            height = 2.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                Icons.Default.GridOn,
                                contentDescription = "My Reels",
                                tint = if (selectedTab == 0) ReelPink else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = "Saved Reels",
                                tint = if (selectedTab == 1) ReelPink else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Reels Grid Content
        val displayedReels = if (selectedTab == 0) userReels else savedReels

        if (displayedReels.isEmpty()) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.PlayArrow else Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) "No reels uploaded yet" else "No saved reels yet",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(displayedReels, key = { it.id }) { reel ->
                ReelThumbnailItem(
                    reel = reel,
                    onDelete = { onDeleteReel(reel.id) }
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(currentUser.name) }
        var editHandle by remember { mutableStateOf(currentUser.handle) }
        var editBio by remember { mutableStateOf(currentUser.bio) }
        var editUpi by remember { mutableStateOf(currentUser.upiId) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text("Edit Profile • प्रोफ़ाइल संपादित करें", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelPink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editHandle,
                        onValueChange = { editHandle = it },
                        label = { Text("Username Handle (@)") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelPink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelPink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editUpi,
                        onValueChange = { editUpi = it },
                        label = { Text("UPI ID (For Monetization)") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ReelCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = currentUser.copy(
                            name = editName.trim(),
                            handle = if (editHandle.startsWith("@")) editHandle.trim() else "@${editHandle.trim()}",
                            bio = editBio.trim(),
                            upiId = editUpi.trim()
                        )
                        onUpdateProfile(updated)
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully! ✨", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReelPink)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = ReelSurface
        )
    }

    // Delete Account Dialog (अकाउंट डिलीट करने का ऑप्शन)
    if (showDeleteAccountDialog) {
        var deleteConfirmationText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Delete Account? • खाता हमेशा के लिए हटाएं?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "⚠️ WARNING: This action is permanent and cannot be undone.\n\nAll your uploaded reels, profile information, follower relationships, comments, and pending monetization earnings will be permanently deleted from ReelVibe servers.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Type 'DELETE' below to confirm:",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = deleteConfirmationText,
                        onValueChange = { deleteConfirmationText = it },
                        placeholder = { Text("DELETE", color = Color.White.copy(alpha = 0.4f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ReelSurfaceVariant,
                            unfocusedContainerColor = ReelSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_confirmation_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteConfirmationText.trim().equals("DELETE", ignoreCase = true)) {
                            showDeleteAccountDialog = false
                            onDeleteAccount()
                            Toast.makeText(context, "Account permanently deleted. Goodbye! 👋", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please type DELETE to confirm", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1B0B14)
        )
    }

    // Security & Privacy Settings Dialog
    if (showSecurityDialog) {
        SecurityPrivacyDialog(
            user = currentUser,
            onUpdateSecurity = { updatedUser ->
                onUpdateProfile(updatedUser)
            },
            onRequestDeleteAccount = {
                showSecurityDialog = false
                showDeleteAccountDialog = true
            },
            onDismiss = { showSecurityDialog = false }
        )
    }

    // Create Page Dialog (पेज बनाने का ऑप्शन - जिससे लोग पैसे कमाएंगे)
    if (showCreatePageDialog) {
        CreatePageDialog(
            initialUpiId = currentUser.upiId.ifBlank { "irafan@okaxis" },
            onDismiss = { showCreatePageDialog = false },
            onPageCreated = { pageName, category, bio, upiId ->
                showCreatePageDialog = false
                onCreatePage(pageName, category, bio, upiId)
            }
        )
    }

    // Pages List & Switcher Dialog (क्रिएटर पेज चुनने व प्रबंधित करने का ऑप्शन)
    if (showPagesListDialog) {
        AlertDialog(
            onDismissRequest = { showPagesListDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = ReelGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("क्रिएटर पेजेस (${allPages.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "जिस पेज पर काम करना हो उसे चुनें या नया पेज बनाएं:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    allPages.forEach { page ->
                        val isCurrentActive = currentUser.isPageMode && currentUser.activePageId == page.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentActive) ReelPink.copy(alpha = 0.2f) else ReelSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    1.dp,
                                    if (isCurrentActive) ReelGold else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(page.pageName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (isCurrentActive) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("(एक्टिव)", color = ReelGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        "${page.category} • ${page.followersCount} फॉलोअर्स • ₹${page.totalEarnings} कमाई",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.5.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!isCurrentActive) {
                                        Button(
                                            onClick = {
                                                showPagesListDialog = false
                                                onSwitchToPage(page)
                                                Toast.makeText(context, "✅ '${page.pageName}' पेज एक्टिव हो गया!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ReelGold),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("खोलें", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            onDeletePage(page.id)
                                            Toast.makeText(context, "पेज हटा दिया गया", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Page", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            showPagesListDialog = false
                            showCreatePageDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReelPink),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = ReelPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ एक और नया पेज बनाएं", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPagesListDialog = false }) {
                    Text("Close • बंद करें", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = ReelSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Moderator Control Zone Sheet
    if (showModeratorSheet) {
        ModeratorControlSheet(
            blacklistedUsers = blacklistedUsers,
            pendingReports = pendingReports,
            onBlacklistUser = onBlacklistUser,
            onUnblacklistUser = onUnblacklistUser,
            onDeleteBlacklistEntry = onDeleteBlacklistEntry,
            onResolveReportAndBan = onResolveReportAndBan,
            onDismissReport = onDismissReport,
            onDismiss = { showModeratorSheet = false }
        )
    }

    // Terms & Conditions and Legal Safety Zone Dialog
    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onDismiss = { showTermsDialog = false }
        )
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
fun ReelThumbnailItem(
    reel: ReelEntity,
    onDelete: () -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(0.72f)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF28104E), Color(0xFF120724))
                )
            )
            .clickable { showOptions = true }
    ) {
        VideoFilterOverlay(filterType = reel.filterType)

        // Bottom views badge
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = formatNumber(reel.viewsCount),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Filter badge top right
        if (reel.filterType != "NORMAL") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(ReelPink)
                    .size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
    }

    if (showOptions && reel.isUserUpload) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Reel Options", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(reel.caption, color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOptions = false
                        onDelete()
                    }
                ) {
                    Text("Delete Reel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOptions = false }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = ReelSurface
        )
    }
}

fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format("%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
