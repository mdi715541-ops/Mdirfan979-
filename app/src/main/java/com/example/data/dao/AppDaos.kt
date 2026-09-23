package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ModerationReportEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
import com.example.data.model.StoryEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE isUserUpload = 1 ORDER BY timestamp DESC")
    fun getUserUploadedReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE id = :reelId LIMIT 1")
    suspend fun getReelById(reelId: Long): ReelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReels(reels: List<ReelEntity>)

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Query("DELETE FROM reels WHERE id = :reelId")
    suspend fun deleteReelById(reelId: Long)

    @Query("DELETE FROM reels WHERE isUserUpload = 1")
    suspend fun deleteAllUserReels()

    @Query("UPDATE reels SET likesCount = likesCount + :delta, isLikedByMe = :isLiked WHERE id = :reelId")
    suspend fun toggleLike(reelId: Long, delta: Int, isLiked: Boolean)

    @Query("UPDATE reels SET viewsCount = viewsCount + 1 WHERE id = :reelId")
    suspend fun incrementViews(reelId: Long)

    @Query("UPDATE reels SET sharesCount = sharesCount + 1 WHERE id = :reelId")
    suspend fun incrementShares(reelId: Long)

    @Query("UPDATE reels SET isDownloaded = 1 WHERE id = :reelId")
    suspend fun markAsDownloaded(reelId: Long)

    @Query("UPDATE reels SET commentsCount = commentsCount + 1 WHERE id = :reelId")
    suspend fun incrementCommentsCount(reelId: Long)

    @Query("UPDATE reels SET remixesCount = remixesCount + 1 WHERE id = :reelId")
    suspend fun incrementRemixes(reelId: Long)

    @Query("UPDATE reels SET isSavedByMe = :isSaved WHERE id = :reelId")
    suspend fun toggleSave(reelId: Long, isSaved: Boolean)

    @Query("SELECT COUNT(*) FROM reels")
    suspend fun getReelsCount(): Int
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 'user_main' LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 'user_main' LIMIT 1")
    suspend fun getCurrentUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = 'user_main'")
    suspend fun deleteUser()

    @Query("UPDATE users SET password = :newPassword WHERE id = 'user_main' OR email = :email")
    suspend fun updatePassword(email: String, newPassword: String)

    @Query("UPDATE users SET isPageMode = :isPageMode, activePageId = :pageId, activePageName = :pageName, activePageCategory = :category, activePageFollowers = :followers, activePageEarnings = :earnings WHERE id = 'user_main'")
    suspend fun updatePageMode(
        isPageMode: Boolean,
        pageId: Long,
        pageName: String,
        category: String,
        followers: Int,
        earnings: Double
    )

    @Query("UPDATE users SET isPageMode = 0, activePageId = 0, activePageName = '', activePageEarnings = 0.0 WHERE id = 'user_main'")
    suspend fun resetToNormalProfile()
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE reelId = :reelId ORDER BY timestamp DESC")
    fun getCommentsForReel(reelId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("DELETE FROM comments WHERE reelId = :reelId")
    suspend fun deleteCommentsForReel(reelId: Long)

    @Query("UPDATE comments SET likesCount = likesCount + 1, isLiked = 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: Long)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)

    @Query("DELETE FROM comments WHERE userHandle = :userHandle")
    suspend fun deleteCommentsByUser(userHandle: String)
}

@Dao
interface MonetizationDao {
    @Query("SELECT * FROM monetization WHERE id = 1 LIMIT 1")
    fun getMonetizationStats(): Flow<MonetizationEntity?>

    @Query("SELECT * FROM monetization WHERE id = 1 LIMIT 1")
    suspend fun getMonetizationStatsSync(): MonetizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: MonetizationEntity)

    @Query("UPDATE monetization SET totalBalance = totalBalance + :addedEarnings, monthlyEarnings = monthlyEarnings + :addedEarnings, adImpressions = adImpressions + :addedImpressions WHERE id = 1")
    suspend fun addAdReward(addedEarnings: Double, addedImpressions: Long)

    @Query("UPDATE monetization SET totalBalance = totalBalance - :payoutAmount WHERE id = 1")
    suspend fun deductBalance(payoutAmount: Double)

    @Query("DELETE FROM monetization")
    suspend fun clearStats()
}

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payouts ORDER BY id DESC")
    fun getAllPayouts(): Flow<List<PayoutRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutRecordEntity): Long

    @Query("DELETE FROM payouts")
    suspend fun clearPayouts()
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY isUserStory DESC, timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE isUserStory = 1 ORDER BY timestamp DESC")
    fun getUserStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :storyId")
    suspend fun markStoryAsViewed(storyId: Long)

    @Query("UPDATE stories SET likesCount = likesCount + 1 WHERE id = :storyId")
    suspend fun likeStory(storyId: Long)

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: Long)

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getStoriesCount(): Int
}

@Dao
interface BlacklistDao {
    @Query("SELECT * FROM blacklisted_users ORDER BY timestamp DESC")
    fun getAllBlacklistedUsers(): Flow<List<BlacklistedUserEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM blacklisted_users WHERE userHandle = :handle AND status = 'BANNED')")
    fun isUserBlacklisted(handle: String): Flow<Boolean>

    @Query("SELECT * FROM blacklisted_users WHERE userHandle = :handle LIMIT 1")
    suspend fun getBlacklistedUserByHandle(handle: String): BlacklistedUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlacklist(user: BlacklistedUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBlacklisted(users: List<BlacklistedUserEntity>)

    @Query("DELETE FROM blacklisted_users WHERE userHandle = :handle")
    suspend fun unblacklistUser(handle: String)

    @Query("DELETE FROM blacklisted_users WHERE id = :id")
    suspend fun deleteBlacklistById(id: Long)

    @Query("SELECT COUNT(*) FROM blacklisted_users")
    suspend fun getBlacklistCount(): Int
}

@Dao
interface ModerationReportDao {
    @Query("SELECT * FROM moderation_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ModerationReportEntity>>

    @Query("SELECT * FROM moderation_reports WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingReports(): Flow<List<ModerationReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ModerationReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReports(reports: List<ModerationReportEntity>)

    @Query("UPDATE moderation_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    @Query("DELETE FROM moderation_reports WHERE id = :id")
    suspend fun deleteReport(id: Long)

    @Query("SELECT COUNT(*) FROM moderation_reports")
    suspend fun getReportsCount(): Int
}

@Dao
interface CreatorPageDao {
    @Query("SELECT * FROM creator_pages ORDER BY createdAt DESC")
    fun getAllPages(): Flow<List<com.example.data.model.CreatorPageEntity>>

    @Query("SELECT * FROM creator_pages WHERE id = :id LIMIT 1")
    suspend fun getPageById(id: Long): com.example.data.model.CreatorPageEntity?

    @Query("SELECT * FROM creator_pages LIMIT 1")
    suspend fun getFirstPage(): com.example.data.model.CreatorPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: com.example.data.model.CreatorPageEntity): Long

    @Query("DELETE FROM creator_pages WHERE id = :id")
    suspend fun deletePage(id: Long)

    @Query("DELETE FROM creator_pages")
    suspend fun deleteAllPages()

    @Query("UPDATE creator_pages SET totalEarnings = totalEarnings + :amount, monthlyRevenue = monthlyRevenue + :amount WHERE id = :id")
    suspend fun addPageEarnings(id: Long, amount: Double)

    @Query("UPDATE creator_pages SET followersCount = followersCount + 1 WHERE id = :id")
    suspend fun incrementPageFollowers(id: Long)
}


