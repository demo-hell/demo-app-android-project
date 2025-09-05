package br.com.mobicare.cielo.commons.utils.totp

class SystemWallClock : Clock {

    override fun nowMillis(): Long {
        return System.currentTimeMillis()
    }

}