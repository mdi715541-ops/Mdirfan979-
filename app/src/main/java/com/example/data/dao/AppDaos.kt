package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CommentEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
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
