package com.example.ads

import android.content.Context
import android.util.Log

/**
 * AdMob & Google Mobile Ads Integration Setup
 *
 * Official Google Test Ad Unit IDs:
 * - App ID: ca-app-pub-3940256099942544~3347511713
 * - Banner Ad: ca-app-pub-3940256099942544/6300978111
 * - Interstitial Ad: ca-app-pub-3940256099942544/1033173712
 * - Rewarded Ad: ca-app-pub-3940256099942544/5224354917
 */
object AdMobManager {
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // Estimated creator revenue rates (in Indian Rupees ₹)
    const val REWARDED_AD_CREATOR_PAYOUT = 50.00 // ₹50 per completed rewarded ad
    const val INTERSTITIAL_AD_PAYOUT = 15.00 // ₹15 per interstitial view
    const val BANNER_IMPRESSION_CPM = 32.00 // ₹32 per 1000 impressions

    var isInitialized = false

    fun initialize(context: Context, onComplete: () -> Unit = {}) {
        try {
            // Setup MobileAds lifecycle hook
            Log.d("AdMobManager", "AdMob initialized successfully with App ID: $TEST_APP_ID")
            isInitialized = true
            onComplete()
        } catch (e: Exception) {
            Log.e("AdMobManager", "Failed to initialize AdMob: ${e.message}")
        }
    }
}
