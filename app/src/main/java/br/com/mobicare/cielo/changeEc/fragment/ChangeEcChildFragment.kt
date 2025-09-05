package br.com.mobicare.cielo.changeEc.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.changeEc.ChangeEcMerchantAdapter
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.activity.ChangeEcListener
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.changeEc.presenter.ChangeEcMerchantPresenter
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EndlessScrollListener
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_change_ec.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ChangeEcChildFragment : BaseFragment(), ChanceEcFragmentContract.View, AllowMeContract.View {

    private val mRepository: ChangeEcRepository by inject()
    private var mAdapter: ChangeEcMerchantAdapter? = null
    private var mMerchants: MerchantsObj = MerchantsObj()
    private var merchant: Merchant? = null
    private lateinit var mPresenter: ChangeEcMerchantPresenter
    private var mChangeEcListener: ChangeEcListener? = null
    private var scrollListener: EndlessScrollListener? = null
    private val analytics: HomeGA4 by inject()
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(
            requireContext()
        )
    }
    private var currentSearch = ""
    private lateinit var mAllowMeContextual: AllowMeContextual
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }

    private var isLogar = true
    private var isMerchantNode = false

    companion object {
        const val MERCHANT_EXTRA = "MERCHANT_EXTRA"
        private const val FILTER_DEBOUNCE_DELAY = 500L

        fun create(changeEcListener: ChangeEcListener, merchant: Merchant?) =
            ChangeEcChildFragment().apply {
                mChangeEcListener = changeEcListener
                val extras = Bundle()
                extras.putParcelable(MERCHANT_EXTRA, merchant)
                arguments = extras
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_ec, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mChangeEcListener?.onButtonLeftVisible(true)
        mAllowMeContextual = allowMePresenter.init(requireContext())
        configViews()
        configPresenter()
        configEditSearch()
        configListeners()

        arguments?.let {
            merchant = it.getParcelable(MERCHANT_EXTRA) as Merchant?
            merchant?.let {
                isLogar = false
                isMerchantNode = true
                useSecurityHash?.let { useSecurityHash ->
                    if (useSecurityHash) allowMePresenter.collect(
                        mAllowMeContextual = mAllowMeContextual,
                        requireActivity(),
                        mandatory = false
                    )
                    else mPresenter.callChangeEc(
                        it,
                        UserPreferences.getInstance().token,
                        isLogar = isLogar,
                        isMerchantNode = isMerchantNode,
                        callChildScreen = false,
                        EMPTY
                    )
                }
            }
        }
    }

    private fun configViews() {
        this.backButton?.visibility = View.VISIBLE
    }

    private fun configPresenter() {
        mPresenter = ChangeEcMerchantPresenter(mRepository)
        mPresenter.setView(this)

        scrollListener = object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mPresenter.getChildrens()
            }

        }

        mAdapter = ChangeEcMerchantAdapter(mMerchants.merchants, chooserMarchent = {
            isLogar = true
            isMerchantNode = false
            merchant = it
            gaSendFormInterection(it.tradingName)
            useSecurityHash?.let { useSecurityHash ->
                if (useSecurityHash) {
                    allowMePresenter.collect(
                        mAllowMeContextual = mAllowMeContextual,
                        requireActivity(),
                        mandatory = false
                    )
                } else {
                    mPresenter.callChangeEc(
                        it,
                        UserPreferences.getInstance().token,
                        isLogar = isLogar,
                        isMerchantNode = isMerchantNode,
                        callChildScreen = false,
                        EMPTY
                    )
                }
            }
        }, findMoreItens = {
            mPresenter.getChildrens()
        },
            isChangeEcChild = true,
            analytics = analytics
        )
        recycler_view.layoutManager = linearLayoutManager
        scrollListener?.let { recycler_view.addOnScrollListener(it) }
        recycler_view.adapter = mAdapter
    }

    private fun configEditSearch() {
        val handler = Handler()
        edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val myRunnable = Runnable() {
                    edit_search.removeTextChangedListener(this)
                    s?.let {
                        currentSearch = it.toString()
                        mAdapter?.filter?.filter(it)
                    }
                    edit_search.addTextChangedListener(this)
                }
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(myRunnable, FILTER_DEBOUNCE_DELAY)
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

    private fun configListeners() {
        this.backButton?.setOnClickListener {
            this.mChangeEcListener?.onBackStep()
        }
    }

    override fun showChildren(merchants: MerchantsObj) {
        mMerchants.token = merchants.token
        mMerchants.merchants.addAll(merchants.merchants)
        mAdapter?.notifyDataSetChanged()
        if (currentSearch.isNotBlank()) mAdapter?.filter?.filter(currentSearch)
    }


    override fun onChangetoNewEc(merchant: Merchant, impersonate: Impersonate) {
        mChangeEcListener?.onChangeNewEc(impersonate, merchant)
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

    private fun gaSendFormInterection(
        label: String,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, HOME_LOGADA),
            action = listOf(HOME_LOGADA_ESTABELECIMENTOS),
            label = listOf(Action.INTERACAO, label)
        )
    }

    override fun showLoadingMore() {
        super.showLoadingMore()
        progress_child.visible()
    }

    override fun hideLoadingMore() {
        super.hideLoadingMore()
        progress_child?.gone()
    }

    override fun successCollectToken(result: String) {
        proceedChange(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory)
            showAlert(errorMessage)
        else {
            FirebaseCrashlytics.getInstance().log(errorMessage)
            proceedChange(result)
        }
    }

    private fun proceedChange(result: String?) {
        merchant?.let { itMerchant ->
            result?.let { itResult ->
                mPresenter.callChangeEc(
                    itMerchant,
                    UserPreferences.getInstance().token,
                    isLogar = isLogar,
                    isMerchantNode = isMerchantNode,
                    callChildScreen = false,
                    itResult
                )
            }
        }
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment.Builder().title(getString(R.string.dialog_title)).message(message)
            .closeTextButton(getString(R.string.dialog_button)).build().showAllowingStateLoss(
                requireActivity().supportFragmentManager, getString(R.string.text_cieloalertdialog)
            )
    }
}