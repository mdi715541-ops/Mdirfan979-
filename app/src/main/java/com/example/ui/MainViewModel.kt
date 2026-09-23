package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.data.AppDatabase
import com.example.data.ReelRepository
import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CreatorPageEntity
import com.example.data.model.ModerationReportEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
import com.example.data.model.StoryEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = ReelRepository(
        reelDao = database.reelDao(),
        userDao = database.userDao(),
        commentDao = database.commentDao(),
        monetizationDao = database.monetizationDao(),
        payoutDao = database.payoutDao(),
        storyDao = database.storyDao(),
        blacklistDao = database.blacklistDao(),
        moderationReportDao = database.moderationReportDao(),
        creatorPageDao = database.creatorPageDao()
    )

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allReels: StateFlow<List<ReelEntity>> = repository.allReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userUploadedReels: StateFlow<List<ReelEntity>> = repository.userUploadedReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monetizationStats: StateFlow<MonetizationEntity?> = repository.monetizationStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val payouts: StateFlow<List<PayoutRecordEntity>> = repository.allPayouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<StoryEntity>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBlacklistedUsers: StateFlow<List<BlacklistedUserEntity>> = repository.allBlacklistedUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ModerationReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReports: StateFlow<List<ModerationReportEntity>> = repository.pendingReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPages: StateFlow<List<CreatorPageEntity>> = repository.allPages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _followedCreators = MutableStateFlow<Set<String>>(setOf("@priya_vibe"))
    val followedCreators: StateFlow<Set<String>> = _followedCreators.asStateFlow()

    private val _activeComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeComments: StateFlow<List<CommentEntity>> = _activeComments.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(database)
            AdMobManager.initialize(application)
        }
    }

    fun toggleFollowCreator(handle: String) {
        viewModelScope.launch {
            val current = _followedCreators.value.toMutableSet()
            val isNowFollowing = !current.contains(handle)
            if (isNowFollowing) {
                current.add(handle)
            } else {
                current.remove(handle)
            }
            _followedCreators.value = current

            currentUser.value?.let { user ->
                val delta = if (isNowFollowing) 1 else -1
                val updatedFollowing = (user.followingCount + delta).coerceAtLeast(0)
                repository.updateUser(user.copy(followingCount = updatedFollowing))
            }
        }
    }

    fun toggleLike(reelId: Long, currentLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLikeReel(reelId, !currentLiked)
        }
    }

    fun recordView(reelId: Long) {
        viewModelScope.launch {
            repository.recordReelView(reelId)
        }
    }

    fun recordShare(reelId: Long) {
        viewModelScope.launch {
            repository.recordReelShare(reelId)
        }
    }

    fun downloadReel(reelId: Long) {
        viewModelScope.launch {
            repository.downloadReel(reelId)
        }
    }

    fun toggleSaveReel(reelId: Long, currentSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveReel(reelId, !currentSaved)
        }
    }

    fun remixReel(reelId: Long) {
        viewModelScope.launch {
            repository.remixReel(reelId)
        }
    }

    fun deleteReel(reelId: Long) {
        viewModelScope.launch {
            repository.deleteReel(reelId)
        }
    }

    fun loadComments(reelId: Long) {
        viewModelScope.launch {
            repository.getCommentsForReel(reelId).collect {
                _activeComments.value = it
            }
        }
    }

    fun addComment(reelId: Long, text: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val userName = user?.name ?: "Mohammad Irafan"
            val userHandle = user?.handle ?: "@irafan_creator"
            repository.addComment(reelId, userName, userHandle, text)
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    fun publishReel(reel: ReelEntity) {
        viewModelScope.launch {
            repository.publishReel(reel)
        }
    }

    fun updateProfile(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            repository.deleteAccountAndData()
        }
    }

    fun logout() {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                repository.updateUser(user.copy(isLoggedIn = false))
            }
        }
    }

    fun login(user: UserEntity) {
        viewModelScope.launch {
            repository.saveUser(user.copy(isLoggedIn = true))
        }
    }

    fun requestPayout(amount: Double, method: String, destination: String) {
        viewModelScope.launch {
            repository.requestPayout(amount, method, destination)
        }
    }

    fun addAdReward(amount: Double, impressionDelta: Long) {
        viewModelScope.launch {
            repository.addAdRevenue(amount, impressionDelta)
        }
    }

    fun addStory(
        mediaType: String,
        mediaUri: String,
        audioTitle: String,
        audioArtist: String,
        caption: String,
        stickerText: String,
        location: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val userName = user?.name ?: "Mohammad Irafan"
            val userHandle = user?.handle ?: "@irafan_creator"
            val story = StoryEntity(
                creatorId = "user_main",
                creatorName = userName,
                creatorHandle = userHandle,
                mediaType = mediaType,
                mediaUri = mediaUri,
                audioTitle = audioTitle.ifBlank { "Original Sound" },
                audioArtist = audioArtist.ifBlank { "ReelVibe Beats" },
                caption = caption,
                stickerText = stickerText,
                location = location,
                isUserStory = true,
                isViewed = false
            )
            repository.addStory(story)
        }
    }

    fun addReelToStory(reel: ReelEntity) {
        viewModelScope.launch {
            val user = currentUser.value
            val userName = user?.name ?: "Mohammad Irafan"
            val userHandle = user?.handle ?: "@irafan_creator"
            val story = StoryEntity(
                creatorId = "user_main",
                creatorName = userName,
                creatorHandle = userHandle,
                mediaType = "VIDEO",
                mediaUri = reel.videoUri,
                audioTitle = reel.audioTitle,
                audioArtist = reel.audioArtist,
                caption = "Watching @${reel.creatorHandle}: ${reel.caption}",
                stickerText = if (reel.videoStickerText.isNotBlank()) reel.videoStickerText else "Viral Reel ✨",
                location = if (reel.location.isNotBlank()) reel.location else "India",
                isUserStory = true,
                isViewed = false
            )
            repository.addStory(story)
        }
    }

    fun markStoryViewed(storyId: Long) {
        viewModelScope.launch {
            repository.markStoryAsViewed(storyId)
        }
    }

    fun likeStory(storyId: Long) {
        viewModelScope.launch {
            repository.likeStory(storyId)
        }
    }

    fun deleteStory(storyId: Long) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
        }
    }

    // Blacklist & Moderation Actions
    fun blacklistUser(
        handle: String,
        name: String,
        reason: String,
        bannedBy: String = "@irafan_creator (Admin)"
    ) {
        viewModelScope.launch {
            repository.blacklistUser(
                handle = handle,
                name = name,
                reason = reason,
                bannedBy = bannedBy
            )
        }
    }

    fun unblacklistUser(handle: String) {
        viewModelScope.launch {
            repository.unblacklistUser(handle)
        }
    }

    fun removeBlacklistEntry(id: Long) {
        viewModelScope.launch {
            repository.removeBlacklistEntry(id)
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            // Refresh comments if needed
            val updated = _activeComments.value.filterNot { it.id == commentId }
            _activeComments.value = updated
        }
    }

    fun deleteCommentAndBanUser(
        commentId: Long,
        userHandle: String,
        userName: String,
        reason: String
    ) {
        viewModelScope.launch {
            repository.deleteCommentAndBanUser(
                commentId = commentId,
                userHandle = userHandle,
                userName = userName,
                reason = reason
            )
            val updated = _activeComments.value.filterNot { it.id == commentId || it.userHandle == userHandle }
            _activeComments.value = updated
        }
    }

    fun submitReport(
        targetType: String,
        targetId: Long,
        reportedHandle: String,
        reportedName: String,
        contentSnippet: String,
        violationType: String
    ) {
        viewModelScope.launch {
            repository.submitModerationReport(
                targetType = targetType,
                targetId = targetId,
                reportedHandle = reportedHandle,
                reportedName = reportedName,
                contentSnippet = contentSnippet,
                violationType = violationType
            )
        }
    }

    fun resolveReportAndBan(
        reportId: Long,
        reportedHandle: String,
        reportedName: String,
        reason: String
    ) {
        viewModelScope.launch {
            repository.resolveReportAndBan(
                reportId = reportId,
                reportedHandle = reportedHandle,
                reportedName = reportedName,
                reason = reason
            )
        }
    }

    fun dismissReport(reportId: Long) {
        viewModelScope.launch {
            repository.dismissReport(reportId)
        }
    }

    fun reportReel(reel: com.example.data.model.ReelEntity, violationType: String = "COMMUNITY_GUIDELINE_VIOLATION") {
        submitReport(
            targetType = "REEL",
            targetId = reel.id,
            reportedHandle = reel.creatorHandle,
            reportedName = reel.creatorName,
            contentSnippet = "रील: ${reel.caption.take(80)}",
            violationType = violationType
        )
    }

    // Page Management Actions (पेज बनाने व सामान्य करने का ऑप्शन - जिससे लोग पैसे कमाएंगे)
    fun createPage(
        pageName: String,
        category: String,
        bio: String,
        upiId: String,
        onComplete: ((Long) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val pageId = repository.createPage(pageName, category, bio, upiId)
            onComplete?.invoke(pageId)
        }
    }

    fun switchToPage(page: CreatorPageEntity) {
        viewModelScope.launch {
            repository.switchToPage(page)
        }
    }

    fun switchToNormalProfile() {
        viewModelScope.launch {
            repository.switchToNormalProfile()
        }
    }

    fun deletePage(pageId: Long) {
        viewModelScope.launch {
            repository.deletePage(pageId)
        }
    }

    // Password reset / forget feature (पासवर्ड भूल जाने पर नया पासवर्ड सेट करने का ऑप्शन)
    fun resetPassword(email: String, newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.resetPassword(email, newPassword)
            onResult(true)
        }
    }
}
