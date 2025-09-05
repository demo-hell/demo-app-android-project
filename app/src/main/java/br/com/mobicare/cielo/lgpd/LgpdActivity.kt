package br.com.mobicare.cielo.lgpd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.cielo.libflue.horizontal.CieloHorizontalRegularTextCheckbox
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LGPD_ELEGIBLE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LGPD_START_BY_LOGIN
import br.com.mobicare.cielo.commons.constants.LgpdLinks
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.main.presentation.ui.activities.PASSWORD_EXTRA_PARAM
import kotlinx.android.synthetic.main.activity_lgpd.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LgpdActivity : BaseLoggedActivity(), LgpdContract.View {

    val presenter: LgpdPresenter by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lgpd)
        loadParams()
        configureToolbar()
        configureViews()
        configureListeners()
    }

    override fun onBackPressed() {
        this.presenter.onBackButtonClicked()
    }

    private fun configureToolbar() {
        setupToolbar(this.toolbar as Toolbar, getString(R.string.text_lgpd_title_toolbar))
    }

    private fun configureViews() {
        this.websiteTermsAndConditionsButton?.showDivider(false)
    }

    private fun loadParams() {
        this.intent.extras?.let { itExtras ->
            val password = itExtras.getByteArray(PASSWORD_EXTRA_PARAM)
            val isStartByLogin = itExtras.getBoolean(ARG_PARAM_LGPD_START_BY_LOGIN, false)
            itExtras.getParcelable<LgpdElegibilityEntity>(ARG_PARAM_LGPD_ELEGIBLE)
                ?.let { itEntity ->
                    this.presenter.loadElegibility(itEntity, password, isStartByLogin)
                }

        }
    }

    override fun render(state: LgpdPresenter.State) {
        when (state) {
            is LgpdPresenter.State.Loading -> setStateloading()
            is LgpdPresenter.State.Error -> setStateError(state.errorMessage)
            is LgpdPresenter.State.EnableButton -> setEnableButton(true)
            is LgpdPresenter.State.DisableButton -> setEnableButton(false)
            else -> renderState(state)
        }
    }

    override fun showMainWindow(password: ByteArray?, isStartByLogin: Boolean) {
        if (isStartByLogin) {
            startActivity<MainBottomNavigationActivity>(PASSWORD_EXTRA_PARAM to password)
        }
        this.finish()
    }

    private fun setStateloading() {
        this.contentLayout?.gone()
        this.errorView?.gone()
        this.progressView?.visible()
    }

    private fun setStateError(error: ErrorMessage?) {
        error?.let {
            this.contentLayout?.gone()
            this.progressView?.gone()
            this.errorView?.visible()
        }
    }

    private fun renderState(state: LgpdPresenter.State) {
        this.privatePolicyButton?.visible()
        this.prepaidIssueButton?.visible(state is LgpdPresenter.State.OwnerLoggedWithDigitalAccount)
        this.cieloAccreditationAgreementButton?.visible()
        this.websiteTermsAndConditionsButton?.visible()
        this.ownerCheckbox?.visible(state is LgpdPresenter.State.OwnerLoggedWithBankingDomicile
                || state is LgpdPresenter.State.OwnerLoggedWithDigitalAccount)
        this.agreeCheckBox?.visible()
    }

    private fun setEnableButton(isEnabled: Boolean) {
        this.agreeButton?.isEnabled = isEnabled
    }

    private fun configureListeners() {
        this.privatePolicyButton?.setOnClickListener {
            openLink(LgpdLinks.PrivatePolicyLink)
        }
        this.prepaidIssueButton?.setOnClickListener {
            openLink(LgpdLinks.PrepaidIssueLink)
        }
        this.cieloAccreditationAgreementButton?.setOnClickListener {
            openLink(LgpdLinks.CieloAccreditationAgreementLink)
        }
        this.websiteTermsAndConditionsButton?.setOnClickListener {
            openLink(LgpdLinks.WebsiteTermsAndConditions)
        }
        this.ownerCheckbox?.setOnCheckedChangeListener(object: CieloHorizontalRegularTextCheckbox.OnCheckedChangeListener {
            override fun onCheckedChanged(
                view: CieloHorizontalRegularTextCheckbox?,
                isChecked: Boolean
            ) {
                this@LgpdActivity.presenter.onOnwerClicked(isChecked)
            }
        })
        this.agreeCheckBox?.setOnCheckedChangeListener(object: CieloHorizontalRegularTextCheckbox.OnCheckedChangeListener {
            override fun onCheckedChanged(
                view: CieloHorizontalRegularTextCheckbox?,
                isChecked: Boolean
            ) {
                this@LgpdActivity.presenter.onAgreeClicked(isChecked)
            }
        })
        this.agreeButton?.setOnClickListener {
            this.presenter.onAgreeButtonClicked()
        }
        this.errorView?.configureActionClickListener {
            this.presenter.onAgreeButtonClicked()
        }
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

}