package br.com.mobicare.cielo.changeEc.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.adicaoEc.presentation.ui.AddEcActivity
import br.com.mobicare.cielo.adicaoEc.presentation.ui.IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.changeEc.ChangeEcMerchantAdapter
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.activity.ChangeEcListener
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.presenter.ChangeEcMerchantPresenter
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_change_ec.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class ChangeEcMerchantFragment : BaseFragment(), ChanceEcFragmentContract.View, AllowMeContract.View {

    private lateinit var mPresenter: ChangeEcMerchantPresenter
    private val mRepository: ChangeEcRepository by inject()
    private var mAdapter: ChangeEcMerchantAdapter? = null
    private var mChangeEcListener: ChangeEcListener? = null
    private val analytics: HomeGA4 by inject()
    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_ec, container, false)
    }
    private lateinit var mAllowMeContextual: AllowMeContextual
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }
    private lateinit var currentMerchant:Merchant
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAllowMeContextual = allowMePresenter.init(requireContext())
        mChangeEcListener?.onButtonLeftVisible(false)

        configViews()
        configMfaRouteHandler()
        configListeners()
        configPresenter()
        configEditSearch()
    }

    private fun configMfaRouteHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        mfaRouteHandler.checkIsMfaEligible { isEligible ->
            tvAddEc.visible(isEligible)
        }
    }

    override fun onResume() {
        super.onResume()
        mfaRouteHandler.onResume()
    }

    private fun configViews() {
        analytics.logHomeChangeEcScreenView(this.javaClass)
        this.backButton?.visibility = View.GONE
    }

    private fun configListeners() {
        tvAddEc.setOnClickListener {
            analytics.logHomeChangeEcClick(this.javaClass)
            mfaRouteHandler.runWithMfaToken {
                context?.startActivity<AddEcActivity>(
                    ScreenView.SCREEN_NAME to HomeAnalytics.ADD_CHANGE_EC_PATH
                )
            }
        }
    }

    private fun configPresenter() {
        mPresenter = ChangeEcMerchantPresenter(mRepository)
        mPresenter.setView(this)
        mPresenter.loadItens()
    }

    private fun configEditSearch() {

        edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edit_search.removeTextChangedListener(this)
                s?.let {
                    mAdapter?.filter?.filter(edit_search.text)
                    analytics.logHomeChangeEcSearch(edit_search.text.toString())
                }
                edit_search.addTextChangedListener(this)
            }
        })

        edit_search.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                edit_search.clearFocus()

                activity?.hideSoftKeyboard()

                handled = true
            }
            handled
        }
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                mChangeEcListener?.hideLoading()
                activity?.showMessage(it.message, it.title)
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            mChangeEcListener?.onLogout()
        }
    }

    override fun showMerchants(merchants: ArrayList<Merchant>) {
        if (isAttached()) {
            val merchantList = merchants.toMutableList()

            val intent = activity?.intent
            intent?.getStringExtra(IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER)?.let { ecNumber ->
                val newMerchantIndex = merchantList.indexOfFirst { it.id == ecNumber }
                if (newMerchantIndex != -1) {
                    val newMerchant = merchantList[newMerchantIndex]
                    merchantList.removeAt(newMerchantIndex)
                    merchantList.add(0, newMerchant)

                    intent.removeExtra(IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER)
                }
            }

            recycler_view.layoutManager = (androidx.recyclerview.widget.LinearLayoutManager(context))
            mAdapter = ChangeEcMerchantAdapter(
                ArrayList(merchantList),
                chooserMarchent = {
                    currentMerchant = it
                    useSecurityHash?.let { useSecurityHash ->
                        if (useSecurityHash) {
                            allowMePresenter.collect(
                                mAllowMeContextual = mAllowMeContextual,
                                requireActivity(),
                                mandatory = false
                            )
                        } else {
                            gaSendFormInterection(it.tradingName)
                            mPresenter.callChangeEc(it, UserPreferences.getInstance().token,
                                isLogar = false, isMerchantNode = true, callChildScreen = true,
                                EMPTY)
                        }
                    }
                },
                findMoreItens = null,
                isChangeEcChild = false,
                analytics = analytics
            )
            recycler_view.adapter = mAdapter
        }
    }

    override fun callChildrenScreen(merchant: Merchant) {
        mChangeEcListener?.onMerchant(merchant)
        mChangeEcListener?.onNextStep(false)
    }

    override fun onChangetoNewEc(merchant: Merchant, impersonate: Impersonate) {
        mChangeEcListener?.onChangeNewEc(impersonate, merchant)
    }

    override fun showLoading() {
        super.showLoading()
        if (isAttached()) {
            mChangeEcListener?.showLoading()
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            mChangeEcListener?.hideLoading()
        }
    }

    override fun onPause() {
        super.onPause()
        mfaRouteHandler.onPause()
    }

    private fun gaSendFormInterection(
            label: String,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, HOME_LOGADA),
            action = listOf(HOME_LOGADA_ESTABELECIMENTOS),
            label = listOf(Action.INTERACAO, label)
        )
    }

    companion object {
        fun create(changeEcListener: ChangeEcListener): ChangeEcMerchantFragment {
            return ChangeEcMerchantFragment().apply {
                this.mChangeEcListener = changeEcListener
            }
        }
    }

    override fun successCollectToken(result: String) {
        proceedChange(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlert(errorMessage)
        } else {
            FirebaseCrashlytics.getInstance().log(errorMessage)
            proceedChange(result)
        }
    }

    private fun proceedChange(result: String?) {
        gaSendFormInterection(currentMerchant.tradingName)
        result?.let {
            mPresenter.callChangeEc(
                currentMerchant, UserPreferences.getInstance().token,
                isLogar = false, isMerchantNode = true, callChildScreen = true, it
            )
        }
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(message)
            .closeTextButton(getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                requireActivity().supportFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }
}