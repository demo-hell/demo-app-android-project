package br.com.mobicare.cielo.meusrecebimentosnew.calculationview

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Base64
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.DOUBLE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.displayedChild
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.MeusRecebimentosFragmentNewBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatarValores
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeActivityNew
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters.MeusRecebimentosRecebiveisAdapter
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.MeusRecebimentosGraficoFragmentNew
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.ComponentFilterFragmentNew
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.ComponentFilterListener
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType
import br.com.mobicare.cielo.meusrecebimentosnew.models.Link
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import br.com.mobicare.cielo.meusrecebimentosnew.models.SummaryItems
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4.Companion.SCREEN_NAME_RECEIVABLES
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.BankAccountItem
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_OBJECT
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_QUICKFILTER
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.VisaoSumarizadaMeusRecebimentosActivity
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pix.constants.AUTHORITY_PROVIDER
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_chargeback_document_view.progressIndicator
import kotlinx.android.synthetic.main.item_card_bancos.view.layout_meus_recebimentos_gravame
import kotlinx.android.synthetic.main.item_card_bancos.view.layout_meus_recebimentos_item_institution
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_agencia
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_conta
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_lancamentos
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_name_institution
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_nome_banco
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_number_institution
import kotlinx.android.synthetic.main.item_card_bancos.view.meus_recebimentos_valor_deposito_banco
import kotlinx.android.synthetic.main.item_prepaid_card_bank.view.textPrepaidBalance
import kotlinx.android.synthetic.main.item_prepaid_card_bank.view.textPrepaidStatementCount
import kotlinx.android.synthetic.main.layout_receivables_alert_payments.view.imageview_expander
import kotlinx.android.synthetic.main.layout_receivables_alert_payments.view.layout_card_descricao
import kotlinx.android.synthetic.main.layout_receivables_alert_payments.view.layout_card_expansivel
import kotlinx.android.synthetic.main.layout_receivables_alert_payments.view.textview_card_descricao
import kotlinx.android.synthetic.main.layout_receivables_alert_payments.view.textview_card_titulo
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import java.io.File

class MeusRecebimentosFragmentNew : BaseFragment(), MeusRecebimentosView, ComponentFilterListener {

    private var presenter: MeusRecebimentosPresenterNew? = null
    private var selectedDay: Int = -1
    private val ga4: MyReceivablesGA4 by inject()
    private var isCardExpanded = false
    private var date = "04/03/2024"
    private lateinit var binding: MeusRecebimentosFragmentNewBinding

    companion object {
        const val SCREEN_NAME = "/MeusRecebimentos"

        fun create(): MeusRecebimentosFragmentNew {
            val fragment = MeusRecebimentosFragmentNew()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Analytics.trackScreenView(
            screenName = "/meus-recebimentos",
            screenClass = javaClass
        )
        binding = MeusRecebimentosFragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init()
        binding.contentDepositadoPendente.layoutErrorExtrato.buttonLoadAgain.setOnClickListener {
            binding.contentDepositadoPendente.vfCard.let {
                it.visible()
                displayedChild(0, it)
            }
            binding.contentDepositadoPendente.layoutErrorExtrato.root.gone()
            init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
        ga4.logScreenView(SCREEN_NAME_RECEIVABLES, EMPTY)
    }

    private fun init() {
        ComponentFilterFragmentNew.use18MonthsInCalendar = true
        ComponentFilterFragmentNew
            .newInstance(this)
            .addInFrame(childFragmentManager, R.id.content_filter)

        arguments?.let {
            selectedDay = it.getInt(MeusRecebimentosHomeActivityNew.CURRENT_POSITION, -1)
        }
        presenter = MeusRecebimentosPresenterImpl(this)
        configureToolbarActionListener?.changeTo(
            R.color.colorPrimary,
            R.color.colorPrimaryDark, title =
            getString(R.string.text_values_received_navigation_label)
        )
        initClick()
        initCalculationVisionRecyclerView()
        configureReceivablesBankAccountsRecyclerView()
        presenter?.initializeAlerts()

        doWhenResumed {
            InteractBannersUtils.launchInteractBanner(
                bannerType = InteractBannerTypes.LEADERBOARD,
                shouldGetOffersFromApi = false,
                frame = R.id.frameInteractLeaderboardBannersOffers,
                fragmentActivity = requireActivity(),
                bannerControl = BannerControl.LeaderboardReceivables,
                onSuccess = {
                    doWhenResumed {
                        if (isAdded) {
                            binding.frameInteractLeaderboardBannersOffers.visible()
                        }
                    }
                }
            )
        }

    }

    override fun hideAlerts() {
        binding.generatePdf.gone()
    }

    override fun showAlerts(retorno: String) {
        date = retorno
        presenter?.onLoadAlerts()
        binding.generatePdf.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf(SCREEN_NAME),
                label = listOf("Gerar PDF")
            )
            presenter?.onGeneratePdfAlerts()
        }
    }

