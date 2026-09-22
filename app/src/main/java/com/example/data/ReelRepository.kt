package com.example.data

import com.example.data.dao.CommentDao
import com.example.data.dao.MonetizationDao
import com.example.data.dao.PayoutDao
import com.example.data.dao.ReelDao
import com.example.data.dao.UserDao
import com.example.data.model.CommentEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class ReelRepository(
    private val reelDao: ReelDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val monetizationDao: MonetizationDao,
    private val payoutDao: PayoutDao
) {
    val allReels: Flow<List<ReelEntity>> = reelDao.getAllReels()
    val userUploadedReels: Flow<List<ReelEntity>> = reelDao.getUserUploadedReels()
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()
    val monetizationStats: Flow<MonetizationEntity?> = monetizationDao.getMonetizationStats()
    val allPayouts: Flow<List<PayoutRecordEntity>> = payoutDao.getAllPayouts()

    suspend fun seedInitialDataIfEmpty(database: AppDatabase) {
        if (userDao.getCurrentUserSync() == null) {
            AppDatabase.populateInitialData(database)
        }
    }

    fun getCommentsForReel(reelId: Long): Flow<List<CommentEntity>> =
        commentDao.getCommentsForReel(reelId)

    suspend fun insertReel(reel: ReelEntity): Long = reelDao.insertReel(reel)
    suspend fun publishReel(reel: ReelEntity): Long = reelDao.insertReel(reel)

    suspend fun deleteReel(reelId: Long) {
        reelDao.deleteReelById(reelId)
        commentDao.deleteCommentsForReel(reelId)
    }

    suspend fun toggleLike(reelId: Long, isLikedCurrently: Boolean) {
        val delta = if (isLikedCurrently) -1 else 1
        reelDao.toggleLike(reelId, delta, !isLikedCurrently)
    }

    suspend fun toggleLikeReel(reelId: Long, isLiked: Boolean) {
        val delta = if (isLiked) 1 else -1
        reelDao.toggleLike(reelId, delta, isLiked)
    }

    suspend fun recordView(reelId: Long) {
        reelDao.incrementViews(reelId)
    }

    suspend fun recordReelView(reelId: Long) {
        reelDao.incrementViews(reelId)
    }

    suspend fun recordShare(reelId: Long) {
        reelDao.incrementShares(reelId)
    }

    suspend fun recordReelShare(reelId: Long) {
        reelDao.incrementShares(reelId)
    }

    suspend fun markDownloaded(reelId: Long) {
        reelDao.markAsDownloaded(reelId)
    }

    suspend fun downloadReel(reelId: Long) {
        reelDao.markAsDownloaded(reelId)
    }

    suspend fun addComment(reelId: Long, userName: String, userHandle: String, text: String): Long {
        val comment = CommentEntity(
            reelId = reelId,
            userName = userName,
            userHandle = userHandle,
            text = text
        )
        val id = commentDao.insertComment(comment)
        reelDao.incrementCommentsCount(reelId)
        return id
    }

    suspend fun likeComment(commentId: Long) {
        commentDao.likeComment(commentId)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
    }

    suspend fun saveUser(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
    }

    // Complete account deletion requirement (अकाउंट डिलीट करने का ऑप्शन)
    suspend fun deleteAccountAndData() {
        // Purge user profile
        userDao.deleteUser()
        // Purge user's uploaded reels
        reelDao.deleteAllUserReels()
        // Purge monetization and payouts
        monetizationDao.clearStats()
        payoutDao.clearPayouts()
    }

    suspend fun reinitializeUser(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
        monetizationDao.insertOrUpdateStats(MonetizationEntity())
    }

    suspend fun addAdReward(amount: Double, impressions: Long = 1) {
        monetizationDao.addAdReward(amount, impressions)
    }

    suspend fun addAdRevenue(amount: Double, impressions: Long = 1) {
        monetizationDao.addAdReward(amount, impressions)
    }

    suspend fun requestPayout(amount: Double, method: String, destination: String): Boolean {
        val stats = monetizationDao.getMonetizationStatsSync()
        if (stats != null && stats.totalBalance >= amount && amount > 0) {
            monetizationDao.deductBalance(amount)
            val record = PayoutRecordEntity(
                amount = amount,
                method = method,
                destination = destination,
                date = "Today",
                status = "Processing"
            )
            payoutDao.insertPayout(record)
            return true
        }
        return false
    }
}
