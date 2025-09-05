package br.com.mobicare.cielo.mdr.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MDR_OFFER
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.THOUSAND
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ActivityMdrOfferBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.genericError
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.mdr.adapter.MdrFeeAndPlansAdapter
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_CONTRACTING
import br.com.mobicare.cielo.mdr.analytics.MdrAnalyticsGA4
import br.com.mobicare.cielo.mdr.domain.mapper.HiringOffersMapper.toMdrBrandsInformation
import br.com.mobicare.cielo.mdr.domain.model.MdrBrandsInformation
import br.com.mobicare.cielo.mdr.ui.state.UiMdrConfirmationState
import br.com.mobicare.cielo.mdr.ui.state.UiMdrOfferState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MdrOfferActivity : BaseActivity() {
    private lateinit var binding: ActivityMdrOfferBinding
    private var mdrFeeAndPlansAdapter: MdrFeeAndPlansAdapter? = null
    private var mdrBrandsInformation: MdrBrandsInformation? = null
    private val viewModel: MdrOfferViewModel by viewModel()
    private val analyticsGA4: MdrAnalyticsGA4 by inject()

    private val toolbarDefault
        get() =
            CieloCollapsingToolbarLayout.Configurator(
                layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
                toolbar =
                    CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.mdr_offer_title),
                        showBackButton = true,
                        menu =
                            CieloCollapsingToolbarLayout.ToolbarMenu(
                                menuRes = R.menu.menu_close,
                                onOptionsItemSelected = { goToHome() },
                            ),
                    ),
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMdrOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mdrBrandsInformation =
            (intent.getParcelableExtra(ARG_PARAM_MDR_OFFER) as HiringOffers?).toMdrBrandsInformation()
        onBackPressedDispatcher.addCallback(this) {
            goToHome()
        }
        init()
    }

    override fun onResume() {
        super.onResume()
        trackPromotion()
    }

    private fun init() {
        viewModel.updateMdrOfferState(mdrBrandsInformation?.id)
        initializeCollapsingToolbarLayout()
        setupInformation()
        setupFeeRecyclerView()
        updateCollapsingToolbar()
        setupObservables()
        setupButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding.cieloCollapsingToolbarLayout.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return binding.cieloCollapsingToolbarLayout.onOptionsItemSelected(item)
    }

    private fun initializeCollapsingToolbarLayout() {
        binding.cieloCollapsingToolbarLayout.initialize(this)
    }

    private fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding.cieloCollapsingToolbarLayout.configure(configurator)
    }

    private fun setupFeeRecyclerView() {
        mdrFeeAndPlansAdapter = MdrFeeAndPlansAdapter(mdrBrandsInformation?.cardFees)

        binding.rvFees.apply {
            layoutManager =
                LinearLayoutManager(
                    this@MdrOfferActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false,
                )
            adapter = mdrFeeAndPlansAdapter
            addItemDecoration(CircleIndicatorItemDecoration(context))
        }
    }

    private fun setupInformation() {
        with(binding) {
            tvAverageRentValue.text = getString(R.string.mdr_offer_average_rent_value, mdrBrandsInformation?.defaultRentValue.toString())
            tvEquipmentQuantityValue.text =
                mdrBrandsInformation?.equipmentQuantity.toString()
        }
    }

    private fun setupObservables() {
        setupMdrOfferObserver()
        mdrConfirmationStateObserver()
    }

    private fun setupMdrOfferObserver() {
        viewModel.mdrOfferState.observe(this) {
            when (it) {
                is UiMdrOfferState.ShowPostponedWithRR -> showPostponedOfferWithRR()
                is UiMdrOfferState.ShowPostponedWithoutRR -> showPostponedOfferWithoutRR()
                is UiMdrOfferState.ShowWithoutPostponedWithRR -> showWithoutPostponedOfferWithRR()
                is UiMdrOfferState.ShowWithoutPostponedWithoutRR -> showWithoutPostponedOfferWithoutRR()
                is UiMdrOfferState.ShowWithoutEquipmentWithRR -> showWithoutEquipmentWithRR()
                is UiMdrOfferState.ShowWithoutEquipmentWithoutRR -> showWithoutEquipmentWithoutRR()
                is UiMdrOfferState.Error -> setupGenericError()
            }
        }
    }

    private fun mdrConfirmationStateObserver() {
        viewModel.mdrConfirmationState.observe(this) {
            when (it) {
                is UiMdrConfirmationState.ShowLoading -> showLoading()
                is UiMdrConfirmationState.HideLoading -> hideLoading()
                is UiMdrConfirmationState.AcceptSuccess -> onAcceptSuccess()
                is UiMdrConfirmationState.RejectSuccess -> onRejectSuccess()
                is UiMdrConfirmationState.Error -> onError(it.error, it.isAccepted, it.screenName)
            }
        }
    }

    private fun showPostponedOfferWithRR() {
        setupWithPostponedInformation()
        setupWithRRInformation()
    }

    private fun showPostponedOfferWithoutRR() {
        setupWithPostponedInformation()
        setupWithoutRRInformation()
    }

    private fun showWithoutPostponedOfferWithRR() {
        setupWithoutPostponedInformation()
        setupWithRRInformation()
    }

    private fun showWithoutPostponedOfferWithoutRR() {
        setupWithoutPostponedInformation()
        setupWithoutRRInformation()
    }

    private fun showWithoutEquipmentWithRR() {
        setupWithoutEquipmentInformation()
        setupWithRRInformation()
    }

    private fun showWithoutEquipmentWithoutRR() {
        setupWithoutEquipmentInformation()
        setupWithoutRRInformation()
    }

    private fun setupWithPostponedInformation() {
        with(binding) {
            tvAverageRentValue.text = getString(R.string.mdr_offer_average_rent_value, mdrBrandsInformation?.defaultRentValue.toString())
            tvEquipmentQuantityValue.text =
                mdrBrandsInformation?.equipmentQuantity.toString()
            cieloOfferAlert.cardText =
                getString(
                    R.string.mdr_offer_alert_postponed,
                    mdrBrandsInformation?.billingGoal?.toPtBrRealString(true),
                    mdrBrandsInformation?.surplusTarget?.toPtBrRealString(true),
                )
            averageRentViewGroup.visible()
        }
    }

    private fun setupWithoutPostponedInformation() {
        with(binding) {
            tvAverageRentValue.text = getString(R.string.mdr_offer_average_rent_value, mdrBrandsInformation?.defaultRentValue.toString())
            tvEquipmentQuantityValue.text =
                mdrBrandsInformation?.equipmentQuantity.toString()
            cieloOfferAlert.cardText =
                getString(
                    R.string.mdr_offer_alert_without_postponed,
                    mdrBrandsInformation?.billingGoal?.toPtBrRealString(true),
                )
            averageRentViewGroup.visible()
        }
    }

    private fun setupWithoutEquipmentInformation() {
        with(binding) {
            tvAverageRentValue.text = getString(R.string.mdr_offer_average_rent_value, mdrBrandsInformation?.defaultRentValue.toString())
            tvEquipmentQuantityValue.text =
                mdrBrandsInformation?.equipmentQuantity.toString()
            cieloOfferAlert.cardText =
                getString(
                    R.string.mdr_offer_alert_without_machine,
                    mdrBrandsInformation?.billingGoal?.toPtBrRealString(true),
                )
            averageRentViewGroup.gone()
        }
    }

    private fun setupGenericError() {
        binding.root.gone()
        genericError(
            onFirstAction = {},
            onSecondAction = ::goToHome,
            onSwipeAction = ::goToHome,
            isShowFirstButton = false,
        )
    }

    private fun setupWithRRInformation() {
        with(binding) {
            tvMdrSubtitle.text = getString(R.string.mdr_offer_with_ra_description).htmlTextFormat()
            tvOfferCashCredit.text =
                getString(
                    R.string.mdr_offer_cash_credit,
                    mdrBrandsInformation?.creditFactorGetFastMensal.toString(),
                ).htmlTextFormat()
            tvOfferInstallmentCredit.text =
                getString(
                    R.string.mdr_offer_installment_credit,
                    mdrBrandsInformation?.installmentFactorGetFastMensal.toString(),
                ).htmlTextFormat()
            receiveAutomaticViewGroup.visible()
        }
    }

    private fun setupWithoutRRInformation() {
        with(binding) {
            tvMdrSubtitle.text = getString(R.string.mdr_offer_description).htmlTextFormat()
            receiveAutomaticViewGroup.gone()
        }
    }

    private fun setupButtons() {
        with(binding) {
            btnAccept.apply {
                setTextAppearance(R.style.semi_bold_montserrat_16)
                setOnClickListener {
                    trackClick(
                        screenName = SCREEN_VIEW_MDR_HOME,
                        buttonName = getString(R.string.mdr_offer_button_accept),
                    )
                    showAcceptBS()
                }
            }
            btnDecline.apply {
                setTextAppearance(R.style.semi_bold_montserrat_16)
                setOnClickListener {
                    trackClick(
                        screenName = SCREEN_VIEW_MDR_HOME,
                        buttonName = getString(R.string.mdr_offer_button_do_not_show_again),
                    )
                    viewModel.postContractUserDecision(
                        mdrBrandsInformation?.apiId,
                        mdrBrandsInformation?.id,
                        false,
                    )
                }
            }
        }
    }

    private fun updateCollapsingToolbar() {
        configureCollapsingToolbar(toolbarDefault)
    }

    private fun showLoading() {
        binding.animatedProgressView.startAnimation(R.string.wait_animated_loading_start_message)
    }

    private fun hideLoading() {
        binding.animatedProgressView.hideAnimationStart()
    }

    private fun showAcceptBS() {
        CieloMessageBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.bs_mdr_offer_accept_title),
                ),
            message =
                CieloMessageBottomSheet.Message(
                    text = getString(R.string.bs_mdr_offer_accept_description),
                    illustration = R.drawable.ic_06,
                ),
            mainButtonConfigurator =
                CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.bs_mdr_offer_accept_continue),
                    onTap = {
                        trackClick(
                            screenName = SCREEN_VIEW_MDR_HOME_CONTRACTING,
                            buttonName = getString(R.string.bs_mdr_offer_accept_continue),
                        )
                        it.dismiss()
                        viewModel.postContractUserDecision(
                            mdrBrandsInformation?.apiId,
                            mdrBrandsInformation?.id,
                            true,
                        )
                    },
                ),
            secondaryButtonConfigurator =
                CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.bs_mdr_offer_accept_cancel),
                    onTap = {
                        trackClick(
                            screenName = SCREEN_VIEW_MDR_HOME_CONTRACTING,
                            buttonName = getString(R.string.bs_mdr_offer_accept_cancel),
                        )
                        it.dismiss()
                    },
                ),
        ).show(supportFragmentManager, this.javaClass.name)
    }

    private fun setupCustomHandlerView(
        title: String,
        message: String,
        labelSecondaryButton: String = EMPTY_STRING,
        labelContained: String,
        contentImage: Int,
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
            illustration = contentImage
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

    private fun onAcceptSuccess() {
        trackPurchase()
        doWhenResumed {
            setupCustomHandlerView(
                title = getString(R.string.mdr_offer_accept_success),
                message = getString(R.string.mdr_offer_accept_success_description),
                labelContained = getString(R.string.mdr_offer_accept_success_conclude),
                contentImage = R.drawable.ic_08,
                onPrimaryButtonClick = {
                    hideHandlerView()
                    goToHome()
                },
            )
        }
    }

    private fun onRejectSuccess() {
        hideHandlerView()
        setupToast()
    }

    private fun onError(
        error: NewErrorMessage?,
        isAccepted: Boolean,
        screenName: String,
    ) {
        doWhenResumed {
            trackException(screenName, error)
            setupCustomHandlerView(
                title = error?.title ?: getString(R.string.generic_error_title),
                message = error?.message ?: getString(R.string.generic_error_message),
                labelSecondaryButton = getString(R.string.back),
                labelContained = getString(R.string.text_try_again_label),
                contentImage = R.drawable.ic_07,
                onSecondaryButtonClick = {
                    hideHandlerView()
                },
                onPrimaryButtonClick = {
                    hideHandlerView()
                    viewModel.postContractUserDecision(
                        mdrBrandsInformation?.apiId,
                        mdrBrandsInformation?.id,
                        isAccepted,
                    )
                },
            )
        }
    }

    private fun hideHandlerView() {
        binding.customHandlerView.gone()
    }

    private fun setupToast() {
        Toast.makeText(this, R.string.mdr_offer_reject_success_message, Toast.LENGTH_LONG).show()
        binding.btnAccept.isEnabled = false
        binding.btnDecline.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            goToHome()
        }, THOUSAND)
    }

    private fun goToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    private fun trackPromotion() {
        analyticsGA4.logViewPromotion(
            promotionId = mdrBrandsInformation?.id.orZero,
            promotionName = mdrBrandsInformation?.name.orEmpty(),
        )
    }

    private fun trackClick(
        screenName: String,
        buttonName: String,
    ) {
        analyticsGA4.logClick(
            screenName = screenName,
            promotionId = mdrBrandsInformation?.id.orZero,
            promotionName = mdrBrandsInformation?.name.orEmpty(),
            contentName = buttonName,
        )
    }

    private fun trackPurchase() {
        analyticsGA4.logPurchase(
            promotionName = mdrBrandsInformation?.name.orEmpty(),
            transactionId = MdrAnalyticsGA4.tMdrOfferTimeStamp,
        )
    }

    private fun trackException(
        screenName: String,
        error: NewErrorMessage?,
    ) {
        analyticsGA4.logException(
            screenName = screenName,
            error = error,
        )
    }
}
