package br.com.mobicare.cielo.security.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.BiometricHelper
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.security.presentation.ui.fragment.SecurityFragment
import kotlinx.android.synthetic.main.security_activity.*

class SecurityActivity : BaseActivity(), BaseActivity.OnBackButtonListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.security_activity)
        setupToolbar(
            toolbarSecurity as Toolbar,
            getString(R.string.menu_security)
        )
        if (BiometricHelper.hasEnrolledFingerprints(this)) {
            setupFragment()
        }else{
            startActivityForResult( Intent(Settings.ACTION_SECURITY_SETTINGS), 0)
            finish()
        }
    }

    fun setupToolbar(toolbar: Toolbar?, toolbarTitle: String = "") {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayShowTitleEnabled(false)

            if (toolbarTitle.isNotBlank()) {
                val toolbarTitleTextView = findViewById<TextView>(R.id.textToolbarMainTitle)
                toolbarTitleTextView?.text = toolbarTitle
            }
        }
    }

    private fun setupFragment() {
        val fragment = SecurityFragment()
        this.supportFragmentManager.beginTransaction().replace(R.id.frameSecurity, fragment)
            .commit()
    }

    override fun onBackTouched() {
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments =  supportFragmentManager.fragments
        val lastFragment: Fragment? = if(fragments!=null && fragments.size>0) fragments.get(fragments.size-1) else null
        lastFragment?.onRequestPermissionsResult(requestCode,  permissions,grantResults)
    }
}