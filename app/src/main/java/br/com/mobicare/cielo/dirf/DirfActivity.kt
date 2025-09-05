package br.com.mobicare.cielo.dirf

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.Toolbar
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.FILE_EXTENSION
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.dirf.analytics.DirfAnalytics
import br.com.mobicare.cielo.dirf.analytics.DirfAnalytics.Companion.DEC_FORMAT
import br.com.mobicare.cielo.dirf.analytics.DirfAnalytics.Companion.EXCEL_FORMAT
import br.com.mobicare.cielo.dirf.analytics.DirfAnalytics.Companion.PDF_FORMAT
import br.com.mobicare.cielo.dirf.analytics.DirfGA4
import br.com.mobicare.cielo.dirf.analytics.DirfGA4.Companion.SCREEN_VIEW_DIRF
import br.com.mobicare.cielo.dirf.ui.DirfResultActivity
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.helpers.displayedChild
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import br.com.mobicare.cielo.review.presentation.GooglePlayReviewViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import kotlinx.android.synthetic.main.component_sanfona_dirf.iv_setinha_down
import kotlinx.android.synthetic.main.component_sanfona_dirf.layout_item_dirf
import kotlinx.android.synthetic.main.component_sanfona_dirf.layout_setinha
import kotlinx.android.synthetic.main.dirf_activity.btnDirf
import kotlinx.android.synthetic.main.dirf_activity.errorHandlerDirf
import kotlinx.android.synthetic.main.dirf_activity.errorLayoutDirf
import kotlinx.android.synthetic.main.dirf_activity.errorToggleDirf
import kotlinx.android.synthetic.main.dirf_activity.layout_info_dec
import kotlinx.android.synthetic.main.dirf_activity.layout_year
import kotlinx.android.synthetic.main.dirf_activity.link_access_contact
import kotlinx.android.synthetic.main.dirf_activity.tv_dirf_rodape
import kotlinx.android.synthetic.main.dirf_activity.tv_year_value
import kotlinx.android.synthetic.main.dirf_activity.vf_dirf
import kotlinx.android.synthetic.main.item_file_dirf.radioGroupDirf
import kotlinx.android.synthetic.main.layout_info_dec.ev_dirf_cnpj
import kotlinx.android.synthetic.main.layout_info_dec.ev_dirf_cpf
import kotlinx.android.synthetic.main.layout_info_dec.ev_nome_ra_social
import kotlinx.android.synthetic.main.layout_info_dec.tv_name_resposable
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.Calendar

class DirfActivity : BaseLoggedActivity(), DirfContract.View {

    private val presenter: DirfPresenter by inject {
        parametersOf(this)
    }
    private val analytics: DirfAnalytics by inject()
    private val ga4: DirfGA4 by inject()

    var meResponse: MCMerchantResponse? = null

    var bt: BottomSheetFluiGenericFragment? = null
    var isClickSetinhaDownAuthorization = false
    var type: String? = null
    var extension: String? = null
    var year: Int? = null
    private val viewModel: GooglePlayReviewViewModel by inject()

