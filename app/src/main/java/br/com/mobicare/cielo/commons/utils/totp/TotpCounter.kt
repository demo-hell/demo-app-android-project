package br.com.mobicare.cielo.commons.utils.totp

class TotpCounter {

    /** Interval of time (seconds) between successive changes of this counter's value.  */
    private var mTimeStep: Long = 0

    /**
     * Earliest time instant (seconds since UNIX epoch) at which this counter assumes the value of
     * `0`.
     */
    private var mStartTime: Long = 0

    /**
     * Constructs a new `TotpCounter` that starts with the value `0` at time instant
     * `0` (seconds since UNIX epoch) and increments its value with the specified frequency.
     *
     * @param timeStep interval of time (seconds) between successive changes of this counter's value.
     */
    constructor(timeStep: Long, startTime: Long = 0) {
        require(timeStep >= 1) { "Time step must be positive: $timeStep" }
        assertValidTime(startTime)
        mTimeStep = timeStep
        mStartTime = startTime
    }


    /**
     * Gets the frequency with which the value of this counter changes.
     *
     * @return interval of time (seconds) between successive changes of this counter's value.
     */
    fun getTimeStep(): Long {
        return mTimeStep
    }

    /**
     * Gets the earliest time instant at which this counter assumes the value `0`.
     *
     * @return time (seconds since UNIX epoch).
     */
    fun getStartTime(): Long {
        return mStartTime
    }

    /**
     * Gets the value of this counter at the specified time.
     *
     * @param time time instant (seconds since UNIX epoch) for which to obtain the value.
     *
     * @return value of the counter at the `time`.
     */
    fun getValueAtTime(time: Long): Long {
        assertValidTime(time)

        // According to the RFC:
        // T = (Current Unix time - T0) / X, where the default floor function is used.
        //   T  - counter value,
        //   T0 - start time.
        //   X  - time step.

        // It's important to use a floor function instead of simple integer division. For example,
        // assuming a time step of 3:
        // Time since start time: -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6
        // Correct value:         -2 -2 -2 -1 -1 -1  0  0  0  1  1  1  2
        // Simple division / 3:   -2 -1 -1 -1  0  0  0  0  0  1  1  1  2
        //
        // To avoid using Math.floor which requires imprecise floating-point arithmetic, we
        // we compute the value using integer division, but using a different equation for
        // negative and non-negative time since start time.
        val timeSinceStartTime = time - mStartTime
        return if (timeSinceStartTime >= 0) {
            timeSinceStartTime / mTimeStep
        } else {
            (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep
        }
    }

    /**
     * Gets the time when the counter assumes the specified value.
     *
     * @param value value.
     *
     * @return earliest time instant (seconds since UNIX epoch) when the counter assumes the value.
     */
    fun getValueStartTime(value: Long): Long {
        return mStartTime + value * mTimeStep
    }

    private fun assertValidTime(time: Long) {
        require(time >= 0) { "Negative time: $time" }
    }

}