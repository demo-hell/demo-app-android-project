package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.CHARGEBACK_SALES_PENDING
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDIND_ACCEPT_SUCESS
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_ACCEPT
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.ChargebackInfoContentAdapter
import br.com.mobicare.cielo.chargeback.presentation.details.builder.ChargebackInfoContentListBuilder
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackFeatureToggleViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackPendingDetailsViewModel
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelector
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelectorScreenType
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.chargeback.utils.UiAcceptState
import br.com.mobicare.cielo.commons.analytics.Action.MODAL
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.analytics.formatTextForGA4
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.commons.utils.wrapInDoubleQuotes
import br.com.mobicare.cielo.databinding.FragmentChargebackPendingDetailsBinding
import br.com.mobicare.cielo.pix.constants.PENDING
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargebackPendingDetailsFragment : BaseFragment(), CieloNavigationListener {

    private val ga4: ChargebackGA4 by inject()
    private var navigation: CieloNavigation? = null
    private val viewModel: ChargebackPendingDetailsViewModel by viewModel()
    private val handlerValidationToken: HandlerValidationToken by inject()
    private val chargebackFeatureToggleViewModel: ChargebackFeatureToggleViewModel by viewModel()

    private var _binding: FragmentChargebackPendingDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: ChargebackPendingDetailsFragmentArgs by navArgs()
    private lateinit var chargeback: Chargeback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chargeback = args.chargeback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChargebackPendingDetailsBinding.inflate(
        inflater, container, false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupDocAttachmentSender()
        setupAmount()
        setupExpirationDate()
        setupExpirationDateTip()
        setupInformationList()
        setupMoreInfo()
        setupButtonAccept()
        setupButtonDecline()
        observeAcceptState()
        observeLoading()
        getDescriptionReasonTypeMessage()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.let {
                it.setNavigationListener(this)
                it.showHelpButton(true)
                it.showContainerButton(isShow = false)
                it.setTextToolbar(EMPTY)
            }
        }
    }

    private fun setupDocAttachmentSender() {
        binding.apply {
            val refundFileInformation = chargeback.chargebackDetails?.refundFileInformation
            if (!refundFileInformation.isNullOrEmpty()) {
                tvLinkAttachmentDocSender.setOnClickListener {
                    findNavController().navigate(
                        ChargebackPendingDetailsFragmentDirections
                            .actionChargebackPendingDetailsFragmentToChargebackDocumentSenderFragment(chargeback)
                    )
                }
                tvLinkAttachmentDocSender.visible()
            } else
                tvLinkAttachmentDocSender.gone()
        }
    }

    private fun getDescriptionReasonTypeMessage() {
        chargebackFeatureToggleViewModel.getDescriptionReasonTypeFeatureToggle(chargeback.chargebackDetails)
    }

    private fun observeAcceptState() {
        viewModel.chargebackAcceptUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiAcceptState.Success -> onSuccess()
                is UiAcceptState.Error -> onError(state.error)
                is UiAcceptState.ErrorToken -> onErrorToken()
            }
        }
    }

    private fun observeLoading() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            onHideLoading()
        }
    }

    private fun setupAmount() {
        binding.tvAmount.text = chargeback.transactionAmount?.toPtBrRealString()
            ?: ChargebackConstants.DEFAULT_EMPTY_VALUE
    }

    private fun setupExpirationDate() {
        binding.ctlExpirationDate.apply {
            ChargebackStatusStyleSelector(
                resources, ChargebackStatusStyleSelectorScreenType.DETAILS, chargeback
            ).apply {
                setTagIcon(tagIcon)
                setBackgroundShape(backgroundShape)
                setTextStyle(textStyle)
                setText(text)
            }
        }
    }

    private fun setupExpirationDateTip() {
        binding.tvExpirationDateTip.text = HtmlCompat.fromHtml(
            getString(R.string.chargeback_expiration_date_tip),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setupInformationList() {
        binding.rvInfo.apply {
            adapter = ChargebackInfoContentAdapter(
                ChargebackInfoContentListBuilder(showRDRCardFeatureToggle = false,res = context.resources,context = requireContext()).build(chargeback)
            ).also {
                it.setOnItemReasonClicked(::onItemReasonClicked)
                it.setOnItemMessageClicked(::onItemMessageClicked)
            }
            addItemDecoration(
                CircleIndicatorItemDecoration(
                    context,
                    dotRadius = ELEVEN.toFloat(),
                    dotWidth = ELEVEN.toFloat()
                )
            )
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    private fun onItemReasonClicked() {
        chargebackFeatureToggleViewModel.descriptionReasonTypeMessage?.let { itMessage ->
            CieloDialog.create(
                title = getString(
                    R.string.chargeback_reason_dialog_title,
                    chargeback.chargebackDetails?.reasonCode,
                    chargeback.chargebackDetails?.descriptionReason
                ),
                message = if (itMessage is Int) getString(itMessage) else itMessage as String
            )
                .closeButtonVisible(true)
                .setPrimaryButton(requireContext().getString(R.string.text_close))
                .setTitleTextAppearance(R.style.bold_montserrat_20_cloud_500)
                .show(requireActivity().supportFragmentManager, tag)
        }
        chargeback.chargebackDetails?.descriptionReason?.let {description ->
            val descriptionReasonFormatted = formatTextForGA4(description)
            ga4.logDisplayContentPendingChargeback(
                MODAL,
                CHARGEBACK_SALES_PENDING,
                descriptionReasonFormatted
            )
        }
    }

    private fun onItemMessageClicked(value: String) {
        CieloDialog.create(
            title = getString(R.string.chargeback_label_message_with_colon),
            message = value.wrapInDoubleQuotes
        )
            .closeButtonVisible(true)
            .setPrimaryButton(getString(R.string.text_close))
            .setTitleTextAppearance(R.style.bold_montserrat_20_cloud_500)
            .show(requireActivity().supportFragmentManager, tag)
    }

    private fun setupMoreInfo() {
        binding.containerMoreInfo.setOnClickListener {
            ChargebackMoreInfoBottomSheet.create(chargeback, PENDING).show(childFragmentManager, tag)
        }
    }

    private fun setupButtonAccept() {
        binding.btnAccept.setOnClickListener {
            ChargebackAcceptConfirmBottomSheet
                .create()
                .setOnAcceptConfirmTapListener(::onAcceptConfirmed)
                .show(childFragmentManager, tag)
        }
    }

    private fun setupButtonDecline() {
        binding.btnDecline.setOnClickListener {
            findNavController().navigate(
                ChargebackPendingDetailsFragmentDirections
                    .actionChargebackPendingDetailsFragmentToChargebackRefuseFragment(
                        chargeback
                    )
            )
        }
    }

    private fun onAcceptConfirmed() = getToken()

    private fun getToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) {
                    viewModel.chargebackAccept(
                        context = context,
                        otpCode = token,
                        chargeback = chargeback
                    )
                }
                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onHideLoading() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {}
        )
    }

    private fun onSuccess() {
        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
        object : HandlerValidationToken.CallbackAnimationSuccess {
            override fun onSuccess() {
                navigation?.showCustomHandlerView(
                    contentImage = R.drawable.ic_129_secure_access_management,
                    title = getString(R.string.chargeback_accept_success_title),
                    message = getString(R.string.chargeback_accept_success_message),
                    labelSecondButton = getString(R.string.chargeback_accept_success_label_btn),
                    isShowButtonClose = true,
                    messageAlignment = View.TEXT_ALIGNMENT_CENTER,
                    callbackClose = {
                        findNavController().navigate(
                            ChargebackPendingDetailsFragmentDirections
                                .actionChargebackPendingDetailsFragmentToChargebackInitFragment(false)
                        )
                    },
                    callbackSecondButton = {
                        findNavController().navigate(
                            ChargebackPendingDetailsFragmentDirections
                                .actionChargebackPendingDetailsFragmentToChargebackInitFragment(true)
                        )
                    }
                )
                ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDIND_ACCEPT_SUCESS)
            }
        })
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    getToken()
                }
            }
        )
    }

    private fun onError(error: NewErrorMessage?) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                navigation?.showCustomHandlerView(
                    title = getString(R.string.commons_generic_error_title),
                    message = requireContext().getNewErrorMessage(
                        newMessage = R.string.commons_generic_error_message
                    ),
                    labelSecondButton = getString(R.string.entendi),
                    isShowButtonClose = true,
                    titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    titleStyle = R.style.Heading_600_bold_20_brand_600
                )
            }
        })
        ga4.logException(SCREEN_VIEW_CHARGEBACK_PENDING_ACCEPT, error)
    }
}