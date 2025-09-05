package br.com.mobicare.cielo.debitoEmConta

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import kotlinx.android.synthetic.main.debito_em_conta_activity.*

class DebitoEmContaActivity : BaseLoggedActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debito_em_conta_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_sub_menu_authorization))

        errorToggle.configureActionClickListener(View.OnClickListener {
            finish()
        })

    }
}