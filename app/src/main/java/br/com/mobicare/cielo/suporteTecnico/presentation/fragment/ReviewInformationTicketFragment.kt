package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_OPEN_TICKET_ERROR
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_OPEN_TICKET_SUCESSO
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentReviewInformationTicketBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatStringToPhone
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.ScheduleAvailabilityViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.UIStatePostOrdersReplacements
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class ReviewInformationTicketFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentReviewInformationTicketBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleAvailabilityViewModel by viewModel()
    private val args: ReviewInformationTicketFragmentArgs by navArgs()
    private lateinit var openTicket: OpenTicket
    private var problemSelected: String? = null

    private var navigation: CieloNavigation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTicket = args.requestTicket
        problemSelected = args.problemSelected
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentReviewInformationTicketBinding.inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserverPostOrdersReplacements()
        setListeners()
        setupViewContact()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = true)
            navigation?.showCloseButton(isShow = false)
            navigation?.showHelpButton(isShow = false)
        }
    }

    private fun openCall() {
        viewModel.postOrdersReplacements(openTicket)
    }

    private fun setupObserverPostOrdersReplacements() {
        viewModel.postOrdersReplacements.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIStatePostOrdersReplacements.Loading -> {
                    showLoadingPost()
                }

                is UIStatePostOrdersReplacements.UpdateLoadingMessage -> {
                    changeLoadingMessage()
                }

                is UIStatePostOrdersReplacements.Success -> {
                    state.data?.let { navigateToCentralOpenTicket(it) }
                }

                is UIStatePostOrdersReplacements.Error -> state.error?.let {
                    onError(it)
                }

                is UIStatePostOrdersReplacements.Empty -> onEmpty()
            }
        }
    }

    private fun showLoadingPost() {
        binding.loading.startAnimation(
            message = R.string.loading_message,
            false
        )
    }

    private fun changeLoadingMessage() {
        binding.loading.updateMessage(R.string.text_animation_loading_finish)
    }

    private fun navigateToCentralOpenTicket(protocol: OrderReplacementResponse) {
        GA4.logScreenView(PATH_OPEN_TICKET_SUCESSO)
        binding.loading.showAnimationSuccess()
        findNavController().navigate(
            ReviewInformationTicketFragmentDirections.actionReviewInformationTicketFragmentToRequestSuccessfullyOpenedFragment(
                protocol
            )
        )
    }

    private fun hideHandlerView() {
        binding.apply {
            customHandlerView.gone()
            loading.gone()
        }
    }

    private fun setupCustomHandlerView(
        title: String,
        message: String,
        labelSecondaryButton: String = EMPTY_STRING,
        labelContained: String,
        onSecondaryButtonClick: (View) -> Unit = {},
        onPrimaryButtonClick: (View) -> Unit,
    ) {
        binding.customHandlerView.apply {
            visible()
            this.title = title
            this.message = message
            this.labelSecondaryButton = labelSecondaryButton
            labelPrimaryButton = labelContained
            isShowBackButton = false
            isShowIconButtonEndHeader = false
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START
            messageAlignment = View.TEXT_ALIGNMENT_TEXT_START
            setOnBackButtonClickListener {
                hideHandlerView()
            }
            cardInformationData = null
            setOnSecondaryButtonClickListener(onSecondaryButtonClick)
            setOnPrimaryButtonClickListener(onPrimaryButtonClick)
        }
    }

    private fun onEmpty() {
        setupCustomHandler()
    }

    private fun onError(error: NewErrorMessage) {
        GA4.logException(
            screenName = PATH_OPEN_TICKET_ERROR,
            errorCode = error.httpCode.toString(),
            errorMessage = error.message
        )
        setupCustomHandler()
    }

    private fun setupCustomHandler() {
        doWhenResumed {
            setupCustomHandlerView(
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                message = getString(R.string.tap_on_phone_initialize_terminal_generic_error_message),
                labelSecondaryButton = getString(R.string.back),
                labelContained = getString(R.string.text_try_again_label),
                onSecondaryButtonClick = {
                    hideHandlerView()
                },
                onPrimaryButtonClick = {
                    hideHandlerView()
                    openCall()
                },
            )
        }
    }

    private fun setupViewContact() {
        val phoneInputFirst = openTicket.phones.first()
        binding.apply {
            tvProblem.text = problemSelected
            tvLogicalNumber.text = openTicket.logicalNumber
            openTicket.address?.let {
                tvAddress.text = getString(
                    R.string.address_complete,
                    it.streetAddress,
                    it.number,
                    it.streetAddress2 ?: EMPTY,
                    it.neighborhood,
                    it.city,
                    it.state,
                    it.zipCode
                )
            }
            tvAddress.text = getString(
                R.string.address_complete,
                openTicket.address?.streetAddress,
                openTicket.address?.number,
                openTicket.address?.streetAddress2 ?: EMPTY,
                openTicket.address?.neighborhood,
                openTicket.address?.city,
                openTicket.address?.state,
                openTicket.address?.zipCode
            )

            tvFacade.text = openTicket.address?.storeFront
            tvReferencePoint.text = openTicket.address?.landMark
            val phoneNumber = phoneInputFirst?.apply {
                areaCode + number
            }

            tvPhoneNumber.text = phoneNumber.toString().formatStringToPhone().ifNullSimpleLine()
            tvContactName.text = openTicket.contactName
            tvBusinessHour.text = openTicket.openingHourText
        }
    }

    private fun setListeners() {
        binding.apply {
            btnOpenCall.setOnClickListener {
                openCall()
            }
        }
    }
}