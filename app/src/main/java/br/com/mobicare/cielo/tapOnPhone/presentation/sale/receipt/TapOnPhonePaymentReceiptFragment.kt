package br.com.mobicare.cielo.tapOnPhone.presentation.sale.receipt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.presentation.onboarding.ArvOnboardingViewModel
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.constants.ONE_TEXT
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.takeScreenshot
import br.com.mobicare.cielo.databinding.FragmentTapOnPhonePaymentReceiptBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.GOOGLE_PLAY_REVIEW_SALES_TAP
import br.com.mobicare.cielo.review.presentation.GooglePlayReviewViewModel
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.domain.model.TransactionReceiptData
import br.com.mobicare.cielo.tapOnPhone.enums.TapOnPhonePaymentTypeEnum
import br.com.mobicare.cielo.tapOnPhone.utils.tapPaymentType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TapOnPhonePaymentReceiptFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhonePaymentReceiptBinding? = null

    private val args: TapOnPhonePaymentReceiptFragmentArgs by navArgs()
    private var receipt: TransactionReceiptData? = null

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private val viewModel: GooglePlayReviewViewModel by viewModel()

    private val paymentMethod
        get() = tapPaymentType(receipt?.transactionType, receipt?.installments)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTapOnPhonePaymentReceiptBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupView()
        setupGooglePlayReview()
    }

    override fun onResume() {
        super.onResume()
        analytics.logPaymentReceipt(paymentMethod.tag, javaClass)
        ga4.logPaymentReceiptScreenView(paymentMethod.tagGa4)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showToolbar(isShow = false)
            navigation?.showContainerButton(isShow = false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.apply {
            ivClose.setOnClickListener {
                doWhenResumed {
                    analytics.logScreenActions(
                        flowName = TapOnPhoneAnalytics.PAYMENT_RECEIPT,
                        labelList = listOf(Action.FECHAR, paymentMethod.tag)
                    )
                    navigation?.goToHome()
                }
            }

            btShareReceipt.setOnClickListener {
                doWhenResumed {
                    analytics.logScreenActions(
                        flowName = TapOnPhoneAnalytics.PAYMENT_RECEIPT,
                        labelList = listOf(Action.COMPARTILHAR, paymentMethod.tag)
                    )
                    ga4.logPaymentReceiptShare(paymentMethod.tagGa4)
                    takeAReceiptScreenshot()
                }
            }

            btNewSale.setOnClickListener {
                doWhenResumed {
                    analytics.logScreenActions(
                        flowName = TapOnPhoneAnalytics.PAYMENT_RECEIPT,
                        labelList = listOf(TapOnPhoneAnalytics.NEW_SALE, paymentMethod.tag)
                    )
                    ga4.logClick(
                        screenName = TapOnPhoneGA4.getTransactionalReceiptScreenView(paymentMethod.tagGa4),
                        contentName = TapOnPhoneGA4.NEW_SALE
                    )
                    findNavController().navigate(
                        TapOnPhonePaymentReceiptFragmentDirections.actionTapOnPhonePaymentReceiptFragmentToTapOnPhoneSaleValueFragment(
                            args.devicetapargs
                        )
                    )
                }
            }
        }
    }

    private fun setupGooglePlayReview() {
        setupObserver()
        viewModel.onRequestReview(
            context = requireContext(),
            activity = requireActivity(),
            featureToggleFlowKey = GOOGLE_PLAY_REVIEW_SALES_TAP,
            isFeatureToggleFlow = true
        )
    }

    private fun setupObserver() {
        viewModel.googlePlayReviewLiveData.observe(viewLifecycleOwner) {

        }
    }

    private fun setupView() {
        changeStatusBarColor(R.color.brand_500)
        setAppearanceLightStatusBar(isAppearanceLightStatusBars = false)
        receipt = args.transactionreceipttapargs
        binding?.includeReceipt?.apply {
            tvPaymentDate.text = getString(
                R.string.tap_on_phone_receipt_date,
                receipt?.date,
                receipt?.hour
            )

            tvEstablishmentName.text = receipt?.receiptInfo?.merchantName
            tvAddress.text = getString(
                R.string.tap_on_phone_receipt_address,
                receipt?.receiptInfo?.merchantAddress,
                receipt?.receiptInfo?.merchantCity,
                receipt?.receiptInfo?.merchantState,
            )
            tvCardInfo.text = getString(
                R.string.tap_on_phone_receipt_card,
                receipt?.receiptInfo?.brand,
                receipt?.cardNumber,
            )
            tvAut.text = getString(
                R.string.tap_on_phone_receipt_aut, receipt?.receiptInfo?.authorizationCode
            )
            tvAid.text = getString(R.string.tap_on_phone_receipt_aid, receipt?.applicationId)
            tvDoc.text = getString(
                R.string.tap_on_phone_receipt_doc, receipt?.doc
            )

            setupInstallmentsView()
            setupAmountView()
            setupDocument(receipt?.receiptInfo?.merchantCode)
        }
    }

    private fun setupInstallmentsView() {
        val isShow = receipt?.installments?.let { it > ONE_TEXT } ?: false
        binding?.includeReceipt?.tvInstallments?.apply {
            visible(isShow)
            text = getString(R.string.tap_on_phone_receipt_installments, receipt?.installments)
        }
    }

    private fun setupDocument(doc: String?) {
        binding?.includeReceipt?.tvCnpj?.apply {
            doc?.let { itDoc ->
                when {
                    ValidationUtils.isCNPJ(itDoc) -> {
                        visible()
                        text = getString(
                            R.string.tap_on_phone_receipt_document,
                            FormHelper.maskFormatter(itDoc, CNPJ_MASK_COMPLETE_FORMAT)
                                .formattedText.string
                        )
                    }
                    ValidationUtils.isCPF(itDoc) -> {
                        visible()
                        text = getString(
                            R.string.tap_on_phone_receipt_document_cpf,
                            EditTextHelper.cpfMaskFormatter(itDoc).formattedText.string
                        )
                    }
                    else -> gone()
                }
            }
        }
    }

    private fun setupAmountView() {
        binding?.includeReceipt?.apply {
            tvPaymentType.text = when (paymentMethod) {
                TapOnPhonePaymentTypeEnum.DEBIT -> getString(R.string.tap_on_phone_receipt_debit)
                TapOnPhonePaymentTypeEnum.INSTALLMENT -> getString(R.string.tap_on_phone_receipt_total_value)
                else -> getString(R.string.tap_on_phone_receipt_credit)
            }
            tvSaleValue.text = getString(
                R.string.tap_on_phone_receipt_value, receipt?.receiptInfo?.value
            )
        }
    }

    private fun takeAReceiptScreenshot() {
        binding?.includeReceipt?.root?.takeScreenshot()?.also {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = requireContext().contentResolver.getType(it)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, it)
            }

            startActivity(
                Intent.createChooser(
                    shareIntent, resources.getText(R.string.share_receipt_pix)
                )
            )
        } ?: run {
            Toast.makeText(
                requireContext(),
                getString(R.string.text_pix_transfer_receipt_share_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackButtonClicked(): Boolean {
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }
}