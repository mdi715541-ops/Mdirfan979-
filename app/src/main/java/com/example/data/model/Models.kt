package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_main",
    val name: String = "Mohammad Irafan",
    val handle: String = "@irafan_creator",
    val email: String = "mdi715541@gmail.com",
    val bio: String = "✨ Creating daily cinematic reels & vibes! • ReelVibe Official Creator 🚀",
    val avatarUrl: String = "",
    val followersCount: Int = 14200,
    val followingCount: Int = 340,
    val totalLikesCount: Int = 98500,
    val isMonetizationApproved: Boolean = true,
    val isLoggedIn: Boolean = true,
    val upiId: String = "irafan@okaxis",
    val bankAccount: String = "•••• 6493",
    val ifscCode: String = "HDFC0001234",
    // Security & Anti-Hack protections
    val is2FAEnabled: Boolean = true,
    val isAppLockEnabled: Boolean = true,
    val isAntiHackShieldActive: Boolean = true,
    // Privacy controls
    val isPrivateAccount: Boolean = false,
    val allowReelDownloads: Boolean = true,
    val autoFilterComments: Boolean = true,
    // Page Mode vs Normal Profile Mode (पेज मोड vs सामान्य प्रोफ़ाइल)
    val isPageMode: Boolean = false,
    val activePageId: Long = 0L,
    val activePageName: String = "",
    val activePageCategory: String = "Digital Creator",
    val activePageFollowers: Int = 0,
    val activePageEarnings: Double = 0.0,
    // Password for recovery & authentication
    val password: String = "password123"
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorId: String = "user_main",
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String = "",
    val caption: String,
    val hashtags: String = "#reelvibe #viral #trending",
    val videoUri: String, // Local URI or sample bundled video URI / procedural
    val audioTitle: String = "Original Sound - ReelVibe",
    val audioArtist: String = "Trending Music",
    val filterType: String = "NORMAL", // NORMAL, VINTAGE, NOIR, VIBRANT, NEON, CINEMATIC, GOLDEN_HOUR, BOLLYWOOD_GLAM, RETRO_VHS
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isLikedByMe: Boolean = false,
    val isDownloaded: Boolean = false,
    val isUserUpload: Boolean = false,
    val earnings: Double = 0.0,
    val isMonetized: Boolean = true,
    val adEarnings: Double = 12.50,
    val location: String = "",
    val videoStickerText: String = "",
    val remixesCount: Int = 3,
    val collaboratorHandle: String = "",
    val collaboratorAvatar: String = "",
    val isSavedByMe: Boolean = false
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorId: String = "user_main",
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String = "",
    val mediaType: String = "PHOTO", // "PHOTO" or "VIDEO"
    val mediaUri: String = "",
    val audioTitle: String = "Nigahen Kyon Churaati Hai • Udit Narayan",
    val audioArtist: String = "Bollywood Classic",
    val caption: String = "",
    val stickerText: String = "",
    val location: String = "अपना फखरपुर बहराइच",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val isUserStory: Boolean = false,
    val likesCount: Int = 0
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reelId: Long,
    val userName: String,
    val userHandle: String,
    val userAvatar: String = "",
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

@Entity(tableName = "blacklisted_users")
data class BlacklistedUserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userHandle: String,
    val userName: String,
    val userAvatar: String = "",
    val reason: String = "Abusive Language & Guidelines Violation (अभद्र भाषा और नियम उल्लंघन)",
    val bannedBy: String = "@irafan_creator (Admin)",
    val timestamp: Long = System.currentTimeMillis(),
    val isPermanent: Boolean = true,
    val status: String = "BANNED" // "BANNED" or "WARNING"
)

@Entity(tableName = "moderation_reports")
data class ModerationReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "COMMENT", "REEL", "USER"
    val targetId: Long,
    val reportedHandle: String,
    val reportedName: String,
    val contentSnippet: String,
    val violationType: String, // "ABUSIVE_LANGUAGE", "HATE_SPEECH", "COMMUNITY_VIOLATION", "SPAM"
    val reportCount: Int = 1,
    val status: String = "PENDING", // "PENDING", "RESOLVED_BANNED", "DISMISSED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "monetization")
data class MonetizationEntity(
    @PrimaryKey val id: Int = 1,
    val totalBalance: Double = 24850.00, // in INR (₹)
    val monthlyEarnings: Double = 8420.50,
    val totalViews: Long = 1425800L,
    val adImpressions: Long = 620400L,
    val rpm: Double = 18.50, // Revenue per 1000 views in ₹
    val cpm: Double = 32.00, // Cost per 1000 ad impressions in ₹
    val watchTimeHours: Double = 3420.0,
    val pendingPayout: Double = 4500.00
)

@Entity(tableName = "payouts")
data class PayoutRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val method: String, // "UPI" or "Bank Transfer"
    val destination: String,
    val date: String,
    val status: String = "Completed", // "Completed" or "Processing"
    val transactionRef: String = "TXN" + System.currentTimeMillis().toString().takeLast(8)
)

enum class VideoFilter(val displayName: String, val description: String) {
    NORMAL("Normal", "Original natural colors"),
    VINTAGE("Vintage", "Warm nostalgic sepia tone"),
    NOIR("Noir", "Cinematic high-contrast B&W"),
    VIBRANT("Vibrant", "Punchy rich color pop"),
    NEON("Neon Glow", "Cyberpunk magenta & cyan"),
    CINEMATIC("Cinematic", "Teal & orange Hollywood look"),
    GOLDEN_HOUR("Golden Hour", "Warm sunset amber radiance"),
    BOLLYWOOD_GLAM("Bollywood", "Dreamy soft glow & warm tones"),
    RETRO_VHS("Retro VHS", "90s camcorder aesthetic & vibe")
}

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val isExtracted: Boolean = false
)

@Entity(tableName = "creator_pages")
data class CreatorPageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerHandle: String = "@irafan_creator",
    val pageName: String,
    val pageHandle: String,
    val category: String = "Digital Creator",
    val bio: String = "Official Creator Page • मनोरंजन और नई रील्स! 🚀",
    val followersCount: Int = 1250,
    val totalViews: Long = 45000L,
    val totalEarnings: Double = 3450.00, // Total ₹ earned from this page
    val monthlyRevenue: Double = 1200.00,
    val isMonetizationActive: Boolean = true,
    val upiId: String = "irafan@okaxis",
    val bannerTheme: String = "PURPLE",
    val createdAt: Long = System.currentTimeMillis()
)

