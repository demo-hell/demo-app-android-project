package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.NAO
import br.com.mobicare.cielo.commons.analytics.SIM
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.databinding.McnFragmentEstablishmentBinding
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosEstabelecimentoPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.MeuCadastroNovoAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ListenerCadastroScreen
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DadosEstablishmentFragment : BaseFragment(), MeuCadastroContract.DadosEstabelecimentoView,
    MeuCadastroNovoAdapter.DataUpdateListener {

    private val analytics: MeuCadastroAnalytics by inject()

    lateinit var onDataUpdateListener: MeuCadastroNovoAdapter.DataUpdateListener
    lateinit var listenerCadastroScreen: ListenerCadastroScreen
    private var blockDialogShowed = false
    private val listOwners = ArrayList<Owner>()
    private val presenter: DadosEstabelecimentoPresenter by inject {
        parametersOf(this)
    }

    private lateinit var binding: McnFragmentEstablishmentBinding

    private lateinit var dadosEstabelecimentoFragment: DadosEstabelecimentoFragment
    private lateinit var dadosEnderecoFragment: DadosEnderecoFragment
    private lateinit var dadosProprietarioFragment: DadosProprietarioFragment
    private lateinit var dadosContatoFragment: DadosContatoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    companion object {
        const val DATA_UPDATE = "ATUALIZACAO_CADASTRAL"
        const val VALUE_ROTATION_90f = 90f
        const val VALUE_ROTATION_0f = 0f
        private const val EDIT_BLOCK_CODE_TYPE = 5
        private const val LOGISTIC_BLOCKED = 20
        private const val BLOCK_CALL_CENTER_NUMBER = "40025472"
        private const val ESTABLISHMENT_DATA_EDIT_BLOCK_DIALOG_TAG =
            "ESTABLISHMENT_DATA_EDIT_BLOCK_DIALOG"

        fun newInstance(listener: ListenerCadastroScreen, dataUpdateListener: MeuCadastroNovoAdapter.DataUpdateListener): DadosEstablishmentFragment {
            return DadosEstablishmentFragment().apply {
                listenerCadastroScreen = listener
                onDataUpdateListener = dataUpdateListener
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = McnFragmentEstablishmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEstabelecimento()
        binding.errorView.errorButton?.setOnClickListener {
            initEstabelecimento()
        }
    }

    private fun initEstabelecimento() {
        presenter.loadDadosAccount()
    }

    private fun configEstablishment(estabelecimento: MCMerchantResponse, isEditBlocked: Boolean) {
        dadosEstabelecimentoFragment = DadosEstabelecimentoFragment.create(estabelecimento, isEditBlocked)
        this.childFragmentManager
            .beginTransaction()
            .replace(
                R.id.container_establishment,
                dadosEstabelecimentoFragment
            )
            .commit()

    }

    private fun configContact(contacts: List<Contact>, isEditBlocked: Boolean) {
        dadosContatoFragment = DadosContatoFragment.create(arrayListOf(*contacts.toTypedArray()), isEditBlocked)
        this.childFragmentManager
            .beginTransaction()
            .replace(
                R.id.container_contact,
                dadosContatoFragment
            )
            .commit()
    }

    private fun configAddress(addresses: List<Address>, isEditBlocked: Boolean) {
        val listAddress = ArrayList<Address>()
        listAddress.addAll(addresses)

        dadosEnderecoFragment = DadosEnderecoFragment.create(listAddress, isEditBlocked).apply {
            dataUpdateListener = this@DadosEstablishmentFragment
        }

        this.childFragmentManager
            .beginTransaction()
            .replace(
                R.id.container_address,
                dadosEnderecoFragment
            )
            .commit()
    }

    private fun configOwner(owners: List<Owner>, showAlert: Boolean?, isEditBlocked: Boolean) {
        listOwners.addAll(owners)

        showAlert?.let { itsToShowAlert ->
            if (itsToShowAlert and listOwners.isNotEmpty()) configAlertContainer()
        }

        dadosProprietarioFragment = DadosProprietarioFragment.create(
            listOwners,
            showAlert ?: false,
            isEditBlocked
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.container_owner_data, dadosProprietarioFragment)
            .commit()
    }

    private fun configAlertContainer() {
        binding.apply {
            updateInfoAlertContainer.visible()
            alertText.setOnClickListener {
                dadosContatoFragment.closeContainer()
                dadosEnderecoFragment.closeContainer()
                dadosEstabelecimentoFragment.closeContainer()
                openOwnerContainer()
            }

            closeButton.setOnClickListener {
                val mAlertDialog = CieloAskQuestionDialogFragment.Builder()
                    .title(getString(R.string.text_title_dialog_no_protocol))
                    .message(getString(R.string.your_data_is_ok_question))
                    .positiveTextButton(getString(R.string.see_data))
                    .cancelTextButton(getString(R.string.already_updated))
                    .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
                    .onPositiveButtonClickListener {
                        openOwnerContainer()
                        analytics.logUpdatedDataClick(SIM)
                    }
                    .onCancelButtonClickListener {
                        analytics.logUpdatedDataClick(NAO)
                        LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(Intent(DATA_UPDATE))
                    }
                    .build()

                activity?.supportFragmentManager?.let {
                    mAlertDialog.show(it, null)
                }
                analytics.logUpdatedDataScreenView()
            }
        }
    }

    private fun openOwnerContainer() {
        dadosProprietarioFragment.showContainer()
    }

    override fun showEstabelecimento(estabelecimento: MCMerchantResponse) {
        val isEditBlocked =
            estabelecimento.blocks?.any {
                it.codeType == EDIT_BLOCK_CODE_TYPE &&
                        it.codeReason != LOGISTIC_BLOCKED
            } == true

        if (isEditBlocked && blockDialogShowed.not()) {
            showBlockedDialog()
            blockDialogShowed = true
        }

        estabelecimento.let {
            configEstablishment(it, isEditBlocked)
            configAddress(it.addresses, isEditBlocked)
            configContact(it.contacts, isEditBlocked)

            it.owners?.let { itOwners ->
                configOwner(itOwners, estabelecimento.updateRequiredOwner, isEditBlocked)
            }
        }
    }

    private fun showBlockedDialog() {
        CieloDialog.create(
            getString(R.string.edit_block_title),
            getString(R.string.edit_block_message)
        )
            .setImage(R.drawable.ic_07)
            .setPrimaryButton(getString(R.string.edit_block_close_button_label))
            .setOnPrimaryButtonClickListener {
                analytics.logDialogClickButton(getString(R.string.edit_block_title), getString(R.string.edit_block_close_button_label))
            }
            .setSecondaryButton(getString(R.string.edit_block_call_center_button_label))
            .setOnSecondaryButtonClickListener {
                Utils.callPhone(requireActivity(), BLOCK_CALL_CENTER_NUMBER)
                analytics.logDialogClickButton(getString(R.string.edit_block_title), getString(R.string.edit_block_call_center_button_label))
            }
            .show(childFragmentManager, ESTABLISHMENT_DATA_EDIT_BLOCK_DIALOG_TAG)
        analytics.logEditNotAllowedScreenView()
    }

    override fun error() {
        listenerCadastroScreen.hideMask()
        binding.apply {
            frameProgressDe.root.gone()
            layoutManEstabelecimento.gone()
            errorView.visible()
        }
    }

    override fun showProgress() {
        binding.apply {
            frameProgressDe.root.visible()
            layoutManEstabelecimento.gone()
            errorView.gone()
        }
    }

    override fun hideProgress() {
        binding.apply {
            frameProgressDe.root.gone()
            errorView.gone()
            layoutManEstabelecimento.visible()
        }
        listenerCadastroScreen.hideMask()
    }

    override fun logout() {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun onDataUpdated() {
        onDataUpdateListener.onDataUpdated()
    }
}