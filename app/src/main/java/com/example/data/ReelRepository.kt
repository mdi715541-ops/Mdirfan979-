package com.example.data

import com.example.data.dao.BlacklistDao
import com.example.data.dao.CommentDao
import com.example.data.dao.CreatorPageDao
import com.example.data.dao.ModerationReportDao
import com.example.data.dao.MonetizationDao
import com.example.data.dao.PayoutDao
import com.example.data.dao.ReelDao
import com.example.data.dao.StoryDao
import com.example.data.dao.UserDao
import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CreatorPageEntity
import com.example.data.model.ModerationReportEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
import com.example.data.model.StoryEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class ReelRepository(
    private val reelDao: ReelDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val monetizationDao: MonetizationDao,
    private val payoutDao: PayoutDao,
    private val storyDao: StoryDao,
    private val blacklistDao: BlacklistDao,
    private val moderationReportDao: ModerationReportDao,
    private val creatorPageDao: CreatorPageDao
) {
    val allReels: Flow<List<ReelEntity>> = reelDao.getAllReels()
    val userUploadedReels: Flow<List<ReelEntity>> = reelDao.getUserUploadedReels()
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()
    val monetizationStats: Flow<MonetizationEntity?> = monetizationDao.getMonetizationStats()
    val allPayouts: Flow<List<PayoutRecordEntity>> = payoutDao.getAllPayouts()
    val allStories: Flow<List<StoryEntity>> = storyDao.getAllStories()
    val allBlacklistedUsers: Flow<List<BlacklistedUserEntity>> = blacklistDao.getAllBlacklistedUsers()
    val allReports: Flow<List<ModerationReportEntity>> = moderationReportDao.getAllReports()
    val pendingReports: Flow<List<ModerationReportEntity>> = moderationReportDao.getPendingReports()
    val allPages: Flow<List<CreatorPageEntity>> = creatorPageDao.getAllPages()

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

    suspend fun remixReel(reelId: Long) {
        reelDao.incrementRemixes(reelId)
    }

    suspend fun toggleSaveReel(reelId: Long, isSaved: Boolean) {
        reelDao.toggleSave(reelId, isSaved)
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
        // Purge all created pages
        creatorPageDao.deleteAllPages()
    }

    // Password reset / forget feature (पासवर्ड भूल जाने पर नया पासवर्ड सेट करने का ऑप्शन)
    suspend fun resetPassword(email: String, newPassword: String) {
        userDao.updatePassword(email, newPassword)
    }

    // Creator Page Methods (पेज बनाने व सामान्य करने का ऑप्शन - जिससे लोग पैसे कमाएंगे)
    suspend fun createPage(
        pageName: String,
        category: String,
        bio: String,
        upiId: String
    ): Long {
        val sanitizedHandle = "@" + pageName.lowercase().replace(" ", "_").filter { it.isLetterOrDigit() || it == '_' }
        val newPage = CreatorPageEntity(
            pageName = pageName.trim(),
            pageHandle = sanitizedHandle,
            category = category,
            bio = bio.ifBlank { "Official Creator Page • मनोरंजन और नई रील्स! 🚀" },
            followersCount = 100, // Starting welcoming community
            totalViews = 1500L,
            totalEarnings = 50.0, // Welcome creator bonus!
            monthlyRevenue = 50.0,
            isMonetizationActive = true,
            upiId = upiId.ifBlank { "irafan@okaxis" }
        )
        val pageId = creatorPageDao.insertPage(newPage)
        // Automatically switch user to newly created page
        userDao.updatePageMode(
            isPageMode = true,
            pageId = pageId,
            pageName = newPage.pageName,
            category = newPage.category,
            followers = newPage.followersCount,
            earnings = newPage.totalEarnings
        )
        return pageId
    }

    suspend fun switchToPage(page: CreatorPageEntity) {
        userDao.updatePageMode(
            isPageMode = true,
            pageId = page.id,
            pageName = page.pageName,
            category = page.category,
            followers = page.followersCount,
            earnings = page.totalEarnings
        )
    }

    suspend fun switchToNormalProfile() {
        userDao.resetToNormalProfile()
    }

    suspend fun deletePage(pageId: Long) {
        creatorPageDao.deletePage(pageId)
        userDao.resetToNormalProfile()
    }

    suspend fun addPageEarnings(pageId: Long, amount: Double) {
        creatorPageDao.addPageEarnings(pageId, amount)
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

    suspend fun addStory(story: StoryEntity): Long = storyDao.insertStory(story)
    suspend fun markStoryAsViewed(storyId: Long) = storyDao.markStoryAsViewed(storyId)
    suspend fun likeStory(storyId: Long) = storyDao.likeStory(storyId)
    suspend fun deleteStory(storyId: Long) = storyDao.deleteStory(storyId)

    // Blacklist & Moderation methods
    suspend fun blacklistUser(
        handle: String,
        name: String,
        reason: String,
        bannedBy: String = "@irafan_creator (Admin)",
        isPermanent: Boolean = true
    ) {
        val entry = BlacklistedUserEntity(
            userHandle = handle,
            userName = name,
            reason = reason,
            bannedBy = bannedBy,
            isPermanent = isPermanent,
            status = "BANNED"
        )
        blacklistDao.insertBlacklist(entry)
        // Clean up any abusive comments by this user
        commentDao.deleteCommentsByUser(handle)
    }

    suspend fun unblacklistUser(handle: String) {
        blacklistDao.unblacklistUser(handle)
    }

    suspend fun removeBlacklistEntry(id: Long) {
        blacklistDao.deleteBlacklistById(id)
    }

    suspend fun deleteComment(commentId: Long) {
        commentDao.deleteComment(commentId)
    }

    suspend fun deleteCommentAndBanUser(
        commentId: Long,
        userHandle: String,
        userName: String,
        reason: String
    ) {
        commentDao.deleteComment(commentId)
        blacklistUser(
            handle = userHandle,
            name = userName,
            reason = reason
        )
    }

    suspend fun submitModerationReport(
        targetType: String,
        targetId: Long,
        reportedHandle: String,
        reportedName: String,
        contentSnippet: String,
        violationType: String
    ): Long {
        val report = ModerationReportEntity(
            targetType = targetType,
            targetId = targetId,
            reportedHandle = reportedHandle,
            reportedName = reportedName,
            contentSnippet = contentSnippet,
            violationType = violationType,
            status = "PENDING"
        )
        return moderationReportDao.insertReport(report)
    }

    suspend fun resolveReportAndBan(
        reportId: Long,
        reportedHandle: String,
        reportedName: String,
        reason: String
    ) {
        moderationReportDao.updateReportStatus(reportId, "RESOLVED_BANNED")
        blacklistUser(
            handle = reportedHandle,
            name = reportedName,
            reason = reason
        )
    }

    suspend fun dismissReport(reportId: Long) {
        moderationReportDao.updateReportStatus(reportId, "DISMISSED")
    }
}