    companion object {
        const val PDF = "PDF"
        const val EXCEL = "Excel"
        const val DEC = ".DEC"

        private const val FILE_NAME = "DIRF"
        private const val FILE_NAME_NEW = "DIRF/EFD-REINF"
        private const val BAIXAR_ARQUIVO = "baixar_arquivo"
        private const val EXTENSION_PDF = "pdf"
        private const val EXTENSION_EXCEL = "xlsx"
        private const val EXTENSION_DEC = "dec"

        private const val DOT_EXTENSION_PDF = ".$EXTENSION_PDF"
        private const val DOT_EXTENSION_EXCEL = ".$EXTENSION_EXCEL"
        private const val DOT_EXTENSION_DEC = ".$EXTENSION_DEC"

        private const val FILE_PATH = "Cielo gestao/DIRF/"

        private const val DIRF_HELP_ID = "TAG_HELP_CENTER_DIRF"

        private const val JANUARY_MONTH_INDEX = 0
        private const val YEAR_COUNT_LIMIT = 5

        private const val LOADING_VIEW_INDEX = 0
        private const val DIRF_OPTIONS_VIEW_INDEX = 1
        private const val FT_ERROR_VIEW_INDEX = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dirf_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_sub_menu_Dirf))

        if (FeatureTogglePreference.instance.isActivate(FeatureTogglePreference.DIRF)) {
            displayedChild(LOADING_VIEW_INDEX, vf_dirf)
            presenter.initView()
            presenter.callApi()
        } else {
            displayedChild(FT_ERROR_VIEW_INDEX, vf_dirf)
        }
    }

    override fun initView() {
        btnDisable()
        layout_year?.setOnClickListener {

            val pickerBS = PickerBottomSheetFragment.newInstance(
                getString(R.string.txt_name_calendar),
                getList(), lockCollapse = true
            ).apply {
                this.onItemSelectedListener = object :
                    PickerBottomSheetFragment.OnItemSelectedListener {

                    override fun onSelected(selectedItem: Int) {
                        val selectedInstallment = getList()?.get(selectedItem)
                        selectedInstallment?.run {
                            val value = this
                            value?.let {
                                presenter!!.sendDate(it)
                            }

                        }
                    }
                }
            }
            pickerBS.show(
                supportFragmentManager,
                "pickerBS"
            )

        }

        layout_setinha?.setOnClickListener {
            if (isClickSetinhaDownAuthorization == false) {
                listOpenAuthorization()
            } else {
                listCloseAuthorization()
            }
        }

        link_access_contact?.setOnClickListener {
            browse("https://www.gov.br/receitafederal/pt-br")
        }


        radioGroupDirf.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<AppCompatRadioButton>(checkedId)
            if (radioButton?.isChecked == true) {
                if (radioButton?.text.toString().equals(DEC)) {
                    layout_info_dec?.visibility = View.VISIBLE
                    this.meResponse?.let {
                        tv_name_resposable?.setText(
                            if (it.owners.isNullOrEmpty()
                                    .not()
                            ) it.owners?.get(ZERO)?.name.toString() else EMPTY
                        )
                        ev_nome_ra_social?.setText(it.companyName)
                        ev_dirf_cpf?.setText(
                            addMaskCPForCNPJ(
                                if (it.owners.isNullOrEmpty()
                                        .not()
                                ) it.owners?.get(ZERO)?.cpf.toString() else EMPTY,
                                getString(R.string.mask_cpf_step4)
                            )
                        )
                        it.cnpj?.let { doc ->
                            ev_dirf_cnpj?.setText(
                                addMaskCPForCNPJ(
                                    doc,
                                    getString(R.string.mask_cnpj_step4)
                                )
                            )
                        }

                    }


                } else {
                    layout_info_dec?.visibility = View.GONE
                }

                if (year != null) {
                    btnEnable()
                    type = radioButton?.text?.toString()
                    textWatchNameResposable()
                    textWatchCpf()
                    textWatchCnpj()
                    textWatchRaSocial()

                } else {
                    type = radioButton?.text?.toString()
                    btnDisable()
                }
            }
        }

        tv_dirf_rodape?.text = configureSubtitle()

        tv_dirf_rodape?.setOnClickListener {
            startActivity<CentralAjudaSubCategoriasEngineActivity>(
                ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_DIRF,
                ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.title_sub_menu_Dirf),
                CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
            )
        }


        btnDirf.setOnClickListener {
            displayedChild(LOADING_VIEW_INDEX, vf_dirf)

            year?.let { itYear ->
                when (type) {
                    DEC -> {
                        presenter.callDirf(
                            itYear,
                            Utils.unmask(ev_dirf_cnpj?.getText() ?: EMPTY),
                            ev_nome_ra_social?.getText() ?: EMPTY,
                            tv_name_resposable?.getText() ?: EMPTY,
                            Utils.unmask(ev_dirf_cpf?.getText() ?: EMPTY),
                            EXTENSION_DEC,
                            DEC
                        )
                        this.extension = EXTENSION_DEC

                        analytics.logSelectedDocument(
                            selectedYear = itYear.toString(),
                            formatType = DEC_FORMAT
                        )
                    }

                    PDF -> {
                        presenter.callDirfPDFOrExcel(
                            itYear,
                            type.let { it }.toString(),
                            PDF
                        )
                        this.extension = EXTENSION_PDF

                        analytics.logSelectedDocument(
                            selectedYear = itYear.toString(),
                            formatType = PDF_FORMAT
                        )
                    }

                    EXCEL -> {
                        this.meResponse?.let {
                            presenter.run {
                                callDirf(
                                    itYear,
                                    it.cnpj?.trim() ?: EMPTY,
                                    it.companyName.trim(),
                                    if (it.owners.isNullOrEmpty()
                                            .not()
                                    ) it.owners?.get(ZERO)?.name.toString() else EMPTY,
                                    if (it.owners.isNullOrEmpty()
                                            .not()
                                    ) it.owners?.get(ZERO)?.cpf.toString() else EMPTY,
                                    EXTENSION_EXCEL,
                                    EXCEL
                                )
                            }

                            analytics.logSelectedDocument(
                                selectedYear = itYear.toString(),
                                formatType = EXCEL_FORMAT
                            )
                        }
                        this.extension = EXTENSION_EXCEL
                    }
                }
            }
        }

        errorLayoutDirf?.configureActionClickListener(View.OnClickListener {
            finish()
        })

        errorHandlerDirf?.configureActionClickListener(View.OnClickListener {
            finish()
        })

        errorToggleDirf?.configureActionClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun returnDate(value: String) {
        tv_year_value?.text = "$value"
        year = value.toInt()

        if (year != null && type != null) {
            btnEnable()
        } else {
            btnDisable()
        }
    }

    private fun getDateInfo(): Pair<Int, Int> {
        return Pair(
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.YEAR)
        )
    }

    private fun getList(): ArrayList<String> {
        val list = ArrayList<String>()
        val dateInfo = getDateInfo()
        val startIndex = if (dateInfo.first > JANUARY_MONTH_INDEX) {
            ZERO
        } else {
            ONE
        }
        val currentYear = dateInfo.second

        for (i in startIndex..YEAR_COUNT_LIMIT) {
            list.add((currentYear - i).toString())
        }
        return list
    }

    private fun listCloseAuthorization() {
        layout_item_dirf.visibility = View.GONE
        isClickSetinhaDownAuthorization = false
        iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_down)
    }

    private fun listOpenAuthorization() {
        layout_item_dirf.visibility = View.VISIBLE
        isClickSetinhaDownAuthorization = true
        iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_up)
    }

    private fun configureSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append(
            getString(R.string.text_body_dirf_01)
                .addSpannable(
                    TextAppearanceSpan(
                        this@DirfActivity,
                        R.style.Paragraph_300_display_400
                    )
                )
        )
        text.append(" ")

        text.append(
            getString(R.string.text_body_dirf_02)
                .addSpannable(
                    TextAppearanceSpan(
                        this@DirfActivity,
                        R.style.Paragraph_300_display_400_bs
                    )
                )
        )

        return text
    }


    private fun btnDisable() {
        btnDirf.isEnabled = false
        btnDirf.alpha = 0.5f
    }

    private fun btnEnable() {
        btnDirf.isEnabled = true
        btnDirf.alpha = 1f
    }


    private fun textWatchNameResposable() {
        tv_name_resposable.setOnTextChangeListener(object :
            CieloTextInputView.TextChangeListener {
            override fun onTextChanged(
                userInput: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                userInput?.let {

                    if (it.length > 0 && !ev_nome_ra_social?.getText().isNullOrEmpty()
                        && !ev_dirf_cpf?.getText().isNullOrEmpty() && !ev_dirf_cnpj?.getText()
                            .isNullOrEmpty()
                        && !tv_year_value?.text.toString()
                            .equals(getString(R.string.text_dirf_year))
                    ) {
                        btnEnable()
                    } else {
                        btnDisable()
                    }

                }
            }
        })
    }

    private fun textWatchCpf() {
        ev_dirf_cpf?.setOnTextChangeListener(object :
            CieloTextInputView.TextChangeListener {
            override fun onTextChanged(
                userInput: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                userInput?.let {

                    if (it.length > 0 && !ev_nome_ra_social?.getText().isNullOrEmpty()
                        && !tv_name_resposable?.getText()
                            .isNullOrEmpty() && !ev_dirf_cnpj?.getText()
                            .isNullOrEmpty()
                        && !tv_year_value?.text.toString()
                            .equals(getString(R.string.text_dirf_year))
                    ) {
                        btnEnable()
                    } else {
                        btnDisable()
                    }

                }
            }
        })
    }

    private fun textWatchCnpj() {
        ev_dirf_cnpj?.setOnTextChangeListener(object :
            CieloTextInputView.TextChangeListener {
            override fun onTextChanged(
                userInput: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                userInput?.let {

                    if (it.length > ZERO && !ev_nome_ra_social?.getText().isNullOrEmpty()
                        && !ev_dirf_cpf?.getText().isNullOrEmpty() && !tv_name_resposable?.getText()
                            .isNullOrEmpty()
                        && !tv_year_value?.text.toString()
                            .equals(getString(R.string.text_dirf_year))
                    ) {
                        btnEnable()
                    } else {
                        btnDisable()
                    }

                }
            }
        })
    }

    private fun textWatchRaSocial() {
        ev_nome_ra_social?.setOnTextChangeListener(object :
            CieloTextInputView.TextChangeListener {
            override fun onTextChanged(
                userInput: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                userInput?.let {

                    if (it.length > ZERO && !tv_name_resposable?.getText().isNullOrEmpty()
                        && !ev_dirf_cpf?.getText().isNullOrEmpty() && !ev_dirf_cnpj?.getText()
                            .isNullOrEmpty()
                        && !tv_year_value?.text.toString()
                            .equals(getString(R.string.text_dirf_year))
                    ) {
                        btnEnable()
                    } else {
                        btnDisable()
                    }

                }
            }
        })
    }

    override fun showSucesso(
        dirfResponse: DirfResponse,
        extension: String?
    ) {
        val localYear = year
        val file: File?

        val fileType = when (extension) {
            PDF -> DOT_EXTENSION_PDF
            EXCEL -> DOT_EXTENSION_EXCEL
            DEC -> DOT_EXTENSION_DEC
            else -> EMPTY
        }

        file = FileUtils(this).convertBase64ToFile(
            dirfResponse.file,
            FILE_PATH,
            "$FILE_NAME$localYear",
            fileType
        )

        startActivity(
            Intent(this, DirfResultActivity::class.java).apply {
                putExtra(DirfResultActivity.FILE_EXTRA, file)
                putExtra(FILE_EXTENSION,extension)
            }
        )

        displayedChild(DIRF_OPTIONS_VIEW_INDEX, vf_dirf)
        ga4.logClickDirfDownload(
            extension,
            FILE_NAME_NEW,
            localYear.toString(),
            BAIXAR_ARQUIVO
        )
        setupGooglePlayReview()
    }

    override fun serverError() {
        erroBadRequest(ErrorMessage())
    }

    override fun erroEnhance(error: ErrorMessage?) {
        ga4.logException(
            screenName = SCREEN_VIEW_DIRF,
            error = error,
        )
        doWhenResumed {
            displayedChild(DIRF_OPTIONS_VIEW_INDEX, vf_dirf)
            bt = bottomSheetGenericFlui(
                getString(R.string.text_registro_recebiveis),
                R.drawable.ic_07,
                getString(R.string.txt_erro_400_title),
                getString(R.string.txt_erro_400_subtitle),
                getString(R.string.btn_retornar_painel),
                getString(R.string.incomint_fast_cancellation_back_button),
                statusNameTopBar = false,
                statusBtnClose = false,
                statusBtnFirst = false,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            ).apply {
                this.onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            dialog.dismiss()
                        }
                    }
            }
            bt?.let {
                it.show(
                    supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
    }

    override fun erroBadRequest(error: ErrorMessage?) {
        doWhenResumed {
            ga4.logException(
                screenName = SCREEN_VIEW_DIRF,
                error = error,
            )
            displayedChild(DIRF_OPTIONS_VIEW_INDEX, vf_dirf)
            bt = bottomSheetGenericFlui(
                getString(R.string.text_registro_recebiveis),
                R.drawable.ic_43,
                getString(R.string.txt_erro_system_title),
                getString(R.string.txt_erro_system_subtitle),
                getString(R.string.btn_retornar_painel),
                getString(R.string.text_try_again_label),
                statusNameTopBar = false,
                statusBtnClose = false,
                statusBtnFirst = false,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            ).apply {
                this.onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            dialog.dismiss()
                        }
                    }
            }
            bt?.let {
                it.show(
                    supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
    }

    override fun verificationPJ() {
        doWhenResumed {
            displayedChild(DIRF_OPTIONS_VIEW_INDEX, vf_dirf)
            bt = bottomSheetGenericFlui(
                getString(R.string.text_registro_recebiveis),
                R.drawable.ic_07,
                getString(R.string.txt_erro_420_title),
                getString(R.string.txt_erro_420_subtitle),
                getString(R.string.btn_retornar_painel),
                getString(R.string.incomint_fast_cancellation_back_button),
                statusNameTopBar = false,
                statusBtnClose = false,
                statusBtnFirst = false,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            ).apply {
                this.onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            if (isAttached()) {
                                dialog.dismiss()
                                finishAndRemoveTask()
                            }
                        }

                        override fun onSwipeClosed() {
                            if (isAttached())
                                finishAndRemoveTask()
                        }
                    }
            }
            bt?.let {
                it.show(
                    supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_VIEW_DIRF)
    }
    /**
     * method to clear the disposible
     * */
    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun responseME(meResponse: MCMerchantResponse) {
        displayedChild(DIRF_OPTIONS_VIEW_INDEX, vf_dirf)
        this.meResponse = meResponse

        if (ValidationUtils.isCNPJ(meResponse.cnpj).not()) {
            verificationPJ()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_common_faq, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                startActivity<CentralAjudaSubCategoriasEngineActivity>(
                    ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_DIRF,
                    ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.title_sub_menu_Dirf),
                    CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupGooglePlayReview() {
        setupObserver()
        viewModel.onRequestReview(
            context = this,
            activity = this,
            featureToggleFlowKey = FeatureTogglePreference.GOOGLE_PLAY_REVIEW_DIRF,
            isFeatureToggleFlow = true
        )
    }

    private fun setupObserver() {
        viewModel.googlePlayReviewLiveData.observe(this) {}
    }
}