package br.com.mobicare.cielo.mfa.token

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.DeviceInfoPreferences
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.secure.presentation.ui.fragment.OtpRegisterFragment
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.addInFrame
import com.ca.mobile.riskminder.RMDeviceInventory
import com.ca.mobile.riskminder.RMDeviceInventoryImpl
import com.ca.mobile.riskminder.RMDeviceInventoryResponseCallBack
import com.ca.mobile.riskminder.RMError
import kotlinx.android.synthetic.main.activity_token_generator.*

class TokenGeneratorActivity : BaseLoggedActivity(), RMDeviceInventoryResponseCallBack,
        BaseActivity.OnBackButtonListener {


    val currentUsername: String? by lazy {
        intent?.getStringExtra(USERNAME_KEY)
    }

    companion object {
        const val USERNAME_KEY: String = "br.com.cielo.mfa.usernameKey"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_generator)

        val rmDevice = RMDeviceInventoryImpl
                .getDeviceInventoryInstance(this, RMDeviceInventory.DDNA_Mode.SDK)
        rmDevice.collectDeviceDNA(this)

        OtpRegisterFragment.create().addInFrame(supportFragmentManager,
                R.id.framePinContent)

        setupToolbar(toolbarTokenGenerator as Toolbar, "Token")
        onBackButtonListener = this
    }

    override fun deleteRMDeviceId() = Unit
    override fun storeRMDeviceId(p0: String?) = Unit
    override fun getRMDeviceId() = ""

    override fun onResponse(deviceDna: String?, p1: RMError?) {
        //TODO DNA do dispositivo
        deviceDna?.let { deviceDnaArg ->
            DeviceInfoPreferences.saveDeviceDna(deviceDnaArg)
        }
    }

    override fun onBackTouched() {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(CLOSE_ACTIVITIES_FROM_BACKSTACK))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onBackTouched()
    }
}