package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.consentDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.carousel.model.ItemCarouselModel
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_FORMAT_DATE_TIME
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.Utils.openBrowser
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.OpenFinanceConsentDetailBinding
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.OpenFinanceFlowNewShareActivity
import br.com.mobicare.cielo.openFinance.presentation.utils.CheckStatus
import br.com.mobicare.cielo.openFinance.presentation.utils.DefaultIconBank.checkTypeImage
import br.com.mobicare.cielo.openFinance.presentation.utils.OpenFinanceCarousels
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentStatus
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateEndShare
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowOptions
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.BRAND_SELECTED
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.CHANGE_SHARE
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.CITIZEN_PORTAL
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RENEW_SHARE
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_SHARE
import com.google.gson.Gson
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConsentDetailFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceConsentDetailBinding? = null
    private val consentDetailViewModel: ConsentDetailViewModel by viewModel()
    private var navigation: CieloNavigation? = null
    private val args: ConsentDetailFragmentArgs by navArgs()
    private val handlerValidationToken: HandlerValidationToken by inject()

    private val toolbarDefault
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.consent_details),
                showBackButton = true,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {}
                ),
            )
        )

    private val toolbarFlowConclusion
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.consent_details),
                showBackButton = true,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {}
                ),
                onBackPressed = { requireActivity().finish() }
            ),
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceConsentDetailBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setMinimumHeight()
        configureToolbar()
        consentDetailViewModel.getConsentDetail(args.stringConsentId, requireContext())
        observeConsentDetail()
        observeShowOptions()
        observeEndShare()
        consentStatusObserver()
        setListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@ConsentDetailFragment)
            }
        }
    }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun hideIdentification(consentDetail: ConsentDetail){
        if (consentDetail.userName.isNullOrEmpty()){
            binding?.layoutIdentification.gone()
        } else {
            binding?.layoutIdentification?.visible()
            binding?.tvIdentification?.text = consentDetail.userName
        }
    }

    private fun consentStatusObserver(){
        consentDetailViewModel.consentDetailStatus.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UIStateConsentStatus.Active -> {
                    statusActive()
                }

                is UIStateConsentStatus.Expired -> {
                    uiState.data?.let{
                        statusExpired(it)
                    }
                }

                is UIStateConsentStatus.Closed -> {
                    uiState.data?.let{
                        statusClosed(it)
                    }
                }

                UIStateConsentStatus.Empty -> {
                    otherStatus()
                }
            }
        }
    }

    private fun statusActive() {
        binding?.apply {
            endSharing.visible()
            closedShared.gone()
        }
    }

    private fun statusExpired(consentDetail: ConsentDetail) {
        binding?.apply {
            endSharing.gone()
            tvClosedShared.text = getString(
                R.string.data_share_expired,
                consentDetail.expirationDateTime.isoDateToBrHourAndMinute(
                    SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
                    SIMPLE_DATE_FORMAT_DATE_TIME
                )
            ).fromHtml()
        }
    }

    private fun statusClosed(consentDetail: ConsentDetail) {
        binding?.apply {
            endSharing.gone()
            tvClosedShared.text = getString(
                R.string.data_share_closed,
                consentDetail.expirationDateTime.isoDateToBrHourAndMinute(
                    SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
                    SIMPLE_DATE_FORMAT_DATE_TIME
                )
            ).fromHtml()
        }
    }

    private fun otherStatus() {
        binding?.apply {
            endSharing.gone()
            closedShared.gone()
        }
    }

    private fun mountView(consentDetail: ConsentDetail) {
        binding?.apply {
            tvDataOrigin.text = consentDetail.brand
            tvDocumentation.text = applyMasks(consentDetail.document)
            tvStatus.text = consentDetail.consentSatus
            tvStatus.setTextAppearance(
                CheckStatus.getStatus(
                    requireContext(),
                    consentDetail.consentSatus
                ).textStyle
            )
            iconStatus.setImageResource(
                CheckStatus.getStatus(
                    requireContext(),
                    consentDetail.consentSatus
                ).icon
            )
            contentStatus.setBackgroundResource(
                CheckStatus.getStatus(
                    requireContext(),
                    consentDetail.consentSatus
                ).background
            )
            checkTypeImage(consentDetail.logoUri, logoOriginData, requireContext())
            carousel.setList(listCarousel(consentDetail.flow))
            tvDateExpires.text = consentDetail.expirationDateTime.formatterDate(
                SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
                SIMPLE_DT_FORMAT_MASK
            )
            tvDateRequested.text = consentDetail.confirmationDateTime.formatterDate(
                SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
                SIMPLE_DATE_FORMAT_DATE_TIME
            )
            checkOptions(
                consentDetail.alteration,
                consentDetail.renovation,
                consentDetail.cancelation
            )
        }
        hideIdentification(consentDetail)
    }

    private fun observeConsentDetail() {
        consentDetailViewModel.getConsentDetailLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsentDetail.Success -> {
                    uiState.data?.let { mountView(it) }
                    successConsentDetail()
                }

                is UIStateConsentDetail.Error -> {
                    errorConsentDetail()
                }

                is UIStateConsentDetail.Loading -> {
                    loadingConsentDetail()
                }
            }
        }
    }

    private fun observeShowOptions() {
        consentDetailViewModel.getShowOptionsLiveData.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowOptions.ShowOptions -> {
                        containerOptions.visible()
                    }

                    is UIStateShowOptions.HideOptions -> {
                        containerOptions.gone()
                        viewLineSeparator.gone()
                    }
                }
            }
        }
    }

    private fun checkOptions(alteration: Boolean, renovation: Boolean, cancelation: Boolean) {
        disableSimpleRenovation()
        if (alteration.not()) { disableAlteration() }
        if (renovation.not()) { disableRenovation() }
        if (cancelation.not()){ disableCancelation() }
    }

    private fun disableAlteration() {
        binding?.apply {
            arrowChangeInformation.invisible()
            tvChangeInformation.setTextAppearance(R.style.regular_montserrat_16_cloud_500_spacing_4)
            iconChangeInformation.setImageResource(R.drawable.ic_edit_neutral_500)
        }
    }

    private fun disableRenovation() {
        binding?.apply {
            iconUpdateTerm.setImageResource(R.drawable.ic_calendar_clock_neutral_500)
            tvUpdateTerm.setTextAppearance(R.style.regular_montserrat_16_cloud_500_spacing_4)
            arrowUpdateTerm.invisible()
        }
    }

    private fun disableSimpleRenovation() {
        binding?.apply {
            iconSimpleRenovation.setImageResource(R.drawable.ic_calendar_neutral_500)
            tvSimpleRenovation.setTextAppearance(R.style.regular_montserrat_16_cloud_500_spacing_4)
            arrowSimpleRenovation.invisible()
        }
    }

    private fun disableCancelation() {
        binding?.endSharing.gone()
    }

    private fun applyMasks(text: String): String {
        return when {
            ValidationUtils.isCNPJ(text) -> addMaskCPForCNPJ(
                text,
                getString(R.string.mask_cnpj_step4)
            )

            ValidationUtils.isCPF(text) -> addMaskCPForCNPJ(
                text,
                getString(R.string.mask_cpf_step4)
            )

            else -> text
        }
    }

    private fun successConsentDetail() {
        binding?.apply {
            containerView.visible()
            shimmerLoading.gone()
            containerError.gone()
        }
    }

    private fun errorConsentDetail() {
        binding?.apply {
            containerView.gone()
            shimmerLoading.gone()
            containerError.visible()
        }
    }

    private fun loadingConsentDetail() {
        binding?.apply {
            containerView.gone()
            containerError.gone()
        }
    }

    private fun listCarousel(journey: String): List<ItemCarouselModel> {
        return when {
            journey.equals(OpenFinanceConstants.RECEIVING_JOURNEY) -> {
                OpenFinanceCarousels.getListReceiving(requireContext())
            }

            else -> {
                OpenFinanceCarousels.getListTransmitting(requireContext())
            }
        }
    }

    private fun setListeners() {
        binding?.apply {
            citizenPortal.setOnClickListener {
                openBrowser(requireActivity(), CITIZEN_PORTAL)
            }
            containerUpdateTerm.setOnClickListener {
                backListing()
                consentDetailViewModel.saveInfoDetailsShare(RENEW_SHARE)
                requireActivity().startActivity<OpenFinanceFlowNewShareActivity>(
                    BRAND_SELECTED to Gson().toJson(consentDetailViewModel.getBrandToChangeOrRenew()),
                    TYPE_SHARE to ONE
                )
            }
            containerChangeInformation.setOnClickListener {
                backListing()
                consentDetailViewModel.saveInfoDetailsShare(CHANGE_SHARE)
                requireActivity().startActivity<OpenFinanceFlowNewShareActivity>(
                    BRAND_SELECTED to Gson().toJson(consentDetailViewModel.getBrandToChangeOrRenew()),
                    TYPE_SHARE to TWO
                )
            }
            endSharing.setOnClickListener {
                showBSCancelShare()
            }
        }
    }

    private fun configureToolbar() {
        if (args?.stringFlowConclusion) {
            navigation?.configureCollapsingToolbar(toolbarFlowConclusion)
        } else {
            navigation?.configureCollapsingToolbar(toolbarDefault)
        }
    }

    private fun backListing(){
        if (args.stringFlowConclusion.not()) requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showBSCancelShare() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.end_sharing)),
                contentLayoutRes = R.layout.layout_open_finance_bs_end_sharing,
                mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.confirm_close_sharing),
                    onTap = {
                        it.dismiss()
                        verifyToken()
                    },
                    drawableRes = R.drawable.button_background_selector_red_500
                ),
                secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.change_ec_btn_back),
                    onTap = {
                        it.dismiss()
                    }
                ),
            ).show(childFragmentManager, EMPTY)
    }

    private fun showHandlerUnavailableService() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.bs_claim_generic_error_title),
            message = getString(R.string.txt_pix_open_finance_unavailable_service),
            labelSecondButton = getString(R.string.ok_understand),
            isShowFirstButton = false,
            isShowSecondButton = true,
            isShowButtonBack = false,
        )
    }

    private fun showHandlerEndShareSuccess() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_sucesso_celular,
            title = getString(R.string.title_end_share),
            message = getString(R.string.desc_end_share),
            labelSecondButton = getString(R.string.verify_shares),
            isShowFirstButton = false,
            isShowSecondButton = true,
            isShowButtonBack = false,
            callbackSecondButton = {
                if (args.stringFlowConclusion) flowEndSharingConclusion()
                else requireActivity().onBackPressed()
            },
            callbackClose = {
                requireActivity().onBackPressed()
            }
        )
    }

    private fun verifyToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) =
                    consentDetailViewModel.endShare(token)

                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    verifyToken()
                }
            }
        )
    }

    private fun hideLoadingToken() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {}
        )
    }

    private fun observeEndShare() {
        consentDetailViewModel.endShareLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UIStateEndShare.ErrorEndShare -> {
                    hideLoadingToken()
                    showHandlerUnavailableService()
                }
                UIStateEndShare.SuccessEndShare -> {
                    hideLoadingToken()
                    showHandlerEndShareSuccess()
                }
                UIStateEndShare.WithoutAccessEndShare -> {
                    hideLoadingToken()
                    RoleWithoutAccessHandler.showNoAccessAlert(requireActivity())
                }
            }
        }
    }

    private fun flowEndSharingConclusion(){
        findNavController().navigate(
            ConsentDetailFragmentDirections.actionConsentDetailFragmentToOpenFinanceSharedDataFragment(true),
            NavOptions.Builder().setPopUpTo(R.id.consentDetailFragment, true).build()
        )
    }
}