package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.data.AppDatabase
import com.example.data.ReelRepository
import com.example.data.model.CommentEntity
import com.example.data.model.MonetizationEntity
import com.example.data.model.PayoutRecordEntity
import com.example.data.model.ReelEntity
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
        payoutDao = database.payoutDao()
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

    private val _activeComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeComments: StateFlow<List<CommentEntity>> = _activeComments.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(database)
            AdMobManager.initialize(application)
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
}
