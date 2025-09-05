package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.adicaoEc.presentation.ui.AddEcActivity
import br.com.mobicare.cielo.commons.CommonActivityWithFragment
import br.com.mobicare.cielo.commons.EXTRA_BUTTON_CANCEL
import br.com.mobicare.cielo.commons.EXTRA_BUTTON_SAVE
import br.com.mobicare.cielo.commons.EXTRA_PARAM_FRAGMENT
import br.com.mobicare.cielo.commons.EXTRA_PARAM_OBJECT
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.collapse
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.expand
import br.com.mobicare.cielo.databinding.McnFragmentDadosEstabelecimentoBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_MEU_CADASTRO_NOVO
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ESTABLISHMENT_ANALYTICS
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MY_REGISTER
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_0f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_90f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia.ARG_PARAM_FANTASY_NAME
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia.EditarDadosNomeFantasiaFragment
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DadosEstabelecimentoFragment : BaseFragment(), ShowLayoutListener {

    private var est: MCMerchantResponse? = null
    private var isEditBlocked: Boolean = false
    private val analytics: MeuCadastroAnalytics by inject()
    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    private lateinit var _binding: McnFragmentDadosEstabelecimentoBinding
    val binding: McnFragmentDadosEstabelecimentoBinding get() = _binding

    companion object {
        private const val PARAM_USER = "param_user"
        private const val EDIT_BLOCK = "edit_block"
        private const val MIN_CNPJ_LENGTH = 14
        fun create(est: MCMerchantResponse, isEditBlocked: Boolean) =
            DadosEstabelecimentoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PARAM_USER, est)
                    putBoolean(EDIT_BLOCK, isEditBlocked)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = McnFragmentDadosEstabelecimentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            it.getParcelable<MCMerchantResponse>(PARAM_USER)?.let { itMerchantResponse ->
                est = itMerchantResponse
            }
            isEditBlocked = it.getBoolean(EDIT_BLOCK)
        }
        analytics.logHomeAddEcEstablishmentScreenView(this.javaClass)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupMfaRouteHandler()

        est?.apply {
            binding.apply {
                textFantasyName.text = tradingName
                textNumberStabliment.text = number.toString()
                textAtive.text = if (status == getString(R.string.open)) getString(R.string.active) else getString(R.string.inactive)
                textPaymentBlockDebit.text =
                    if (debitPaymentBlock) getString(R.string.text_yes_label) else getString(R.string.text_no_label)
                textDateOpen.text = openingDate?.dateFormatToBr()
                textMainName.text = tradingName
                textDateLastSele.text = lastSaleDate?.dateFormatToBr()
                category?.let {
                    textMcc.text = "$categoryCode - $it"
                }

                updateRequiredOwner?.let { itUpdateRequiredOwner ->
                    if (itUpdateRequiredOwner) {
                        constraintViewDetailsEstablishment.gone()
                        imageViewArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
                    }
                }

                cnpj?.let { doc ->
                    textCnpj.text = addMaskCPForCNPJ(doc, getString(R.string.mask_cnpj_step4))
                    if (doc.length >= MIN_CNPJ_LENGTH) {
                        editButton.visible()
                        editButton.setOnClickListener {
                            goToOwnerEditData()
                        }
                    } else
                        editButton.gone()
                } ?: editButton.gone()
            }
        }

        binding.apply {
            contrantViewTitleEst.setOnClickListener {
                if (constraintViewDetailsEstablishment.visibility == View.VISIBLE) {
                    gaSendInteraction(textTitle.text.toString())

                    imageViewArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
                    constraintViewDetailsEstablishment.collapse()
                } else {
                    imageViewArrow.animate()?.rotation(VALUE_ROTATION_90f)?.start()
                    constraintViewDetailsEstablishment.expand()
                }
            }

            if (isEditBlocked) {
                editButton.gone()
            }
        }
    }

    private fun setupListeners() {
        binding.tvAddEc.setOnClickListener {
            analytics.logHomeAddEcEstablishmentClick(this.javaClass)
            mfaRouteHandler.runWithMfaToken {
                requireContext().startActivity<AddEcActivity>(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to MeuCadastroAnalytics.MY_REGISTER_PATH
                )
            }
        }
    }

    private fun setupMfaRouteHandler() {
        mfaRouteHandler.checkIsMfaEligible { isEligible ->
            binding.tvAddEc.visible(isEligible)
        }
    }

    private fun goToOwnerEditData() {
        this.context?.let {
            val clazzName: String = EditarDadosNomeFantasiaFragment::class.java.name
            val bundle = Bundle().apply {
                putParcelable(ARG_PARAM_FANTASY_NAME, est)
            }

            activity?.startActivityForResult<CommonActivityWithFragment>(
                REQUEST_CODE_MEU_CADASTRO_NOVO,
                EXTRA_BUTTON_CANCEL to getString(R.string.keep_current),
                EXTRA_BUTTON_SAVE to getString(R.string.to_change),
                EXTRA_PARAM_FRAGMENT to clazzName,
                EXTRA_PARAM_OBJECT to bundle
            )

        }

    }

    override fun onResume() {
        super.onResume()
        mfaRouteHandler.onResume()
        analytics.screenViewEstablishment()
    }

    override fun onPause() {
        super.onPause()
        mfaRouteHandler.onPause()
    }

    private fun gaSendInteraction(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MY_REGISTER),
                action = listOf(ESTABLISHMENT_ANALYTICS),
                label = listOf(Label.INTERACAO, labelButton)
            )
        }
    }

    override fun closeContainer() {
        binding.apply {
            constraintViewDetailsEstablishment.gone()
            imageViewArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
        }
    }
}