package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ReelEntity::class,
        CommentEntity::class,
        MonetizationEntity::class,
        PayoutRecordEntity::class,
        StoryEntity::class,
        BlacklistedUserEntity::class,
        ModerationReportEntity::class,
        CreatorPageEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reelDao(): ReelDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun monetizationDao(): MonetizationDao
    abstract fun payoutDao(): PayoutDao
    abstract fun storyDao(): StoryDao
    abstract fun blacklistDao(): BlacklistDao
    abstract fun moderationReportDao(): ModerationReportDao
    abstract fun creatorPageDao(): CreatorPageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reelvibe_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reelvibe_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val reelDao = database.reelDao()
            val commentDao = database.commentDao()
            val monetizationDao = database.monetizationDao()
            val payoutDao = database.payoutDao()

            // 1. Initial User
            val defaultUser = UserEntity(
                id = "user_main",
                name = "Mohammad Irafan",
                handle = "@irafan_creator",
                email = "mdi715541@gmail.com",
                bio = "✨ Creating daily cinematic reels & vibes! • ReelVibe Creator 🚀",
                followersCount = 18450,
                followingCount = 412,
                totalLikesCount = 125600,
                isMonetizationApproved = true,
                isLoggedIn = true,
                upiId = "irafan@okaxis",
                bankAccount = "•••• 6493",
                ifscCode = "HDFC0001234"
            )
            userDao.insertOrUpdateUser(defaultUser)

            // 2. Initial Monetization Stats
            val defaultMonetization = MonetizationEntity(
                id = 1,
                totalBalance = 24850.00,
                monthlyEarnings = 8420.50,
                totalViews = 1425800L,
                adImpressions = 620400L,
                rpm = 18.50,
                cpm = 32.00,
                watchTimeHours = 3420.0,
                pendingPayout = 4500.00
            )
            monetizationDao.insertOrUpdateStats(defaultMonetization)

            // 3. Initial Payouts History
            val defaultPayouts = listOf(
                PayoutRecordEntity(
                    amount = 5000.00,
                    method = "UPI (irafan@okaxis)",
                    destination = "irafan@okaxis",
                    date = "15 Sep 2026",
                    status = "Completed",
                    transactionRef = "TXN84930211"
                ),
                PayoutRecordEntity(
                    amount = 8200.00,
                    method = "Bank (•••• 6493)",
                    destination = "HDFC Bank",
                    date = "01 Sep 2026",
                    status = "Completed",
                    transactionRef = "TXN79341029"
                ),
                PayoutRecordEntity(
                    amount = 4500.00,
                    method = "UPI (irafan@okaxis)",
                    destination = "irafan@okaxis",
                    date = "20 Sep 2026",
                    status = "Processing",
                    transactionRef = "TXN99281724"
                )
            )
            defaultPayouts.forEach { payoutDao.insertPayout(it) }

            // 4. Initial Trending Reels
            val sampleReels = listOf(
                ReelEntity(
                    id = 1,
                    creatorId = "creator_shadab",
                    creatorName = "Shadab & Hamza",
                    creatorHandle = "mr_shadab253",
                    collaboratorHandle = "mr_hamza11111",
                    caption = "मेरा दिल तोड़ने वाले जरा मेरे सामने तो आ... देसी स्वैग फखरपुर! ❤️🔥",
                    hashtags = "#fakharpur #bahraich #viral #trending #reels #foryou",
                    videoUri = "sample://fakharpur_viral",
                    audioTitle = "Nigahen Kyon Churaati Hai",
                    audioArtist = "Udit Narayan",
                    filterType = "BOLLYWOOD_GLAM",
                    likesCount = 14200,
                    commentsCount = 68,
                    sharesCount = 53,
                    remixesCount = 16,
                    viewsCount = 48500,
                    isLikedByMe = true,
                    isUserUpload = false,
                    location = "अपना फखरपुर बहराइच",
                    videoStickerText = "I ❤️ फखरपुर"
                ),
                ReelEntity(
                    id = 2,
                    creatorId = "creator_anjana",
                    creatorName = "Anjana",
                    creatorHandle = "anjana41805",
                    collaboratorHandle = "",
                    caption = "Al-generated profile • dance vibe 💃 Beautiful evening in Lucknow! ✨",
                    hashtags = "#dance #reels #explore #viral #foryou",
                    videoUri = "sample://anjana_dance",
                    audioTitle = "Kesariya (Dance Mix)",
                    audioArtist = "Arijit Singh",
                    filterType = "GOLDEN_HOUR",
                    likesCount = 51,
                    commentsCount = 6,
                    sharesCount = 53,
                    remixesCount = 3,
                    viewsCount = 1240,
                    isLikedByMe = false,
                    isUserUpload = false,
                    location = "हजरतगंज, लखनऊ, उत्तर प्रदेश",
                    videoStickerText = "✨ Dance Vibe"
                ),
                ReelEntity(
                    id = 3,
                    creatorId = "user_main",
                    creatorName = "Mohammad Irafan",
                    creatorHandle = "@irafan_creator",
                    collaboratorHandle = "",
                    caption = "Neon city nights & fast rides ⚡ Cyberpunk aesthetics shot on mobile! Try this Neon filter! 🔥",
                    hashtags = "#Cyberpunk #NeonVibes #NightLife #ReelsVideo #CreatorStudio",
                    videoUri = "sample://neon_night",
                    audioTitle = "Chaleya (Remix)",
                    audioArtist = "Anirudh Ravichander",
                    filterType = "NEON",
                    likesCount = 85200,
                    commentsCount = 1420,
                    sharesCount = 6120,
                    remixesCount = 42,
                    viewsCount = 342000,
                    isLikedByMe = false,
                    isUserUpload = true,
                    location = "शाह कटरा, जैदपुर, बारा बंकी",
                    videoStickerText = "🔥 Night Vibe"
                ),
                ReelEntity(
                    id = 4,
                    creatorId = "creator_aarav",
                    creatorName = "Aarav Sharma",
                    creatorHandle = "@aarav_cinematics",
                    collaboratorHandle = "",
                    caption = "Mumbai sunsets hit different in 4K 🌅✨ Golden hour magic through my lens! Watch till the end!",
                    hashtags = "#Mumbai #CinematicReel #SunsetVibes #GoldenHour #ReelVibe",
                    videoUri = "sample://mumbai_sunset",
                    audioTitle = "Dil Diyan Gallan (Acoustic Lo-Fi)",
                    audioArtist = "Atif Aslam",
                    filterType = "GOLDEN_HOUR",
                    likesCount = 42800,
                    commentsCount = 890,
                    sharesCount = 3420,
                    remixesCount = 28,
                    viewsCount = 184500,
                    isLikedByMe = true,
                    isUserUpload = false,
                    location = "मरीन ड्राइव, मुंबई, महाराष्ट्र",
                    videoStickerText = "🌅 Mumbai 4K"
                ),
                ReelEntity(
                    id = 5,
                    creatorId = "creator_priya",
                    creatorName = "Priya Kapoor",
                    creatorHandle = "@priya_vibes",
                    collaboratorHandle = "",
                    caption = "Vintage café hopping in old town ☕ Old school warmth and memories that never fade 🤎",
                    hashtags = "#VintageAesthetic #CoffeeLovers #OldTown #RetroVibes",
                    videoUri = "sample://vintage_cafe",
                    audioTitle = "Tum Hi Ho (Unplugged)",
                    audioArtist = "Arijit Singh",
                    filterType = "VINTAGE",
                    likesCount = 29400,
                    commentsCount = 645,
                    sharesCount = 1980,
                    remixesCount = 12,
                    viewsCount = 112000,
                    isLikedByMe = false,
                    isUserUpload = false,
                    location = "दशाश्वमेध घाट, वाराणसी, उत्तर प्रदेश",
                    videoStickerText = "☕ Old Memories"
                )
            )
            reelDao.insertAllReels(sampleReels)

            // Initial Comments
            val initialComments = listOf(
                CommentEntity(
                    reelId = 1,
                    userName = "Rohan Verma",
                    userHandle = "@rohan_v",
                    text = "Bhai the lighting is unreal! Which camera did you use? 🔥",
                    likesCount = 142
                ),
                CommentEntity(
                    reelId = 1,
                    userName = "Sneha Patel",
                    userHandle = "@sneha_p",
                    text = "This soundtrack with the sea link is pure therapy 😍🌊",
                    likesCount = 89
                ),
                CommentEntity(
                    reelId = 2,
                    userName = "Devansh Tech",
                    userHandle = "@dev_coder",
                    text = "The neon filter looks super clean! ReelVibe colors are next level 🚀",
                    likesCount = 215
                ),
                CommentEntity(
                    reelId = 2,
                    userName = "Alia Khan",
                    userHandle = "@alia_k",
                    text = "Keep inspiring bro! Great edits 👏",
                    likesCount = 48
                )
            )
            initialComments.forEach { commentDao.insertComment(it) }

            // 5. Initial Stories / Status
            val storyDao = database.storyDao()
            val sampleStories = listOf(
                StoryEntity(
                    id = 1,
                    creatorId = "user_main",
                    creatorName = "Mohammad Irafan",
                    creatorHandle = "@irafan_creator",
                    mediaType = "PHOTO",
                    mediaUri = "sample://story_irafan",
                    audioTitle = "Nigahen Kyon Churaati Hai",
                    audioArtist = "Udit Narayan",
                    caption = "अपना फखरपुर बहराइच की खूबसूरत सुबह! ☀️❤️",
                    stickerText = "I ❤️ फखरपुर",
                    location = "अपना फखरपुर बहराइच",
                    isUserStory = true,
                    isViewed = false,
                    likesCount = 42
                ),
                StoryEntity(
                    id = 2,
                    creatorId = "creator_priya",
                    creatorName = "Priya Sharma",
                    creatorHandle = "@priya_vibe",
                    mediaType = "PHOTO",
                    mediaUri = "sample://story_priya",
                    audioTitle = "Dil Diyan Gallan (Lo-Fi)",
                    audioArtist = "Acoustic Cafe",
                    caption = "Chai and morning rain vibes in Mumbai ☕🌧️",
                    stickerText = "Morning Vibe ✨",
                    location = "Mumbai, Maharashtra",
                    isUserStory = false,
                    isViewed = false,
                    likesCount = 98
                ),
                StoryEntity(
                    id = 3,
                    creatorId = "creator_kabir",
                    creatorName = "Kabir Khan",
                    creatorHandle = "@kabir_reels",
                    mediaType = "VIDEO",
                    mediaUri = "sample://story_kabir",
                    audioTitle = "Desi Hip Hop • Mumbai Flow",
                    audioArtist = "Gully Beats",
                    caption = "On location shooting the next big reel! 🎥🔥",
                    stickerText = "Viral Mood 🔥",
                    location = "Nawabganj, Uttar Pradesh",
                    isUserStory = false,
                    isViewed = false,
                    likesCount = 135
                ),
                StoryEntity(
                    id = 4,
                    creatorId = "creator_desi",
                    creatorName = "Desi Swag",
                    creatorHandle = "@desi_swag_in",
                    mediaType = "VIDEO",
                    mediaUri = "sample://story_desi",
                    audioTitle = "Bollywood Dhamaka 2026",
                    audioArtist = "DJ Chetas Beats",
                    caption = "Desi beats to start the party! 💃🎉",
                    stickerText = "देसी स्वैग 😎",
                    location = "लखनऊ, उत्तर प्रदेश",
                    isUserStory = false,
                    isViewed = false,
                    likesCount = 210
                )
            )
            storyDao.insertAllStories(sampleStories)

            // 7. Initial Blacklisted Users
            val blacklistDao = database.blacklistDao()
            if (blacklistDao.getBlacklistCount() == 0) {
                val initialBlacklist = listOf(
                    BlacklistedUserEntity(
                        id = 1,
                        userHandle = "@abusive_troll99",
                        userName = "Spam Bot 99",
                        reason = "Abusive Comments & Hate Speech (कमेंट में अभद्र भाषा और गाली-गलौज)",
                        bannedBy = "@irafan_creator (Admin)",
                        isPermanent = true,
                        status = "BANNED"
                    ),
                    BlacklistedUserEntity(
                        id = 2,
                        userHandle = "@toxic_uploader",
                        userName = "Violator Account",
                        reason = "Community Guidelines Violation (कम्युनिटी गाइडलाइंस के खिलाफ वीडियो)",
                        bannedBy = "@irafan_creator (Admin)",
                        isPermanent = true,
                        status = "BANNED"
                    )
                )
                blacklistDao.insertAllBlacklisted(initialBlacklist)
            }

            // 8. Initial Moderation Reports
            val moderationReportDao = database.moderationReportDao()
            if (moderationReportDao.getReportsCount() == 0) {
                val initialReports = listOf(
                    ModerationReportEntity(
                        id = 1,
                        targetType = "COMMENT",
                        targetId = 101,
                        reportedHandle = "@toxic_commenter",
                        reportedName = "Rude User",
                        contentSnippet = "बकवास रील है, फालतू वीडियो बनाना बंद करो #%@!",
                        violationType = "ABUSIVE_LANGUAGE",
                        status = "PENDING"
                    ),
                    ModerationReportEntity(
                        id = 2,
                        targetType = "REEL",
                        targetId = 202,
                        reportedHandle = "@fake_spammer",
                        reportedName = "Scam Links Hub",
                        contentSnippet = "घर बैठे 50000 कमाएं लिंक पर क्लिक करें (Spam scheme)",
                        violationType = "SPAM",
                        status = "PENDING"
                    )
                )
                moderationReportDao.insertAllReports(initialReports)
            }

            // 9. Initial Creator Pages (पेज जिससे लोग पैसे कमाएंगे)
            val creatorPageDao = database.creatorPageDao()
            val initialPage = CreatorPageEntity(
                id = 1,
                ownerHandle = "@irafan_creator",
                pageName = "फखरपुर कॉमेडी रील्स (Official Page)",
                pageHandle = "@fakharpur_comedy_page",
                category = "Comedy & Entertainment",
                bio = "🌟 अपना फखरपुर कॉमेडी क्लब • डेली मस्ती रील्स व जोक्स! 🎬",
                followersCount = 4250,
                totalViews = 89000L,
                totalEarnings = 3850.00,
                monthlyRevenue = 1450.00,
                isMonetizationActive = true,
                upiId = "irafan@okaxis"
            )
            creatorPageDao.insertPage(initialPage)
        }
    }
}
