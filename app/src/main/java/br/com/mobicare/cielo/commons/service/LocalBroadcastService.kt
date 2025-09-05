package br.com.mobicare.cielo.commons.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocalBroadcastService(var contract: LocalBroadcastContract)  : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

       // val message = intent?.getStringExtra("message")
        intent?.let {
            contract.action(it)
        }

    }


}