package br.com.mobicare.cielo.internaluser

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.changeEc.ChangeEc
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.EndlessScrollListener
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.internaluser.adapter.ChangeEcMerchantInternalUserAdapter
import br.com.mobicare.cielo.internaluser.presenter.InternalUserPresenterImpl
import br.com.mobicare.cielo.main.presentation.ui.activities.CAME_FROM_INTERNAL_USER_SCREEN
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.main.presentation.ui.activities.PASSWORD_EXTRA_PARAM
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_internal_user.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val INTERNAL_USER_RESULT_CODE = 9121

class InternalUserActivity : BaseLoggedActivity(), InternalUserView, AllowMeContract.View {

    private var adapter: ChangeEcMerchantInternalUserAdapter? = null
    private var currentSearch = ""
    private var scrollListener: EndlessScrollListener? = null
    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }
    private var merchantObj: MerchantsObj = MerchantsObj()
    private val presenter: InternalUserPresenterImpl by inject { parametersOf(this) }
    private var page = 0
    private  var fingerPrint = ""
    private var password: ByteArray? = null
    private lateinit var mAllowMeContextual: AllowMeContextual
    private val analytics: HomeGA4 by inject()
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_user)
        mAllowMeContextual = allowMePresenter.init(this)
        init()
        configViews()
    }

    private fun init() {
        intent.extras?.let {
            password = it.getByteArray(PASSWORD_EXTRA_PARAM)
        }

        scrollListener = object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                this@InternalUserActivity.page = page + 1
                presenter.searchChild(this@InternalUserActivity.page, null)
            }

        }
        recycler_view.layoutManager = linearLayoutManager
        adapter = ChangeEcMerchantInternalUserAdapter(
            merchantObj.merchants,
            chooserMarchent = {
                presenter.callChangeEc(it, this.merchantObj.token, fingerPrint)
            },
            analytics
        )
        useSecurityHash?.let { useSecurityHash ->
            if (useSecurityHash) {
                allowMePresenter.collect(
                    mAllowMeContextual = mAllowMeContextual,
                    this,
                    mandatory = false
                )
            } else {
                adapter = ChangeEcMerchantInternalUserAdapter(
                    merchantObj.merchants,
                    chooserMarchent = {
                        presenter.callChangeEc(it, this.merchantObj.token, EMPTY)
                    },
                    analytics
                )
            }
        }

        scrollListener?.let { recycler_view.addOnScrollListener(it) }
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler_view.adapter = adapter
    }

    private fun configViews() {
        analytics.logLoginInternalUserScreenView(this.javaClass)
        textViewButtonSearch.setOnClickListener {
            resetList()
            hideSoftKeyboard()
            presenter.searchChild(page, currentSearch)
        }
        enableFindButton(false)

        edit_search.addTextChangedListener(onTextChanged = { text, start, before, count ->
            text?.let {
                currentSearch = it.toString()
                enableFindButton(currentSearch.length >= 3)
            }
        })
    }

    override fun showChildren(merchants: MerchantsObj) {
        this.merchantObj.token = merchants.token
        this.merchantObj.merchants.addAll(merchants.merchants)
        adapter?.notifyDataSetChanged()
    }

    override fun showLoading() {
        progressBarLoading.visible()
        recycler_view.gone()
    }

    override fun showLoadingMore() {
        progressBarLoadingMore.visible()
    }

    override fun hideLoading() {
        progressBarLoading.gone()
        recycler_view.visible()
    }

    override fun hideLoadingMore() {
        progressBarLoadingMore.gone()
    }

    override fun showError(error: ErrorMessage) {

        val message = getString(R.string.text_search_stablishment_error)
        analytics.logLoginInternalUserExcepiton(error.code, error.errorMessage)
        progressBarLoading.gone()
        progressBarLoadingMore.gone()
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.text_fullsec_error_title))
            .message(getString(R.string.text_search_stablishment_error))
            .closeTextButton("OK")
            .build().let {
                it.showAllowingStateLoss(this.supportFragmentManager, "CieloAlertDialog")
            }
    }

    override fun onChangetoNewEc(merchant: Merchant, impersonate: Impersonate) {
        val changeEc = ChangeEc()
        changeEc.createNewLoginConvivencia(impersonate, merchant)
        try {
            val intentMain = Intent(this, MainBottomNavigationActivity::class.java)
            intentMain.putExtra(
                PASSWORD_EXTRA_PARAM,
                intent.getByteArrayExtra(PASSWORD_EXTRA_PARAM)
            )
            intentMain.putExtra(CAME_FROM_INTERNAL_USER_SCREEN, true)
            startActivity(intentMain)
            finish()
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    override fun showEmptyListError(visibility: Int, background: Int) {
        edit_search.background = ContextCompat.getDrawable(this, background)
        textViewEmptyList.visibility = visibility
    }

    private fun resetList() {
        page = 1
        this.merchantObj.merchants.clear()
        recycler_view.layoutManager?.scrollToPosition(0)
        adapter?.notifyDataSetChanged()
        scrollListener?.resetState()
    }

    private fun enableFindButton(enable: Boolean) {
        textViewButtonSearch.isClickable = enable
        if (enable) {
            textViewButtonSearch.setTextColor(ContextCompat.getColor(this, R.color.brand_400))
        } else {
            textViewButtonSearch.setTextColor(ContextCompat.getColor(this, R.color.display_300))
        }
    }

    override fun successCollectToken(result: String) {
            fingerPrint = result
            proceedChange(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlert(errorMessage)
        } else {
            proceedChange(result)
        }
    }

    private fun proceedChange(result: String?) {
        adapter = ChangeEcMerchantInternalUserAdapter(
            merchantObj.merchants,
            chooserMarchent = {
                result?.let { result -> presenter.callChangeEc(it, this.merchantObj.token, result) }
            },
            analytics
        )
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return this.supportFragmentManager
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(message)
            .closeTextButton(getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                this.supportFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }


}