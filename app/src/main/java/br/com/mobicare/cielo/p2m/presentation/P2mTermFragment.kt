package br.com.mobicare.cielo.p2m.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.LgpdLinks
import br.com.mobicare.cielo.commons.constants.WhatsApp
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.databinding.FragmentP2mTermBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.p2m.analytics.P2MGA4
import br.com.mobicare.cielo.p2m.presentation.viewmodel.P2mAcreditationViewModel
import br.com.mobicare.cielo.p2m.utils.UiP2mAcceptState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Constants.BANNER_ID_P2M

class P2mTermFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentP2mTermBinding? = null
    private val viewModel: P2mAcreditationViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    private val args: P2mTermFragmentArgs by navArgs()
    private var isSelectedThirty: Boolean = false
    private val ga4: P2MGA4 by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSelectedThirty = args.isSelectedThirty
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentP2mTermBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        observeAcceptState()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(P2MGA4.SCREEN_VIEW_P2M_TERM)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this@P2mTermFragment)
            navigation?.showButton(true)
            navigation?.enableButton(false)
            navigation?.setTextButton(getString(R.string.btn_send_terms_p2m))
            navigation?.configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        show = true,
                        toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                            menuRes = R.menu.menu_common_only_faq_blue,
                            onOptionsItemSelected = {
                                if((it.itemId == R.id.action_help)) { showFaqOnBrowser() }
                            }
                        ),
                        showBackButton = true,
                    )
                )
            }
    }

    private fun showFaqOnBrowser(){
        Utils.openBrowser(requireActivity(), WhatsApp.LINK_TO_SALES_WHATS_APP)
    }

    override fun onButtonClicked(labelButton: String) {
        goCheckCardSelectedBeforeToSend()
    }

    private fun goCheckCardSelectedBeforeToSend() {
        checkCardSelectedBeforeToSend()
    }

    private fun observeAcceptState() {
        viewModel.p2mAcceptUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiP2mAcceptState.Success, UiP2mAcceptState.Empty -> showLoadingSuccess()
                is UiP2mAcceptState.Error -> onError(state.message)
                is UiP2mAcceptState.ShowLoading -> showLoading()
                is UiP2mAcceptState.HideLoading -> hideLoading()
            }
        }
    }

    private fun onError(error: NewErrorMessage?) {
        ga4.logException(
            screenName = P2MGA4.SCREEN_VIEW_P2M_TERM,
            error = error
        )

        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.img_10_erro,
                title = getString(R.string.commons_generic_error_title),
                message = requireContext().getNewErrorMessage(
                    newMessage = R.string.commons_generic_error_message
                ),
                labelSecondButton = getString(R.string.btn_two_error),
                callbackSecondButton = {
                    checkCardSelectedBeforeToSend()
                },
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                titleStyle = R.style.Heading_600_bold_20_brand_600,
                labelFirstButton = getString(R.string.back),
                callbackFirstButton = {},
                callbackClose = {
                    returnHome()
                },
                isShowFirstButton = true,
                isShowSecondButton = true,
            )
        }
        showToolbarAndButton()
    }

    private fun showToolbarAndButton(){
        navigation?.showButton(true)
        navigation?.configureCollapsingToolbar(
            CollapsingToolbarBaseActivity.Configurator(
                show = true,
                toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {
                        if((it.itemId == R.id.action_help)) { showFaqOnBrowser() }
                    }
                ),
                showBackButton = true,
            )
        )
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading(R.string.p2m_load_wait)
        navigation?.showButton(false)
        navigation?.configureCollapsingToolbar(
            CollapsingToolbarBaseActivity.Configurator(
                show = true,
                showBackButton = false,
            )
        )
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun showLoadingSuccess() {
        doWhenResumed {
            navigation?.showAnimatedLoadingSuccess(
                message = R.string.p2m_all_right,
                onAction = {
                    goToP2mEnd()
                })
        }
    }

    private fun setupListeners() {
        binding?.apply {
            containerP2mAuthorizationAgreement.apply {
                containerReadP2mTerms.apply {
                    isClickable = true
                    setOnClickListener {
                        Utils.openLink(requireActivity(), LgpdLinks.PrivatePolicyLink)
                    }
                    include.checkBoxP2m.setOnCheckedChangeListener { _, selected ->
                        navigation?.enableButton(selected)
                    }
                }
            }
        }
    }

    private fun checkCardSelectedBeforeToSend(){
        if (isSelectedThirty)
            goToP2mEnd()
        else
            sendAccept()
    }

    private fun sendAccept() {
        viewModel.p2mAccept(
            context = context,
            bannerId = BANNER_ID_P2M
        )
    }

    private fun goToP2mEnd() {
        ga4.logPurchase(isSelectedThirty)
        findNavController().navigate(
            P2mTermFragmentDirections
                .actionP2mTermFragmentToP2mEndFragment()
        )
    }

    private fun returnHome() {
        requireActivity().backToHome()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}

