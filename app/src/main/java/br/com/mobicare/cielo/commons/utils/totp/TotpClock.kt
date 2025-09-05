package br.com.mobicare.cielo.commons.utils.totp

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.preference.PreferenceManager
import java.util.concurrent.TimeUnit


class TotpClock : Clock {

    @VisibleForTesting
    val PREFERENCE_KEY_OFFSET_MINUTES = "timeCorrectionMinutes"

    private var mSystemWallClock: Clock? = null
    private var mPreferences: SharedPreferences? = null

    private val mLock = Any()

    /**
     * Cached value of time correction (in minutes) or `null` if not cached. The value is cached
     * because it's read very frequently (once every 100ms) and is modified very infrequently.
     *
     * @GuardedBy [.mLock]
     */
    private var mCachedCorrectionMinutes: Int? = null

    constructor(context: Context?, systemWallClock: Clock?) {
        mSystemWallClock = systemWallClock
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun nowMillis(): Long {
        return mSystemWallClock!!.nowMillis() + getTimeCorrectionMinutes() * TimeUnit.MINUTES
                .toMillis(1)
    }

    /**
     * Gets the currently used time correction value.
     *
     * @return number of minutes by which this device is behind the correct time.
     */
    fun getTimeCorrectionMinutes(): Int {
        synchronized(mLock) {
            if (mCachedCorrectionMinutes == null) {
                mCachedCorrectionMinutes = mPreferences!!.getInt(PREFERENCE_KEY_OFFSET_MINUTES, 0)
            }
            return mCachedCorrectionMinutes!!
        }
    }

    /**
     * Sets the currently used time correction value.
     *
     * @param minutes number of minutes by which this device is behind the correct time.
     */
    fun setTimeCorrectionMinutes(minutes: Int) {
        synchronized(mLock) {
            mPreferences!!.edit().putInt(PREFERENCE_KEY_OFFSET_MINUTES, minutes).apply()
            // Invalidate the cache to force reading actual settings from time to time
            mCachedCorrectionMinutes = null
        }
    }

    /**
     * Gets the system "wall" clock on top of this this TOTP clock operates.
     */
    fun getSystemWallClock(): Clock? {
        return mSystemWallClock
    }

}