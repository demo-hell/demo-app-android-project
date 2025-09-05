package br.com.mobicare.cielo.newLogin.updateregister

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import kotlinx.android.synthetic.main.activity_on_board_update_register.*

class OnBoardUpdateRegister : BaseActivity() {

    private val PHONE_NUMBER = "tel:40025472"
    private val PHONE_0800_NUMBER = "tel:08005708472"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board_update_register)
        initOnClick()
    }

    private fun initOnClick() {
        phoneNumber.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply { data = Uri.parse(PHONE_NUMBER) })
        }
        phoneNumber0800.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply { data = Uri.parse(PHONE_0800_NUMBER) })
        }
        logoutButton.setOnClickListener {
            SessionExpiredHandler.userSessionExpires(this)
            finish()
        }
    }

    override fun onBackPressed() = Unit
}
