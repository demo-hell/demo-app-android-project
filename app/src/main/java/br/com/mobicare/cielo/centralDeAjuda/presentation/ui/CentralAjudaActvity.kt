package br.com.mobicare.cielo.centralDeAjuda.presentation.ui

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaFragment
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.CentralAjudaActivityBinding

class CentralAjudaActvity : BaseActivity() {
    lateinit var screen: String
    lateinit var merchantId: String
    private lateinit var binding: CentralAjudaActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.central_ajuda_activity)
        setupToolbar(binding.toolbar.toolbarMain, getString(R.string.menu_central_ajuda))

        intent.apply {
            screen = this.extras!!.getString(CentralAjudaFragment.SCREEN_NAME)!!
            merchantId = this.extras!!.getString(CentralAjudaFragment.MERCHANT_ID)!!
        }
        configureBack()
        setFragment()
    }

    private fun configureBack() {
        binding.toolbar.toolbarMain.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame,
            CentralAjudaFragment.create(
                screenPath = "/CentralAjuda",
                merchantId = merchantId,
            ),
        )
        transaction.commit()
    }
}
