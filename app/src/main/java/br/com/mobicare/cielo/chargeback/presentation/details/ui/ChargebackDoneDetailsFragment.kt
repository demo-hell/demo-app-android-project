package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.CHARGEBACK_SALES_TREATED
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.ChargebackInfoContentAdapter
import br.com.mobicare.cielo.chargeback.presentation.details.builder.ChargebackInfoContentListBuilder
import br.com.mobicare.cielo.chargeback.presentation.details.helper.ChargebackLifecycleStateController
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDoneDetailsViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackFeatureToggleViewModel
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelector
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelectorScreenType
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.DONE
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.analytics.Action.MODAL
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.analytics.formatTextForGA4
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.formatDateToBrazilian
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.wrapInDoubleQuotes
import br.com.mobicare.cielo.databinding.FragmentChargebackDoneDetailsBinding
import br.com.mobicare.cielo.extensions.visible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargebackDoneDetailsFragment : BaseFragment(), CieloNavigationListener {

    private val ga4: ChargebackGA4 by inject()
    private var navigation: CieloNavigation? = null

    private var _binding: FragmentChargebackDoneDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: ChargebackPendingDetailsFragmentArgs by navArgs()
    private val viewModel: ChargebackDoneDetailsViewModel by viewModel()
    private val chargebackFeatureToggleViewModel: ChargebackFeatureToggleViewModel by viewModel()

    private lateinit var chargeback: Chargeback
    private var lifecycleController: ChargebackLifecycleStateController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chargeback = args.chargeback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChargebackDoneDetailsBinding.inflate(
            inflater, container, false
        ).also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        getFeatureToggles()
        setupAmount()
        setupStatus()
        setupDate()
        setupDocAttachment()
        setupDocAttachmentSender()
        setupLifecycle()
        setupGeneralInformation()
        setupMoreInfo()
        setupLifecycleController()
        setupLifecycleObserver()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS)
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
                it.setTextToolbar(EMPTY)
            }
        }
    }

    private fun getFeatureToggles() {
        chargebackFeatureToggleViewModel.getDescriptionReasonTypeFeatureToggle(chargeback.chargebackDetails)
        chargebackFeatureToggleViewModel.getShowRDRCardFeatureToggle()
    }
    private fun setupLifecycleObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onLifecycleLoading()
                is UiState.Error -> onLifecycleError(state.error)
                is UiState.Success -> onLifecycleSuccess(state.data)
                is UiState.Empty -> onLifecycleEmpty()
            }
        }
    }

    private fun onLifecycleLoading() = lifecycleController?.showLoadingIndicator()

    private fun onLifecycleError(error: NewErrorMessage?) {
        lifecycleController?.showError()
        ga4.logException(SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS, error)
    }

    private fun onLifecycleEmpty() = lifecycleController?.collapseContainer()

    private fun onLifecycleSuccess(lifecycleList: List<Lifecycle>?) {
        if (lifecycleList.isNullOrEmpty())
            onLifecycleError(null)
        else
            lifecycleController?.showList(lifecycleList)
    }

    private fun setupLifecycleController() {
        lifecycleController = ChargebackLifecycleStateController(
            binding.containerLifecycleState,
            binding.containerLifecycle
        )

        lifecycleController?.setOnReloadClickListener {
            viewModel.getChargebackLifecycle(chargeback.caseId ?: ZERO)
        }
    }

    private fun setupAmount() {
        binding.tvAmount.text = chargeback.transactionAmount?.toPtBrRealString()
            ?: ChargebackConstants.DEFAULT_EMPTY_VALUE
    }

    private fun setupStatus() {
        binding.ctlStatus.apply {
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

    private fun setupDate() {
        val date = this.chargeback.chargebackDetails?.receptionDate
        val showDateComponent = date != null
        binding.apply {
            tvDate.visible(showDateComponent)
            tvDateLabel.visible(showDateComponent)
            if(showDateComponent) tvDate.text = "${getString(R.string.chargeback_date_in)} ${date.toString().formatDateToBrazilian()}"
        }
    }

    private fun setupDocAttachment() {
        binding.apply {
            if (chargeback.isStatusDeclined) {
                tvLinkAttachment.setOnClickListener {
                    findNavController().navigate(
                        ChargebackDoneDetailsFragmentDirections
                            .actionChargebackDoneDetailsFragmentToChargebackDocumentViewFragment(chargeback)
                    )
                }
                tvLinkAttachment.visible()
            } else
                tvLinkAttachment.gone()
        }
    }

    private fun setupDocAttachmentSender() {
        binding.apply {
            val refundFileInformation = chargeback.chargebackDetails?.refundFileInformation
            if (!refundFileInformation.isNullOrEmpty()) {
                tvLinkAttachmentDocSender.setOnClickListener {
                    findNavController().navigate(
                        ChargebackDoneDetailsFragmentDirections
                            .actionChargebackDoneDetailsFragmentToChargebackDocumentSenderFragment(chargeback)
                    )
                }
                tvLinkAttachmentDocSender.visible()
            } else {
                tvLinkAttachmentDocSender.gone()
            }
        }
    }

    private fun setupLifecycle() {
        binding.apply {
            chargeback.lifecycle?.apply {
                tvLifecycleAction.text = action ?: ChargebackConstants.DEFAULT_EMPTY_VALUE
                tvLifecycleActionDate.text = getString(
                    R.string.chargeback_lifecycle_action_date, actionDate?.convertToBrDateFormat())
            }
        }
    }
    private fun setupGeneralInformation() {
        binding.rvInfo.apply {
            adapter = ChargebackInfoContentAdapter(
                chargebackInfoContentList = ChargebackInfoContentListBuilder(showRDRCardFeatureToggle = chargebackFeatureToggleViewModel.showRDRCardInDetails,res = context.resources,context = requireContext()).build(chargeback)
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

    private fun setupMoreInfo() {
        binding.containerMoreInfo.setOnClickListener {
            ChargebackMoreInfoBottomSheet.create(chargeback, DONE).show(childFragmentManager, tag)
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
            ga4.logDisplayContentTreatedChargebacks(
                MODAL,
                CHARGEBACK_SALES_TREATED,
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
}