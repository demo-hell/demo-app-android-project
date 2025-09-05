package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_SECOND
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsTrustedDestinationsDetailBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_STATUS_APPROVED
import br.com.mobicare.cielo.pix.constants.PIX_STATUS_PENDING
import br.com.mobicare.cielo.pix.constants.PIX_TRUSTED_DESTINATION_RESPONSE_ARGS
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixMyLimitsTrustedDestinationsDetailFragment :
    BaseFragment(),
    CieloNavigationListener,
    PixMyLimitsTrustedDestinationsDetailContract.View {
    private val presenter: PixMyLimitsTrustedDestinationsDetailPresenter by inject {
        parametersOf(this)
    }

    private val trustedDestinationResponse: PixTrustedDestinationResponse? by lazy {
        arguments?.getParcelable(PIX_TRUSTED_DESTINATION_RESPONSE_ARGS)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private var navigation: CieloNavigation? = null
    private var _binding: FragmentPixMyLimitsTrustedDestinationsDetailBinding? = null
    private val binding get() = _binding

    private val statusIsApproved get() = trustedDestinationResponse?.status == PIX_STATUS_APPROVED
    private val statusIsApprovedOrPending get() = trustedDestinationResponse?.status?.let { it in listOf(PIX_STATUS_APPROVED, PIX_STATUS_PENDING) } ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding =
            FragmentPixMyLimitsTrustedDestinationsDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_contact_data))
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        trustedDestinationResponse?.let { itTrustedDestination ->
            val clearDate = itTrustedDestination.solicitationDate?.clearDate()
            val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
            val hour =
                clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
                    ?: EMPTY

            binding?.apply {
                tvTrustedName.text = itTrustedDestination.name
                tvTrustedKey.text = itTrustedDestination.nationalRegistration

                includeDetails.tvDestinationBank.text = itTrustedDestination.institutionName

                includeDetails.tvLimitValue.text =
                    itTrustedDestination.limits?.first()?.value?.toPtBrRealString()

                includeDetails.tvDestinationAgencyAndAccount.text =
                    getString(
                        R.string.agency_and_account_bank_value,
                        itTrustedDestination.bankBranchNumber,
                        itTrustedDestination.bankAccountNumber,
                    )

                includeDetails.tvRegistrationDateTitle.apply {
                    text = getString(if (statusIsApproved) R.string.text_registration_date else R.string.status)
                    visible(statusIsApprovedOrPending)
                }

                includeDetails.tvRegistrationDate.apply {
                    val drawable = ContextCompat.getDrawable(context, if (statusIsApproved) R.drawable.ic_calendar_pix_16dp else R.drawable.ic_symbol_alert_round_warning_16_dp)
                    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    text = if (statusIsApproved) getString(R.string.text_pix_transfer_receipt_date_mask, date, hour) else getString(R.string.pending)
                    visible(statusIsApprovedOrPending)
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.includeDetails?.tvDeleteAccount?.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        CieloAskQuestionDialogFragment
            .Builder()
            .title(getString(R.string.text_delete_account_dialog_title))
            .message(getString(R.string.text_delete_account_dialog_info))
            .cancelTextButton(getString(R.string.text_delete_account_dialog_btn))
            .positiveTextButton(getString(R.string.back))
            .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
            .build().let {
                it.onCancelButtonClickListener =
                    View.OnClickListener {
                        deleteTrustedDestination()
                    }
                it.show(
                    childFragmentManager,
                    PixMyLimitsTrustedDestinationsDetailFragment::class.java.simpleName,
                )
            }
    }

    private fun deleteTrustedDestination(isAnimation: Boolean = true) {
        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.deleteTrustedDestination(otpCode, trustedDestinationResponse?.id)
        }
    }

    override fun onErrorDeleteTrustedDestination(
        onGenericError: () -> Unit,
    ) {
        validationTokenWrapper.playAnimationError(
            callbackValidateToken =
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        onGenericError.invoke()
                    }
                },
        )
    }

    override fun showError(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() ||
            error.errorCode.contains(
                OTP,
            )
        ) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error =
                    processErrorMessage(
                        error,
                        getString(R.string.business_error),
                        getString(R.string.text_pix_generic_error_message),
                    ),
                title = getString(R.string.text_pix_generic_error_title),
                isFullScreen = false,
            )
        }
    }

    override fun onSuccessDeleteTrustedDestination() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    findNavController().navigate(
                        PixMyLimitsTrustedDestinationsDetailFragmentDirections.actionPixMyLimitsTrustedDestinationsDetailFragmentToPixMyLimitsFragment(
                            false,
                        ),
                    )
                }
            },
        )
    }
}
