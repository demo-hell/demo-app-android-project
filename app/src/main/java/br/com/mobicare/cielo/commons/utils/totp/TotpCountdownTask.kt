package br.com.mobicare.cielo.commons.utils.totp

import android.os.Handler
import java.util.concurrent.TimeUnit

const val OTP_EXPIRATION_TIME = 500L
const val OTP_RESET_TIME = 500

class TotpCountdownTask : Runnable {

    private var counter: TotpCounter? = null
    private var clock: Clock? = null
    private var remainingTimeNotificationPeriod: Long = 0
    private val handler: Handler = Handler()
    private var firstTimeOpened = true
    private var isOtpRecentlyGenerated = false
    private var lastSeenCounterValue: Long = Long.MIN_VALUE
    private var shouldStop = false
    private var totpEventsListener: Listener? = null

    /**
     * Listener notified of changes to the time remaining until the counter value changes.
     */
    interface Listener {
        /**
         * Invoked when the time remaining till the TOTP counter changes its value.
         *
         * @param millisRemaining time (milliseconds) remaining.
         */
        fun onTotpCountdown(millisRemaining: Long)

        /** Invoked when the TOTP counter changes its value.  */
        fun onTotpCounterValueChanged()

        /**
         * Responsible for generating a new OTP code.
         * It's invoked when the validity of the current OTP code expires.
         */
        fun onGenerateNewOtpCode(shouldManipulateTime: Boolean)
    }

    /**
     * Constructs a new `TotpRefreshTask`.
     *
     * @param counter TOTP counter this task monitors.
     * @param clock TOTP clock that drives this task.
     * @param remainingTimeNotificationPeriod approximate interval (milliseconds) at which this task
     * notifies its listener about the time remaining until the @{code counter} changes its
     * value.
     */
    constructor(counter: TotpCounter?, clock: TotpClock?,
                          remainingTimeNotificationPeriod: Long) {
        this.counter = counter
        this.clock = clock
        this.remainingTimeNotificationPeriod = remainingTimeNotificationPeriod
    }

    /**
     * Sets the listener that this task will periodically notify about the state of the TOTP counter.
     *
     * @param listener listener or `null` for no listener.
     */
    fun setListener(listener: Listener?) {
        this.totpEventsListener = listener
    }

    /**
     * Starts this task and immediately notifies the listener that the counter value has changed.
     *
     *
     * The immediate notification during startup ensures that the listener does not miss any
     * updates.
     *
     * @throws IllegalStateException if the task has already been stopped.
     */
    fun startAndNotifyListener() {
        check(!shouldStop) { "Task already stopped and cannot be restarted." }
        run()
    }

    /**
     * Stops this task. This task will never notify the listener after the task has been stopped.
     */
    fun stop() {
        shouldStop = true
    }

    override fun run() {
        if (shouldStop) {
            return
        }
        val now = clock!!.nowMillis()
        val counterValue = getCounterValue(now)
        if (lastSeenCounterValue != counterValue) {
            lastSeenCounterValue = counterValue!!
            fireTotpCounterValueChanged()
        }
        fireTotpCountdown(getTimeTillNextCounterValue(now))
        scheduleNextInvocation()
    }

    private fun scheduleNextInvocation() {
        val now = clock!!.nowMillis()
        val counterValueAge = getCounterValueAge(now)
        val timeTillNextInvocation = remainingTimeNotificationPeriod - counterValueAge % remainingTimeNotificationPeriod
        handler.postDelayed(this, timeTillNextInvocation)
    }

    private fun fireTotpCountdown(timeRemaining: Long) {
        totpEventsListener?.let { listener ->
            if (!shouldStop) {
                listener.onTotpCountdown(timeRemaining)
                handleOtpGeneration(timeRemaining, listener)
            }
        }
    }

    /**
     * Handles the generation of the OTP based on the remaining time and whether it's the first time the flow is opened.
     *
     * @param timeRemaining The remaining time in milliseconds before the OTP resets.
     * @param listener The listener to be notified when the OTP is generated.
     *
     * If it's the first time the app is opened, an OTP is generated with time manipulation.
     * If the remaining time is less than or equal to the reset time and an OTP has not been recently generated, an OTP is generated without time manipulation.
     * If the remaining time is more than the reset time, the flag for recent OTP generation is reset.
     */
    private fun handleOtpGeneration(timeRemaining: Long, listener: Listener) {
        if (firstTimeOpened) {
            generateOtpCode(listener, true)
        } else if (timeRemaining <= OTP_RESET_TIME && isOtpRecentlyGenerated.not()) {
            generateOtpCode(listener, false)
        } else if (timeRemaining > OTP_RESET_TIME) {
            isOtpRecentlyGenerated = false
        }
    }

    private fun generateOtpCode(listener: Listener, shouldManipulateTime: Boolean = false) {
        isOtpRecentlyGenerated = true
        firstTimeOpened = false
        listener.onGenerateNewOtpCode(shouldManipulateTime)
    }

    private fun fireTotpCounterValueChanged() {
        if (totpEventsListener != null && !shouldStop) {
            totpEventsListener!!.onTotpCounterValueChanged()
        }
    }

    /**
     * Gets the value of the counter at the specified time instant.
     *
     * @param time time instant (milliseconds since epoch).
     */
    private fun getCounterValue(time: Long): Long? {
        return counter?.getValueAtTime(TimeUnit.MILLISECONDS.toSeconds(time))
    }

    /**
     * Gets the time remaining till the counter assumes its next value.
     *
     * @param time time instant (milliseconds since epoch) for which to perform the query.
     *
     * @return time (milliseconds) till next value.
     */
    private fun getTimeTillNextCounterValue(time: Long): Long {
        val currentValue = getCounterValue(time)
        val nextValue = currentValue?.plus(1)
        val nextValueStartTime: Long = TimeUnit.SECONDS
                .toMillis(counter?.getValueStartTime(nextValue!!)!!)
        return nextValueStartTime - time
    }

    /**
     * Gets the age of the counter value at the specified time instant.
     *
     * @param time time instant (milliseconds since epoch).
     *
     * @return age (milliseconds).
     */
    private fun getCounterValueAge(time: Long): Long {
        return time - TimeUnit.SECONDS.toMillis(counter?.getValueStartTime(getCounterValue(time)!!)!!)
    }

}