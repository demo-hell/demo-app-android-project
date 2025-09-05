package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.MeuCadastroNovoAdapter
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.meu_cadastro_fragment.*
import kotlinx.android.synthetic.main.meu_cadastro_fragment.toolbar
import kotlinx.android.synthetic.main.toolbar_blue.*

class MeuCadastroActivity : BaseActivity(), MeuCadastroContract.View, ListenerCadastroScreen {

    private var disposableDefault: DisposableDefault? = null
    private var fragmentAdapter: MeuCadastroNovoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(window)
        setContentView(R.layout.meu_cadastro_fragment)
        toolbarCustom()
    }


    companion object {
        const val MEU_CADASTRO_ACTIVITY = "br.com.cielo.meucadastro"
    }

    fun toolbarCustom() {
        this.toolbar?.visibility = View.VISIBLE
        txtTitle.text = "Meu Cadastro"

        btnLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        hideProgress()
        setupUserInfo()
    }

    private fun setupUserInfo() {
        if (isAttached()) {
            fragmentAdapter = MeuCadastroNovoAdapter(supportFragmentManager,
                    this, this)

            /*this.configureToolbarActionListener?.changeTo(title =
            getString(R.string.menu_meu_cadastro))*/

            disposableDefault = fragmentAdapter
            viewpager_meu_cad.adapter = fragmentAdapter
            tabs_meu_cad.setupWithViewPager(viewpager_meu_cad)

            for (i in 1..tabs_meu_cad.tabCount) {
                val tab = tabs_meu_cad.getTabAt(i)
                tab?.customView = fragmentAdapter?.getTabView(i)
            }
        }
    }

    override fun hideContent() {
        //
    }

    override fun showContent() {
        if (isAttached()) {
            relativeMyRegisterError.visibility = View.GONE
            pb_meu_cadastro.visibility = View.GONE
            tabs_meu_cad.visibility = View.VISIBLE
            viewpager_meu_cad.visibility = View.VISIBLE
        }
    }

    override fun showProgress() {
        if (isAttached()) {
            relativeMyRegisterError.visibility = View.GONE
            pb_meu_cadastro.visibility = View.VISIBLE
            viewpager_meu_cad.visibility = View.GONE
            tabs_meu_cad.visibility = View.GONE
        }
    }

    override fun hideProgress() {
        if (isAttached()) {
            relativeMyRegisterError.visibility = View.GONE
            pb_meu_cadastro.visibility = View.GONE
            viewpager_meu_cad.visibility = View.VISIBLE
            tabs_meu_cad.visibility = View.VISIBLE
        }
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
            relativeMyRegisterError.visibility = View.VISIBLE
            img_error.visibility = View.GONE
            text_view_error_msg.text = error.message
            container_error.visibility = View.VISIBLE
            button_error_try.text = getString(R.string.text_try_again_label)
        }
    }

    override fun loadDadosEstabelecimento(meuCadastroObj: MeuCadastroObj) {
    }

    override fun loadBandeirasHabilitadas(bandeiras: CardBrandFees) {
    }

    override fun hideBandeirasHabilitadas() {
        if (isAttached()) {
            container_error.visibility = View.VISIBLE
            pb_meu_cadastro.visibility = View.GONE
            tabs_meu_cad.visibility = View.GONE
        }
    }

    override fun context(): Context {
        return baseContext
    }

    override fun callAccountEngine(
        list: ArrayList<Bank>?,
        elegibility: Boolean
    ) = Unit

    override fun showMask() {
        if (isAttached()) {
            maskTransferencia.visibility = View.VISIBLE
            maskTransferencia.setOnClickListener(null)
            pb_meu_cadastro.visibility = View.VISIBLE
        }
    }

    override fun hideMask() {
        synchronized(this) {
            if (isAttached()) {
                maskTransferencia.visibility = View.GONE
                pb_meu_cadastro.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableDefault?.disposable()
        disposableDefault = null
    }
}