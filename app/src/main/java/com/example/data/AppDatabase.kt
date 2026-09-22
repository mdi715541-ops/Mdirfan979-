package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ReelEntity::class,
        CommentEntity::class,
        MonetizationEntity::class,
        PayoutRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reelDao(): ReelDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun monetizationDao(): MonetizationDao
    abstract fun payoutDao(): PayoutDao

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
                    creatorId = "creator_1",
                    creatorName = "Aarav Sharma",
                    creatorHandle = "@aarav_cinematics",
                    caption = "Mumbai sunsets hit different in 4K 🌅✨ Golden hour magic through my lens! Watch till the end!",
                    hashtags = "#Mumbai #CinematicReel #SunsetVibes #GoldenHour #ReelVibe",
                    videoUri = "sample://mumbai_sunset",
                    audioTitle = "Dil Diyan Gallan (Acoustic Lo-Fi)",
                    audioArtist = "Lo-Fi Collective",
                    filterType = "GOLDEN_HOUR",
                    likesCount = 42800,
                    commentsCount = 890,
                    sharesCount = 3420,
                    viewsCount = 184500,
                    isLikedByMe = true,
                    isUserUpload = false
                ),
                ReelEntity(
                    id = 2,
                    creatorId = "user_main",
                    creatorName = "Mohammad Irafan",
                    creatorHandle = "@irafan_creator",
                    caption = "Neon city nights & fast rides ⚡ Cyberpunk aesthetics shot on mobile! Try this Neon filter! 🔥",
                    hashtags = "#Cyberpunk #NeonVibes #NightLife #ReelsVideo #CreatorStudio",
                    videoUri = "sample://neon_night",
                    audioTitle = "Midnight Drive • Synthwave Beat",
                    audioArtist = "RetroWave Studios",
                    filterType = "NEON",
                    likesCount = 85200,
                    commentsCount = 1420,
                    sharesCount = 6120,
                    viewsCount = 342000,
                    isLikedByMe = false,
                    isUserUpload = true
                ),
                ReelEntity(
                    id = 3,
                    creatorId = "creator_3",
                    creatorName = "Priya Kapoor",
                    creatorHandle = "@priya_vibes",
                    caption = "Vintage café hopping in old town ☕ Old school warmth and memories that never fade 🤎",
                    hashtags = "#VintageAesthetic #CoffeeLovers #OldTown #RetroVibes",
                    videoUri = "sample://vintage_cafe",
                    audioTitle = "Vintage Jazz & Raindrops",
                    audioArtist = "Coffeehouse Beats",
                    filterType = "VINTAGE",
                    likesCount = 29400,
                    commentsCount = 645,
                    sharesCount = 1980,
                    viewsCount = 112000,
                    isLikedByMe = false,
                    isUserUpload = false
                ),
                ReelEntity(
                    id = 4,
                    creatorId = "creator_4",
                    creatorName = "Kabir Fitness",
                    creatorHandle = "@kabir_pro",
                    caption = "Grind in silence, let your reels speak for you! 💪 6AM motivation daily routine 🔥",
                    hashtags = "#GymMotivation #WorkoutRoutine #FitnessReels #NeverQuit",
                    videoUri = "sample://gym_grind",
                    audioTitle = "Unstoppable Bassline Boost",
                    audioArtist = "GymPhonk Records",
                    filterType = "VIBRANT",
                    likesCount = 67300,
                    commentsCount = 1120,
                    sharesCount = 4590,
                    viewsCount = 289000,
                    isLikedByMe = true,
                    isUserUpload = false
                ),
                ReelEntity(
                    id = 5,
                    creatorId = "creator_5",
                    creatorName = "Ananya Arts",
                    creatorHandle = "@ananya_canvas",
                    caption = "Classic black & white portraiture study 🖤 The power of light and shadow in motion.",
                    hashtags = "#NoirArt #BlackAndWhite #FilmStyle #CinematicReel",
                    videoUri = "sample://noir_art",
                    audioTitle = "Nocturne in C Sharp Minor",
                    audioArtist = "Chopin Piano Solo",
                    filterType = "NOIR",
                    likesCount = 38900,
                    commentsCount = 720,
                    sharesCount = 2300,
                    viewsCount = 156000,
                    isLikedByMe = false,
                    isUserUpload = false
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
        }
    }
}
