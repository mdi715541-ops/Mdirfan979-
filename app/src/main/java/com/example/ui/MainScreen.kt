package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.feed.ReelsFeedScreen
import com.example.ui.screens.monetization.MonetizationScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.record.CreateReelScreen
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface

enum class ReelNavTab {
    FEED,
    MONETIZATION,
    PROFILE
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allReels by viewModel.allReels.collectAsState()
    val userUploadedReels by viewModel.userUploadedReels.collectAsState()
    val monetizationStats by viewModel.monetizationStats.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val activeComments by viewModel.activeComments.collectAsState()

    var activeTab by remember { mutableStateOf(ReelNavTab.FEED) }
    var isCreateScreenOpen by remember { mutableStateOf(false) }

    // If user is not logged in or account was deleted, show AuthScreen
    if (currentUser == null || !currentUser!!.isLoggedIn) {
        AuthScreen(
            onLoginSuccess = { user ->
                viewModel.login(user)
            },
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            bottomBar = {
                ReelVibeBottomBar(
                    selectedTab = activeTab,
                    onTabSelected = { activeTab = it },
                    onCreateClick = { isCreateScreenOpen = true }
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {
                when (activeTab) {
                    ReelNavTab.FEED -> {
                        ReelsFeedScreen(
                            reels = allReels,
                            activeComments = activeComments,
                            onLikeToggle = { id, liked -> viewModel.toggleLike(id, liked) },
                            onRecordView = { id -> viewModel.recordView(id) },
                            onRecordShare = { id -> viewModel.recordShare(id) },
                            onDownloadReel = { id -> viewModel.downloadReel(id) },
                            onDeleteReel = { id -> viewModel.deleteReel(id) },
                            onOpenCommentsForReel = { id -> viewModel.loadComments(id) },
                            onAddComment = { id, text -> viewModel.addComment(id, text) },
                            onLikeComment = { commentId -> viewModel.likeComment(commentId) },
                            onAdImpression = { viewModel.addAdReward(2.5, 1) }
                        )
                    }

                    ReelNavTab.MONETIZATION -> {
                        MonetizationScreen(
                            stats = monetizationStats,
                            payouts = payouts,
                            userUpiId = currentUser?.upiId ?: "",
                            onRequestPayout = { amount, method, dest ->
                                viewModel.requestPayout(amount, method, dest)
                            },
                            onAddAdReward = { amount, delta ->
                                viewModel.addAdReward(amount, delta)
                            }
                        )
                    }

                    ReelNavTab.PROFILE -> {
                        ProfileScreen(
                            user = currentUser,
                            userReels = userUploadedReels,
                            allReels = allReels,
                            onUpdateProfile = { updated -> viewModel.updateProfile(updated) },
                            onDeleteAccount = { viewModel.deleteAccount() },
                            onLogout = { viewModel.logout() },
                            onNavigateToMonetization = { activeTab = ReelNavTab.MONETIZATION },
                            onDeleteReel = { id -> viewModel.deleteReel(id) }
                        )
                    }
                }
            }
        }

        // Fullscreen Create Reel Sheet (Camera, Filters, Audio Extraction, Publish)
        AnimatedVisibility(
            visible = isCreateScreenOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            CreateReelScreen(
                currentUserName = currentUser?.name ?: "Mohammad Irafan",
                currentUserHandle = currentUser?.handle ?: "@irafan_creator",
                onPublishReel = { newReel ->
                    viewModel.publishReel(newReel)
                    isCreateScreenOpen = false
                    activeTab = ReelNavTab.FEED
                },
                onCancel = { isCreateScreenOpen = false }
            )
        }
    }
}

@Composable
fun ReelVibeBottomBar(
    selectedTab: ReelNavTab,
    onTabSelected: (ReelNavTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.Black.copy(alpha = 0.95f))
            .border(
                width = 0.6.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .testTag("reel_vibe_bottom_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home / Feed
            BottomNavItem(
                label = "Feed",
                isSelected = selectedTab == ReelNavTab.FEED,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(ReelNavTab.FEED) },
                modifier = Modifier.testTag("nav_tab_feed")
            )

            // Create Reel Button (+) with Neon Gradient
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ReelPink, ReelPurple, ReelCyan)
                        )
                    )
                    .clickable { onCreateClick() }
                    .testTag("nav_create_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Reel",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Monetization Studio
            BottomNavItem(
                label = "Studio",
                isSelected = selectedTab == ReelNavTab.MONETIZATION,
                activeIcon = Icons.Filled.MonetizationOn,
                inactiveIcon = Icons.Outlined.MonetizationOn,
                activeTint = ReelGold,
                onClick = { onTabSelected(ReelNavTab.MONETIZATION) },
                modifier = Modifier.testTag("nav_tab_monetization")
            )

            // Profile
            BottomNavItem(
                label = "Profile",
                isSelected = selectedTab == ReelNavTab.PROFILE,
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                onClick = { onTabSelected(ReelNavTab.PROFILE) },
                modifier = Modifier.testTag("nav_tab_profile")
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    activeTint: Color = Color.White,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (isSelected) activeTint else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeTint else Color.White.copy(alpha = 0.5f)
        )
    }
}
