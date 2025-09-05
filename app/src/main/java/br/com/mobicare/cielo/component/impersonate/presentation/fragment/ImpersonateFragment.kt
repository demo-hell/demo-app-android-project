package br.com.mobicare.cielo.component.impersonate.presentation.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.UILoadingState
import br.com.mobicare.cielo.component.impersonate.presentation.fragment.adapter.ImpersonateMerchantsAdapter
import br.com.mobicare.cielo.component.impersonate.presentation.model.ImpersonateUI
import br.com.mobicare.cielo.component.impersonate.presentation.viewModel.ImpersonateViewModel
import br.com.mobicare.cielo.component.impersonate.utils.UIImpersonateState
import br.com.mobicare.cielo.databinding.FragmentImpersonateBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity.ChannelMainBottomNavigationActivity
import br.com.mobicare.cielo.main.presentation.util.MessageChannelMainBottomNavigationEnum
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImpersonateFragment : BaseFragment(), CieloNavigationListener, AllowMeContract.View {

    private val viewModel: ImpersonateViewModel by viewModel()

    private var binding: FragmentImpersonateBinding? = null
    private var navigation: CieloNavigation? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private val merchantsAdapter = ImpersonateMerchantsAdapter(::onTapEc)
    private val toolbar = CollapsingToolbarBaseActivity.Configurator(show = false)

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private lateinit var allowMeContextual: AllowMeContextual

    private var impersonateUI: ImpersonateUI? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImpersonateBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allowMeContextual = allowMePresenter.init(requireContext())
        setupNavigation()
        getImpersonatingUIFromActivity()
        setupView()
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    override fun successCollectToken(result: String) {
        viewModel.impersonate(result, impersonateUI?.typeImpersonate, impersonateUI?.flowOpenFinance)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        showImpersonatingError()
    }

    override fun getSupportFragmentManagerInstance() = childFragmentManager

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@ImpersonateFragment)
                configureCollapsingToolbar(toolbar)
                showButton(false)
            }
        }
    }

    private fun getImpersonatingUIFromActivity() {
        val data = navigation?.getData()
        if (data is ImpersonateUI) impersonateUI = data
    }

    private fun setupView() {
        impersonateUI?.let {
            viewModel.setMerchantsUI(it.merchants)
            binding?.tvSubtitle?.text = it.subTitle
            binding?.tvTitle?.text = getString(it.title)
        }
    }

    private fun setupRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        merchantsAdapter.setMerchants(viewModel.merchantsUI)

        binding?.rvListEcs?.apply {
            adapter = merchantsAdapter
            layoutManager = scrollControlledLinearManager
        }
    }

    private fun setupListeners() {
        binding?.ibBack?.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setupObservers() {
        setupObserverLoadingState()
        setupObserverImpersonateState()
    }

    private fun setupObserverLoadingState() {
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UILoadingState.ShowLoading -> showLoading(true)
                is UILoadingState.HideLoading -> showLoading(false)
            }
        }
    }

    private fun setupObserverImpersonateState() {
        viewModel.impersonateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIImpersonateState.SendMessageUpdateMainBottomNavigation -> sendMessageUpdateMainBottomNavigation()
                is UIImpersonateState.Success -> successImpersonate()
                is UIImpersonateState.ImpersonateError -> showImpersonatingError()
                is UIImpersonateState.LogoutError -> showLogoutError()
                is UIImpersonateState.WithoutAccess -> showBottomSheetWithoutAccess()
            }
        }
    }

    private fun successImpersonate() {
        requireActivity().run {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun sendMessageUpdateMainBottomNavigation() {
        ChannelMainBottomNavigationActivity.channel.trySend(
            MessageChannelMainBottomNavigationEnum.UPDATE_APP_AFTER_IMPERSONATING
        )
    }

    private fun showLoading(isShow: Boolean) {
        binding?.apply {
            llContent.visible(isShow.not())
            loading.visible(isShow)

            if (isShow) {
                loading.startAnimation(
                    message = R.string.super_link_router_loading_message,
                    false
                )
            } else {
                loading.hideAnimationStart()
            }
        }
    }

    private fun showImpersonatingError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(R.string.commons_generic_error_title),
                message = getString(R.string.commons_generic_error_message),
                labelFirstButton = getString(R.string.back),
                labelSecondButton = getString(R.string.text_try_again_label),
                isShowFirstButton = true,
                callbackSecondButton = {
                    collectFingerprint()
                },
                callbackClose = {
                    requireActivity().finish()
                }
            )
        }
    }

    private fun onTapEc(id: String) {
        viewModel.selectMerchant(id)
        collectFingerprint()
    }

    private fun collectFingerprint() {
        allowMePresenter.collect(
            mAllowMeContextual = allowMeContextual,
            context = requireActivity(),
            mandatory = false
        )
    }
    private fun showBottomSheetWithoutAccess() {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.txt_without_access_open_finance),
                showCloseButton = true
            ),
            contentLayoutRes = R.layout.layout_open_finance_without_access,
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.txt_select_other_account_open_finance),
                onTap = {
                    it.dismiss()
                }
            ),
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun showLogoutError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(R.string.impersonate_title_bs_logout_error),
                message = getString(R.string.impersonate_message_bs_logout_error),
                labelSecondButton = getString(R.string.impersonate_label_second_button_bs_logout_error),
                callbackSecondButton = {
                    baseLogout(isLoginScreen = true)
                },
                callbackClose = {
                    baseLogout(isLoginScreen = true)
                }
            )
        }
    }

}