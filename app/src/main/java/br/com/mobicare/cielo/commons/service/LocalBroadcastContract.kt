package br.com.mobicare.cielo.commons.service

import android.content.Intent

interface LocalBroadcastContract {
    fun action(intent: Intent)
}