    private fun initClick() {
        binding.contentDepositadoPendente.meusRecebimentosLinearImageArrowDown.setOnClickListener {
            changeCardVisibility()
        }
        binding.contentDepositadoPendente.linearImageArrowRight.setOnClickListener {
            presenter?.onClickPendingAmount()
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf(SCREEN_NAME),
                label = listOf("Valor Pedente")
            )
        }
    }

    override fun onCalculationVisionSuccess(
        summaryResponse: SummaryResponse,
        quickFilter: QuickFilter
    ) {
        binding.contentDepositadoPendente.layoutErrorExtrato.root.gone()
        quickFilter.finalDate?.let { quickFilter.initialDate?.let { it1 -> showExtrato(it1, it) } }

        this.onShowLoadingCalculationVision(false)
        binding.contentDepositadoPendente.vfCard.let {
            it.visible()
            displayedChild(1, it)
        }
        binding.contentDepositadoPendente.pendingValue.text = Utils.formatValue(DOUBLE)
        binding.contentDepositadoPendente.titlePaid.text =
            configExpectedDeposited(summaryResponse.summary)

        if (summaryResponse.summary.paidAmount != null)
            binding.contentDepositadoPendente.paidValue.formatarValores(
                summaryResponse.summary.paidAmount,
                false
            )
        else
            if (summaryResponse.summary.expectedAmount != null)
                binding.contentDepositadoPendente.paidValue.formatarValores(
                    summaryResponse.summary.expectedAmount,
                    false
                )
            else binding.contentDepositadoPendente.paidValue.formatarValores(DOUBLE, false)

        if (summaryResponse.summary.totalAmount == DOUBLE) {
            binding.contentDepositadoPendente.arrowDown.gone()
        } else {
            binding.contentDepositadoPendente.arrowDown.visible()
        }
        binding.contentDepositadoPendente.linearImageArrowRight.isClickable = false
        summaryResponse.summary.pendingAmount.let {
            if (it != DOUBLE) {
                binding.contentDepositadoPendente.linearImageArrowRight.isClickable = true
                binding.contentDepositadoPendente.arrowRight.visible()
            }
            binding.contentDepositadoPendente.pendingValue.formatarValores(it, false)
        }
        loadCalculationVisionRecyclerView(summaryResponse.summaryItems, quickFilter)
    }

    override fun onClickPendingAmount(summary: Summary, quickFilter: QuickFilter) {
        //TODO - Incluir strings em constantes
        val item = SummaryItems(
            ONE_HUNDRED, "Valores pendentes", summary.pendingAmount,
            arrayListOf(
                Link("self", getString(R.string.item_summary_items_link, BuildConfig.HOST_API))
            )
        )
        context?.startActivity(
            Intent(
                activity,
                VisaoSumarizadaMeusRecebimentosActivity::class.java
            ).let {
                it.putExtra(PARAM_OBJECT, item)
                it.putExtra(PARAM_QUICKFILTER, quickFilter)
            })
    }

    override fun onCalculationVisionError(error: ErrorMessage) {
        binding.contentDepositadoPendente.vfCard.gone()
        binding.contentDepositadoPendente.layoutErrorExtrato.root.visible()
        ga4.logException(SCREEN_NAME_RECEIVABLES, EMPTY, error)
    }

    override fun onShowLoadingReceivablesBankAccounts(isShow: Boolean) {
        if (isShow) {
            binding.contentBancos.meusRecebimentosRecyclerViewBancos.invisible()
            binding.contentBancos.loadingReceivablesBankAccounts.visible()
        } else {
            binding.contentBancos.loadingReceivablesBankAccounts.gone()
        }
    }

    override fun onShowReceivablesBankAccounts(items: List<BankAccountItem>, isPrevisto: Boolean) {
        this.onShowLoadingReceivablesBankAccounts(false)
        binding.contentBancos.meusRecebimentosRecyclerViewBancos.visible()
        val adapter = DefaultViewListAdapter<BankAccountItem>(items, R.layout.item_card_bancos)
        adapter.setBindItemViewType(object :
            DefaultViewListAdapter.OnBindItemViewType<BankAccountItem> {
            override fun onBind(position: Int, item: BankAccountItem): Int {
                if (item.bank?.code == "996" && item.bank?.agency == "0" && item.bank?.account == "0") {
                    return R.layout.item_prepaid_card_bank;
                }
                return R.layout.item_card_bancos;
            }
        })
        adapter.setBindViewHolderCallback(object :
            DefaultViewListAdapter.OnBindViewHolder<BankAccountItem> {
            private fun formatEntries(quantity: Int?): String {
                var text = ""
                quantity?.let {
                    text = "${quantity} lançamento"
                    if (it > 1) {
                        text += "s"
                    }
                }
                return text
            }

            override fun onBind(item: BankAccountItem, holder: DefaultViewHolderKotlin) {
                if (holder.itemViewType == R.layout.item_card_bancos) {
                    holder.mView.meus_recebimentos_nome_banco?.text = item.bank?.name
                    holder.mView.meus_recebimentos_agencia?.text = "Agência: ${item.bank?.agency}"
                    holder.mView.meus_recebimentos_conta?.text = "Conta: ${item.bank?.account}"
                    holder.mView.meus_recebimentos_valor_deposito_banco?.text =
                        item.netAmount?.toPtBrRealString()
                    holder.mView.meus_recebimentos_lancamentos?.text = formatEntries(item.quantity)
                    if (item.collateralCredit == true && item.creditInstitution != null) {
                        holder.mView.layout_meus_recebimentos_gravame?.visible()
                        holder.mView.meus_recebimentos_name_institution?.text =
                            item.creditInstitution?.name
                        holder.mView.meus_recebimentos_number_institution?.text =
                            item.creditInstitution?.identificationNumber?.let {
                                FormHelper.maskFormatter(
                                    it, CNPJ_MASK_COMPLETE_FORMAT
                                ).formattedText.string
                            }
                    } else {
                        holder.mView.layout_meus_recebimentos_item_institution?.gone()
                    }
                } else {
                    holder.mView.textPrepaidBalance?.text = item.netAmount?.toPtBrRealString()
                    holder.mView.textPrepaidStatementCount?.text = formatEntries(item.quantity)
                }
            }
        })
        binding.contentBancos.meusRecebimentosRecyclerViewBancos.adapter = adapter
    }

    override fun onHideReceivablesBankAccounts() {
        this.onShowLoadingReceivablesBankAccounts(false)
        binding.contentBancos.meusRecebimentosRecyclerViewBancos.gone()
    }

    override fun onShowLoadingCalculationVision(isShow: Boolean) {
        binding.contentDepositadoPendente.vfCard.let {
            it.visible()
            if (isShow) {
                displayedChild(0, it)
            } else {
                displayedChild(1, it)
            }
        }
    }

    override fun onLoadAlertsSuccess(it: AlertsResponse?) {
        binding.generatePdf.visible()
        it?.let { it1 -> addCards(it1, binding.layoutMeusRecebimentos) }
    }

    override fun onLoadAlertsError(convertToErro: ErrorMessage) {
        binding.generatePdf.gone()
    }

    override fun onLoadAlertsPdfSuccess(it: FileResponse?) {
//TODO - Incluir strings em constantes
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_group_fees,
            "Comunicação gerada com sucesso!",
            "",
            "Visualizar Arquivo",
            "Compartilhar Arquivo",
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    loadPdfFromBase64(it?.file ?: EMPTY)
                }

                override fun onBtnSecond(dialog: Dialog) {
                    val file = it?.file?.let { it1 ->
                        FileUtils(requireContext()).convertBase64ToFile(
                            base64String = it1,
                            fileName = "ComunicadoPdf",
                            fileType = br.com.mobicare.cielo.commons.constants.Intent.PDF
                        )
                    }
                    if (file != null) {
                        FileUtils(requireContext()).startShare(file)
                    }
                }

                override fun onSwipeClosed() {
                    dismiss()
                }

                override fun onCancel() {
                    dismiss()
                }

            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))

    }

    override fun onLoadAlertsPdfError(convertToErro: ErrorMessage) {
        //TODO - Incluir strings em constantes
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.text_title_error))
            .message("Houve um erro ao abrir o PDF: ${convertToErro.message}")
            .closeTextButton(getString(R.string.ok))
            .build().let {
                it.onCloseButtonClickListener = View.OnClickListener {}
                it.showAllowingStateLoss(
                    requireActivity()
                        .supportFragmentManager, "CieloAlertDialog"
                )
            }
    }

    private fun initCalculationVisionRecyclerView() {
        binding.contentDepositadoPendente.recyclerViewIncoming.layoutManager =
            LinearLayoutManager(context)
        val decorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decorator.setDrawable(resources.getDrawable(R.drawable.line_decorator))
        binding.contentDepositadoPendente.recyclerViewIncoming.addItemDecoration(decorator)
    }

    private fun loadCalculationVisionRecyclerView(
        summaryItems: List<SummaryItems>,
        quickFilter: QuickFilter
    ) {
        binding.contentDepositadoPendente.recyclerViewIncoming.adapter = (summaryItems
            .let { MeusRecebimentosRecebiveisAdapter(it, context, quickFilter) })
    }

    private fun changeCardVisibility() {
        if (binding.contentDepositadoPendente.meusRecebimentosShowDepositos.visibility == View.VISIBLE) {
            binding.contentDepositadoPendente.meusRecebimentosShowDepositos.gone()
            binding.contentDepositadoPendente.arrowDown.rotation = 90F
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPCARD),
                action = listOf(SCREEN_NAME),
                label = listOf("Colapse Valor Depositado")
            )
        } else {
            binding.contentDepositadoPendente.meusRecebimentosShowDepositos.visible()
            binding.contentDepositadoPendente.arrowDown.rotation = -90F
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPCARD),
                action = listOf(SCREEN_NAME),
                label = listOf("Expand Valor Depositado")
            )
        }
    }

    private fun configExpectedDeposited(summary: Summary): String {
        return if (summary.paidAmount != null) {
            getString(R.string.meus_recebimentos_depositado)
        } else {
            getString(R.string.meus_recebimentos_previsto)
        }
    }

    override fun onClickDate(
        initialDate: String,
        finalDate: String,
        isGraphSelection: Boolean,
        selectedDateType: DayType.Type?
    ) {

        try {
            if (isGraphSelection) {
                if (isAttached()) {
                    (childFragmentManager
                        .findFragmentByTag(ComponentFilterFragmentNew::class.java.simpleName)
                            as? ComponentFilterListener.UpdateDateFromGraph)
                        ?.updateDateFromGraph(initialDate, finalDate)
                }
            }
            presenter?.onCreate(initialDate, finalDate)

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            if (e.stackTrace[ZERO].lineNumber < ZERO)
                FirebaseCrashlytics.getInstance().setCustomKey(
                    SCREEN_NAME,
                    getString(R.string.text_message_line_number_unavailable)
                )
            else
                FirebaseCrashlytics.getInstance().setCustomKey(
                    SCREEN_NAME,
                    getString(
                        R.string.text_firebase_custom_key,
                        e.stackTrace[ZERO].lineNumber.toString(),
                        e.stackTrace[ZERO].className
                    )
                )
        }
    }

    override fun showGraph(mainDate: DataCustomNew) {
        binding.contentAlertGrafico.gone()
        binding.contentGraficoMeusRecebimentos.visible()
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.frameMyProfits, MeusRecebimentosGraficoFragmentNew
                    .newInstance(mainDate, selectedDay, this)
            )
            .commitAllowingStateLoss()

        if (selectedDay >= 0) selectedDay = -1
    }

    private fun showExtrato(initDate: String, finalDate: String) {
        BalcaoRecebiveisExtratoFragment
            .newInstance(initDate, finalDate)
            .addInFrame(childFragmentManager, R.id.frameExtrato)

    }

    override fun hideGraph() {
        binding.contentGraficoMeusRecebimentos.gone()
        binding.contentAlertGrafico.visible()
    }

    override fun showFilterErroAlert() = Unit

    private fun configureReceivablesBankAccountsRecyclerView() {
        binding.contentBancos.meusRecebimentosRecyclerViewBancos.layoutManager =
            LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_common_faq, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_help -> {
                openFAQ()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openFAQ() {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_REGISTRO_RECEBIVEIS,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_values_received_navigation_label),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    fun addCards(documento: AlertsResponse, parent: ViewGroup) {
        //TODO - Refatorar método
        val inflater = LayoutInflater.from(parent.context)

        if (documento.hasTextCredit) {
            val creditCard = inflater.inflate(
                R.layout.layout_receivables_alert_payments,
                binding.layoutMeusRecebimentos,
                false
            )
            setupCardExpansion(creditCard)
            creditCard.textview_card_titulo.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.title_receivables_payments_credit, date),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            creditCard.textview_card_descricao.text = SpannableString(
                HtmlCompat.fromHtml(
                    documento.textCreditApp,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            binding.layoutMeusRecebimentos.addView(creditCard, 0)
        }

        if (documento.hasTextDebit) {
            val debitCard = inflater.inflate(
                R.layout.layout_receivables_alert_payments,
                binding.layoutMeusRecebimentos,
                false
            )
            setupCardExpansion(debitCard)
            debitCard.textview_card_titulo.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.title_receivables_payments_debit, date),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            debitCard.textview_card_descricao.text = SpannableString(
                HtmlCompat.fromHtml(
                    documento.textDebitApp,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )

            binding.layoutMeusRecebimentos.addView(debitCard, 1)
        }

        if (documento.hasTextCreditAdjust) {
            val creditAdjustCard = inflater.inflate(
                R.layout.layout_receivables_alert_payments,
                binding.layoutMeusRecebimentos,
                false
            )
            setupCardExpansion(creditAdjustCard)

            creditAdjustCard.textview_card_titulo.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.title_receivables_payments_credit_adjust, date),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            creditAdjustCard.textview_card_descricao.text = SpannableString(
                HtmlCompat.fromHtml(
                    documento.textCreditAdjust,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )

            creditAdjustCard.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.card_background_adjust_credit
            )

            binding.layoutMeusRecebimentos.addView(creditAdjustCard, 2)
        }

        if (documento.hasTextDebitAdjust) {
            val debitAdjustCard = inflater.inflate(
                R.layout.layout_receivables_alert_payments,
                binding.layoutMeusRecebimentos,
                false
            )
            setupCardExpansion(debitAdjustCard)
            debitAdjustCard.textview_card_titulo.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.title_receivables_payments_debit_adjust, date),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            debitAdjustCard.textview_card_descricao.text = SpannableString(
                HtmlCompat.fromHtml(
                    documento.textDebitAdjust,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            debitAdjustCard.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.card_background_adjust_debit)
            binding.layoutMeusRecebimentos.addView(debitAdjustCard, 3)
        }
    }

    private fun setupCardExpansion(card: View) {
        val layoutCardExpansivel = card.layout_card_expansivel
        val layoutCardDescricao = card.layout_card_descricao
        val imageviewExpander = card.imageview_expander

        layoutCardExpansivel.setOnClickListener {
            layoutCardDescricao.visibility =
                if (layoutCardDescricao.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            imageviewExpander.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (layoutCardDescricao.visibility == View.VISIBLE) R.drawable.ic_arrow_up_tax else R.drawable.ic_arrow_down_tax
                )
            )
        }
    }

    private fun loadPdfFromBase64(encodedFile: String) {
        //TODO - Incluir strings em constantes
        try {
            val file = FileUtils(requireContext()).convertBase64ToFile(
                base64String = encodedFile,
                fileName = "ComunicadoPdf",
                fileType = br.com.mobicare.cielo.commons.constants.Intent.PDF
            )
            FileUtils(requireContext()).startShare(file)
        } catch (e: Exception) {
            e.printStackTrace()
            onDocumentError()
        }
    }

    private fun onDocumentError() {
        binding.apply {
            progressIndicator.gone()
        }
    }
}