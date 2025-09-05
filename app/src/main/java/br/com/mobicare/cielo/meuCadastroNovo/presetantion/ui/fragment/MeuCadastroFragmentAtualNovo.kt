package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ELEGIBILITY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.URL_WEBSITE_CIELO
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.MeuCadastroFragmentBinding
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_TRANSFER_ACCOUNT_ADD
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroDomicilio.MEU_CADASTRO_DOMICILIO_BANKS
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.AddAccountEngineActivity
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ESTABLISHMENT
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.MeuCadastroNovoAdapter
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.startActivity
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4

@Keep
class MeuCadastroFragmentAtualNovo : BaseFragment(), MeuCadastroContract.View,
    ListenerCadastroScreen, MeuCadastroNovoAdapter.DadosContaFragmentBrandTransferListener {

    private var disposableDefault: DisposableDefault? = null
    private var fragmentAdapter: MeuCadastroNovoAdapter? = null

    private val binding: MeuCadastroFragmentBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
        setupUserInfo()
    }

    private fun setupUserInfo() {
        if (isAttached()) {
            fragmentAdapter = MeuCadastroNovoAdapter(childFragmentManager, requireActivity(), this).apply {
                this.onTransferBrandListener = this@MeuCadastroFragmentAtualNovo
            }

            this.configureToolbarActionListener?.changeTo(title = getString(R.string.menu_meu_cadastro))

            disposableDefault = fragmentAdapter
            binding.apply {
                viewpagerMeuCad.adapter = fragmentAdapter
                tabsMeuCad.setupWithViewPager(viewpagerMeuCad)

                MenuPreference.instance.getUserObj().let { userObj ->
                    if (userObj?.isCustomRole == true || userObj?.isLockedProfile == true) {
                        val userTab = tabsMeuCad.getTabAt(ONE)
                        tabsMeuCad.selectTab(userTab, true)
                        userObj.mainRole?.let { mainRole ->
                            tabsMeuCad.addOnTabSelectedListener(getTabLayoutSelectedlistener(userTab, mainRole))
                        }
                    }
                }
                for (i in ONE..tabsMeuCad.tabCount) {
                    val tab = tabsMeuCad.getTabAt(i)
                    tab?.customView = fragmentAdapter?.getTabView(i)
                }
            }
        }
    }

    private fun getTabLayoutSelectedlistener(userTab: TabLayout.Tab?, mainRole: String) = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab?.position != userTab?.position) {
                binding.viewpagerMeuCad.gone()
                checkMainRoleToShowWarningCieloDialog(mainRole, userTab)
            } else {
                binding.viewpagerMeuCad.visible()
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    private fun checkMainRoleToShowWarningCieloDialog(mainRole: String, userTab: TabLayout.Tab?) {
        when (mainRole) {
            UserObj.TECHNICAL -> showCieloDialogProfileWarning(
                title = binding.root.context.getString(R.string.txt_title_dialog_for_technical_profile),
                description = binding.root.context.getString(R.string.txt_desc_dialog_for_technical_profile),
                userTab = userTab,
                primaryButtonText = R.string.txt_label_button_access_website
            ) {
                Utils.openBrowser(requireActivity(), URL_WEBSITE_CIELO)
            }

            UserObj.CUSTOM -> showCieloDialogProfileWarning(
                title = binding.root.context.getString(R.string.txt_title_dialog_for_custom_profile),
                description = binding.root.context.getString(R.string.txt_desc_dialog_for_custom_profile),
                userTab = userTab,
                primaryButtonText = R.string.close
            )
        }
    }

    private fun showCieloDialogProfileWarning(
        title: String,
        description: String,
        userTab: TabLayout.Tab?,
        primaryButtonText: Int,
        primaryAction: (() -> Unit)? = null
    ) {
        CieloDialog.create(title, description).apply {
            setTitleTextAppearance(R.style.bold_montserrat_16)
            setPrimaryButton(binding.root.context.getString(primaryButtonText))
            setOnPrimaryButtonClickListener {
                dismiss()
                userTab?.select()
                primaryAction?.invoke()
            }
            setOnCancelListener {
                userTab?.select()
            }
            setOnCloseClickListener {
                userTab?.select()
            }
        }.show(childFragmentManager, this@MeuCadastroFragmentAtualNovo::class.simpleName)
    }

    // métodos para sobrescrita
    override fun hideContent() {

    }

    override fun showContent() {
        if (isAttached()) {
            binding.apply {
                relativeMyRegisterError.root.gone()
                pbMeuCadastro.root.gone()
                tabsMeuCad.visible()
                viewpagerMeuCad.visible()
            }
        }
    }

    override fun showProgress() {
        if (isAttached()) {
            binding.apply {
                relativeMyRegisterError.root.gone()
                pbMeuCadastro.root.visible()
                viewpagerMeuCad.gone()
                tabsMeuCad.gone()
            }
        }
    }

    override fun hideProgress() {
        if (isAttached()) {
            binding.apply {
                relativeMyRegisterError.root.gone()
                pbMeuCadastro.root.gone()
                viewpagerMeuCad.visible()
                tabsMeuCad.visible()
            }
        }
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
            binding.relativeMyRegisterError.apply {
                root.visible()
                imgError.gone()
                containerError.visible()
                textViewErrorMsg.text = error.message
                buttonErrorTry.text = getString(R.string.text_try_again_label)
            }
        }
    }


    override fun loadDadosEstabelecimento(meuCadastroObj: MeuCadastroObj) {
        //MeuCadastroFragmentAtualNovo.meuCadastroObj = meuCadastroObj
    }


    override fun loadBandeirasHabilitadas(bandeiras: CardBrandFees) {
        //MeuCadastroFragmentAtualNovo.bandeiras = bandeiras
    }

    override fun hideBandeirasHabilitadas() {
        if (isAttached()) {
            binding.apply {
                relativeMyRegisterError.containerError.visible()
                pbMeuCadastro.root.gone()
                tabsMeuCad.gone()
            }
        }
    }

    override fun context(): Context {
        return requireActivity().baseContext
    }

    override fun callAccountEngine(
        list: ArrayList<Bank>?,
        elegibility: Boolean
    ) {
        val intent = Intent(activity, AddAccountEngineActivity::class.java)
        intent.putExtra(MEU_CADASTRO_DOMICILIO_BANKS, list)
        intent.putExtra(ELEGIBILITY, elegibility)
        startActivityForResult(intent, REQUEST_CODE_TRANSFER_ACCOUNT_ADD)
    }

    override fun showMask() {
        if (isAttached()) {
            binding.apply {
                maskTransferencia.root.visible()
                maskTransferencia.root.setOnClickListener(null)
                pbMeuCadastro.root.visible()
            }
        }
    }

    override fun hideMask() {
        synchronized(this) {
            if (isAttached()) {
                binding.apply {
                    maskTransferencia.root.gone()
                    pbMeuCadastro.root.gone()
                }
            }
        }
    }

    override fun onDestroy() {
        disposableDefault?.disposable()
        disposableDefault = null
        super.onDestroy()
    }

    override fun transferBrandListener(listBanks: List<Bank>, currentBank: Bank) {
        requireActivity().startActivity<FlagTransferEngineActivity>(
            "listBank" to listBanks,
            "bank" to currentBank
        )
    }

    override fun callStatusError() {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    private fun logScreenView() {
        if (isAttached()) {
            ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ESTABLISHMENT)
        }
    }
}