package br.com.mobicare.cielo.adicaoEc.presentation.ui

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccount
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.adicaoEc.domain.model.ParamsEc
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcContract
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcPresenter
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.spannable.SpannableLink
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.component.selectBottomSheet.SelectBottomSheet
import br.com.mobicare.cielo.component.selectBottomSheet.SelectBottomSheetEnum
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.databinding.ActivityAddEcBinding
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection.HTTP_FORBIDDEN

const val SHOW_IMPERSONATE_EC_BOTTOMSHEET = "SHOW_IMPERSONATE_EC_BOTTOMSHEET"
const val IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER = "IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER"

class AddEcActivity : BaseLoggedActivity(), AddEcContract.View {

    private val mPresenter: AddEcPresenter by inject { parametersOf(this) }
    private val analytics: HomeGA4 by inject()

    private lateinit var mProfileType: String
    private lateinit var mParams: ParamsEc
    private var mEcNumber: String? = null
    private var mBank: BankMaskVO? = null
    var screenPath: String = EMPTY

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(supportFragmentManager)
    }

    private val binding: ActivityAddEcBinding by lazy { ActivityAddEcBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbarInclude.toolbarMain, getString(R.string.add_ec_toolbar_title))

        if (intent != null && intent.extras != null) {
            screenPath = intent.getStringExtra(ScreenView.SCREEN_NAME) ?: EMPTY
        }

        mProfileType = getString(R.string.name_tipo_de_perfil_pf)
        mParams = ParamsEc(
            getString(R.string.name_tipo_de_perfil_pj),
            getString(R.string.name_tipo_de_perfil_pf),
            getString(R.string.conta_corrente),
            getString(R.string.conta_poupanca),
            getString(R.string.conta_simples),
            getString(R.string.conta_entidade_publicas)
        )

        mPresenter.getBankList()
        mPresenter.fetchAccountTypes(profileType = mProfileType, params = mParams)

        setListeners()

        binding.apply {
            textLinkTerms.setText(formatTextOfTerms(), TextView.BufferType.SPANNABLE)
            textLinkTerms.movementMethod = LinkMovementMethod.getInstance()
        }
        analytics.logHomeAddEcScreenView(screenPath, this.javaClass)
    }

    private fun setListeners() {
        binding.apply {
            confirmButton.setOnClickListener {
                this@AddEcActivity.hideSoftKeyboard()
                showLoading()
                createObjectToSubmitData()
                analytics.logHomeAddEcClick(screenPath, this.javaClass)
            }
            checkBoxConfirmTerms.setOnCheckedChangeListener { _, isChecked ->
                confirmButton.isEnabled = isChecked && areFieldsFilledIn()
            }
            includeProfileChoose.radioButtonPessoaFisica.setOnClickListener {
                prepareToFetchAccountTypes(getString(R.string.name_tipo_de_perfil_pf))
            }
            includeProfileChoose.radioButtonPessoaJuridica.setOnClickListener {
                prepareToFetchAccountTypes(getString(R.string.name_tipo_de_perfil_pj))
            }


            val buttonConfirmListener = object : CieloTextInputView.TextChangeListener {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    confirmButton.isEnabled = areFieldsFilledIn()
                }
            }

            ecNumber.setOnTextChangeListener(buttonConfirmListener)
            includeViewAccountData.inputBankAgency.setOnTextChangeListener(buttonConfirmListener)
            includeViewAccountData.accountNumber.setOnTextChangeListener(buttonConfirmListener)
            includeViewAccountData.accountDigit.setOnTextChangeListener(buttonConfirmListener)
            includeViewAccountData.inputAccountType.setOnTextChangeListener(buttonConfirmListener)
            includeViewAccountData.inputBankName.setOnTextChangeListener(buttonConfirmListener)

        }


    }

    override fun showLoading() {
        binding.apply {
            progressBar.visible()
            toolbarInclude.root.gone()
            scrollViewMainContent.gone()
            confirmButton.gone()
        }
    }

    private fun createObjectToSubmitData() {

        binding.apply {
            mEcNumber = ecNumber.getText()
            val bankAgency = includeViewAccountData.inputBankAgency.getText()
            val accountNumber = includeViewAccountData.accountNumber.getText()
            val accountDigit = includeViewAccountData.accountDigit.getText()
            val accountType = includeViewAccountData.inputAccountType.getText()

            mBank?.let { itBank ->
                mEcNumber?.let { itEcNumber ->
                    val objEcPrepared = mPresenter.prepareObjectToSubmit(
                        BankAccountObj(
                            itEcNumber,
                            BankAccount(
                                accountNumber + accountDigit,
                                accountType,
                                bankAgency,
                                itBank.code
                            )
                        ), mProfileType, mParams
                    )

                    validationTokenWrapper.generateOtp(
                        onResult = { otpCode ->
                            mPresenter.addNewEc(objEcPrepared, otpCode)
                        }
                    )
                }
            }
        }
    }

    private fun prepareToFetchAccountTypes(profileType: String) {
        mProfileType = profileType

        mBank?.let { itBank ->
            binding.includeViewAccountData.inputAccountType.setText("")
            mPresenter.fetchAccountTypes(itBank.code, mProfileType, mParams)
        }
    }

    private fun areFieldsFilledIn(): Boolean {
        return with(binding) {
            ecNumber.getText().trim().isNotEmpty() && includeViewAccountData.inputBankName.getText().trim()
                .isNotEmpty() &&
                    includeViewAccountData.inputBankAgency.getText().trim().isNotEmpty() && includeViewAccountData.accountNumber.getText().trim()
                .isNotEmpty() &&
                    includeViewAccountData.accountDigit.getText().trim().isNotEmpty() && includeViewAccountData.inputAccountType.getText().trim()
                .isNotEmpty() &&
                    checkBoxConfirmTerms.isChecked
        }
    }

    override fun prepareBottomSheetBankList(bankListResponse: ArrayList<SelectItem<BankMaskVO>>) {
        if (isAttached()) {
            val bottomSheet = SelectBottomSheet
                .Builder<BankMaskVO>()
                .title(getString(R.string.list_of_available_banks))
                .hintSearchBar(getString(R.string.search_by_bank_name))
                .isShowSearchBar(true)
                .isShowSearchIcon(true)
                .height(SelectBottomSheetEnum.MEDIUM)
                .list(bankListResponse)
                .listener(object : SelectBottomSheet.OnItemListener {
                    override fun onItemSelected(item: Any) {
                        onBankSelected(item as BankMaskVO)
                    }
                }).build()

            binding.includeViewAccountData.viewBankAgency.setOnClickListener {
                bottomSheet.showBottomSheet(this@AddEcActivity.supportFragmentManager)
            }
        }
    }

    private fun formatTextOfTerms(): SpannableStringBuilder {
        return getString(R.string.terms_text_1)
            .addSpannable(TextAppearanceSpan(this, R.style.TextGrayLight13sp700_Normal)).append(" ")
            .append(
                getString(R.string.terms_text_2)
                    .addSpannable(
                        TextAppearanceSpan(
                            this,
                            R.style.Paragraph_300_Regular_14_Display_600
                        ),
                        SpannableLink({
                            showTerms()
                        })
                    )
            ).append(" ")
            .append(
                getString(R.string.terms_text_3)
                    .addSpannable(TextAppearanceSpan(this, R.style.TextGrayLight13sp700_Normal))
            )
    }

    private fun onBankSelected(bank: BankMaskVO) {
        this.mBank = bank

        if (mProfileType.isNullOrEmpty()) mProfileType =
            getString(R.string.name_tipo_de_perfil_pf)

        binding.includeViewAccountData.inputBankName.setText(bank.name)

        mBank?.let { mPresenter.fetchAccountTypes(it.code, mProfileType, mParams) }
    }

    private fun showTerms() =
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(BuildConfig.CONDITION_TERMS_URL)))

    override fun prepareBottomSheetAccountTypeList(accountTypes: ArrayList<SelectItem<BankMaskVO>>) {
        if (isAttached()) {
            val bottomSheet = SelectBottomSheet
                .Builder<BankMaskVO>()
                .title(getString(R.string.account_type))
                .list(accountTypes)
                .height(SelectBottomSheetEnum.SMALL)
                .listener(object : SelectBottomSheet.OnItemListener {
                    override fun onItemSelected(item: Any) {
                        binding.includeViewAccountData.inputAccountType.setText(item as String)
                    }
                }).build()

            binding.includeViewAccountData.viewAccountType.setOnClickListener {
                bottomSheet.showBottomSheet(this@AddEcActivity.supportFragmentManager)
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (error?.code != HTTP_FORBIDDEN.toString()) {
            bottomSheetGenericFlui(
                "",
                R.drawable.ic_generic_error_image,
                getString(R.string.text_title_generic_error),
                error?.message ?: getString(R.string.text_message_generic_error),
                "",
                getString(R.string.ok),
                statusTitle = true,
                statusSubTitle = true,
                statusImage = true,
                statusBtnFirst = true,
                statusBtnSecond = true,
                statusView1Line = true,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
                isFullScreen = true
            ).apply {
                onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        goToHome(false)
                        finish()
                    }

                    override fun onSwipeClosed() {
                        goToHome(false)
                        finish()
                    }

                    override fun onCancel() {
                        goToHome(false)
                        finish()
                    }
                }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    override fun showBottomSheetSuccess() {
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_08,
            getString(R.string.add_establishment_success_title),
            getString(R.string.add_establishment_success_text),
            "",
            getString(R.string.add_establishment_success_button),
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    analytics.logHomeAddEcSuccess(screenPath, this.javaClass)
                    goToHome(true)
                    finish()
                }

                override fun onSwipeClosed() {
                    goToHome(true)
                    finish()
                }

                override fun onCancel() {
                    goToHome(true)
                    finish()
                }
            }
        }.show(
            supportFragmentManager,
            getString(R.string.bottom_sheet_generic)
        )
    }

    private fun goToHome(showBottomSheetEC: Boolean = false) {
        backToHome(
            Pair(SHOW_IMPERSONATE_EC_BOTTOMSHEET, showBottomSheetEC),
            Pair(IMPERSONATE_EC_BOTTOMSHEET_ECNUMBER, mEcNumber)
        )
    }
}