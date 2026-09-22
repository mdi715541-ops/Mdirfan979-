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
    val ifscCode: String = "HDFC0001234"
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
    val filterType: String = "NORMAL", // NORMAL, VINTAGE, NOIR, VIBRANT, NEON, CINEMATIC, GOLDEN_HOUR
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isLikedByMe: Boolean = false,
    val isDownloaded: Boolean = false,
    val isUserUpload: Boolean = false,
    val earnings: Double = 0.0
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
    GOLDEN_HOUR("Golden Hour", "Warm sunset amber radiance")
}

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val isExtracted: Boolean = false
)
