package br.com.mobicare.cielo.mySales.presentation.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.ActivityDetector
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDateTime
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ActivityMinhasVendasDetalheBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.minhasVendas.detalhe.MinhasVendasDetalhesActvity
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.saleDetail.SaleDetailAdapter
import br.com.mobicare.cielo.mySales.presentation.viewmodel.SaleDetailsViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import br.com.mobicare.cielo.mySales.presentation.utils.SaleDetailField
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.minhasVendas.activities.CancelDetailActivity
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_DETAILS
import br.com.mobicare.cielo.minhasVendas.constants.INELEGIBLE_SALE
import br.com.mobicare.cielo.minhasVendas.constants.NOT_FOUND
import br.com.mobicare.cielo.minhasVendas.constants.WITHOUT_BALANCE
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.mySales.presentation.utils.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.mySales.presentation.utils.PIX
import br.com.mobicare.cielo.mySales.presentation.utils.SalesStatementStatus
import br.com.mobicare.cielo.mySales.presentation.utils.ScreenShotsConstants.FILE_PROVIDER_URI_PACKAGE
import br.com.mobicare.cielo.mySales.presentation.utils.ScreenShotsConstants.SCREENSHOT_DIR
import br.com.mobicare.cielo.mySales.presentation.utils.ScreenShotsConstants.SCREENSHOT_FILE
import java.util.Locale


//TODO [MYSALES] - IMPORTANTE
// Esta tela conta com cancelamento de vendas, porem por conta do escopo da migracao, a parte de cancelamento ficara
// como debito tecnico. Futuramente e necessario verificar o codigo antigo (MinhasVendasDetalhesActivity)
// e aplicar as acoes relacionadas ao cancelamento de vendas. Tambem e necessario verificar a recyclerview por
// conta do cancelamento.

class MySaleDetailsActivity: BaseLoggedActivity(), AllowMeContract.View, OnCancelamentoContract.View {

    private var binding: ActivityMinhasVendasDetalheBinding? = null
    private var isShowSharedButton = true
    private lateinit var sale: Sale
    private lateinit var adapter: SaleDetailAdapter
    private lateinit var mAllowMeContextual: AllowMeContextual
    private val ga4: MySalesGA4 by inject()



    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val viewModel: SaleDetailsViewModel by viewModel()

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(this)
    }

    private val presenterCancelSale: CancelamentoPresenter by inject {
        parametersOf(this)
    }

    private val isSaleToday: Boolean by lazy {
        intent.getBooleanExtra(IS_SALE_TODAY_ARGS,false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinhasVendasDetalheBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initToolbar()
        setupAllowmeAndGetExtras()
        setupSaleStatementUI()
    }

    override fun onResume() {
        super.onResume()
        handleSaleDetailsMerchantAPIService()
        ga4.logScreenView(SCREEN_NAME_SALES_DETAILS)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private val MINHAS_VENDAS_DETALHES = "MINHAS_VENDAS_DETALHES"
        fun newInstance(context: Context, sale: Sale) =
            Intent(context, MySaleDetailsActivity::class.java).apply{
                putExtra(MINHAS_VENDAS_DETALHES,sale)
        }
    }

    private fun handleSaleDetailsMerchantAPIService() {
        viewModel.getSaleMerchantViewState.observe(this) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> {
                    populateFilipetaWithMerchantData(state.data)
                }
                is MySalesViewState.ERROR -> {
                    val bindingSaleStatement = binding?.contentExtratoComprovante?.contentComprovante
                    bindingSaleStatement?.vfMvDetail?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAllowmeAndGetExtras(){
        mAllowMeContextual = allowMePresenter.init(this)
        intent?.extras?.getParcelable<Sale>(MinhasVendasDetalhesActvity.MINHAS_VENDAS_DETALHES)?.let {
            it.statusCode?.let { itStatus ->
                this.isShowSharedButton = itStatus != ExtratoStatusDef.NEGADA
            }
            viewModel.useSecurityHash.let { useSecurityHash ->
                if (useSecurityHash) {
                    sale = it
                    allowMePresenter.collect(
                        mAllowMeContextual = mAllowMeContextual,
                        this,
                        mandatory = false
                    )
                } else {
                    viewModel.createSaleDetailsStatement(it, EMPTY)
                }
            }
        }
        setupMfaRouterHandler()
    }


    //region - configuracao da toolbar
    private fun initToolbar() {
        binding?.toolbarDetailMinhasVendas?.toolbarMain?.let { toolbar ->
            setupToolbar(toolbar, resources.getString(R.string.extrato_detalhes))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_extrato_diario, menu)
        menu.findItem(R.id.action_extrato_shared).isVisible = this.isShowSharedButton
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_extrato_shared) {
            trackAnalyticsEvent(
                category = Category.TAPICON,
                action = Action.EXTRATO_DETALHES_VENDA,
                label = Action.COMPARTILHAR
            )
            shareScreenshot()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region - funcoes do allowme
    override fun successCollectToken(result: String) {
        viewModel.createSaleDetailsStatement(sale, result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlertBS(errorMessage)
        } else {
            FirebaseCrashlytics.getInstance().log(errorMessage)
            result?.let {
                viewModel.createSaleDetailsStatement(sale,it)
            }
        }
    }

    //endregion

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return this.supportFragmentManager
    }

    private fun showAlertBS(message: String) {
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

    //region - share
    private fun shareScreenshot() {
        val contentUri = createScreenshot()
        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.type = contentResolver.getType(contentUri)
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.extrato_detalhes)))
        }
    }

    private fun createScreenshot(): Uri? {
        val filipetaComprovanteLayout = binding?.contentExtratoComprovante?.contentComprovante?.layoutExtratoRecibo

        filipetaComprovanteLayout?.isDrawingCacheEnabled = true
        val b = filipetaComprovanteLayout?.drawingCache

        val screenshotDir = File(baseContext.cacheDir, SCREENSHOT_DIR)
        screenshotDir.mkdirs()
        val screenshotFile = File(baseContext.cacheDir, SCREENSHOT_FILE)

        val stream = FileOutputStream(screenshotFile)
        b?.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        stream.close()

        return FileProvider.getUriForFile(baseContext, FILE_PROVIDER_URI_PACKAGE, screenshotFile)
    }

    //endregion

    //region - metodos para popular os campos de detalhes da venda

    private fun setupSaleStatementUI() {
        val bindingSaleStatement = binding?.contentExtratoComprovante
        bindingSaleStatement?.apply {
            adapter = SaleDetailAdapter(createSaleDetailFields())
            recyclerViewExtratoDetalhe.layoutManager = LinearLayoutManager(this@MySaleDetailsActivity)
            recyclerViewExtratoDetalhe.adapter = adapter
        }
        showFilipeta()
        verifySaleStatus()
    }

    private fun showFilipeta(){
        val bindingSaleStatement = binding?.contentExtratoComprovante?.contentComprovante
        bindingSaleStatement?.apply {
            textViewExtratoComprovanteBrand.text = sale.cardBrandDescription ?: sale.cardBrand
            textViewExtratoComprovantePaymentType.text = sale.paymentType
            textViewExtratoComprovanteCreditCardNumber.text = "**** **** **** " + (if (sale.cardNumber != null) sale.cardNumber?.replace("*", "") else sale.truncatedCardNumber)
            textViewExtratoComprovanteTerminalNumber.text = getString(R.string.extrato_recibo_pos) + sale.terminal
            textViewExtratoComprovanteDoc.text = getString(R.string.extrato_recibo_doc) + sale.nsu
            textViewExtratoComprovanteAut.text = getString(R.string.extrato_recibo_aut) + sale.authorizationCode
            textViewExtratoComprovanteDate.text = sale.date?.convertTimeStampToDateTime(true)
            textViewExtratoComprovanteAmount.text = if (sale.amount != null) sale.amount?.toPtBrRealString() else sale.grossAmount?.toPtBrRealString()
            if(sale.cardBrand?.uppercase(Locale.getDefault()).equals(PIX)){
                textViewExtratoComprovanteCreditCardNumber.gone()
                textViewExtratoComprovanteAut.gone()
            }
        }
    }

    private fun populateFilipetaWithMerchantData(salesMerchantBO: SalesMerchantBO?){
        val bindingSaleStatement = binding?.contentExtratoComprovante?.contentComprovante
        salesMerchantBO?.let {
            bindingSaleStatement?.vfMvDetail?.displayedChild = ONE

            val cnpjText = getString(R.string.extrato_recibo_cnpj) + " " + addMaskCPForCNPJ(salesMerchantBO.cnpj, getString(R.string.mask_cnpj_step4))
            bindingSaleStatement?.textViewExtratoComprovanteDocumentNumber?.text = cnpjText

            val saleAddress = salesMerchantBO.address
            val addressText = "${saleAddress.streetAddress} ${saleAddress.neighborhood} ${saleAddress.city} ${saleAddress.state}"
            bindingSaleStatement?.textViewExtratoComprovanteAddress?.text = addressText

            bindingSaleStatement?.textViewExtratoComprovanteName?.text = salesMerchantBO.companyName
        }
    }

    private fun createSaleDetailFields(): MutableList<Pair<String,String>> {
        val listOfSaleFields: MutableList<Pair<String,String>> = mutableListOf()

        sale.date?.let {
            listOfSaleFields.add(Pair(SaleDetailField.SALE_DATE,it.convertTimeStampToDateTime(true)))
        }
        sale.authorizationDate?.let {
            listOfSaleFields.add(Pair(SaleDetailField.AUTHORIZATION_DATE,it.convertTimeStampToDate()))
        }

        if(sale.cardBrandCode == Text.PIX_CARDBRAND_CODE) {
            sale.transactionPixId?.let { pixId -> listOfSaleFields.add(Pair(SaleDetailField.ID,pixId)) }
        }else{
            sale.id?.let { listOfSaleFields.add(Pair(SaleDetailField.ID,it)) }
        }

        if(sale.transactionId != null)
            listOfSaleFields.add(Pair(SaleDetailField.TID,sale.transactionId!!))
        else if (sale.tid != null)
            listOfSaleFields.add(Pair(SaleDetailField.TID,sale.tid!!))


        sale.nsu?.let{ listOfSaleFields.add(Pair(SaleDetailField.NSU_DOC,it)) }
        sale.cardBrandDescription?.let {listOfSaleFields.add(Pair(SaleDetailField.FLAG,it)) }
        sale.grossAmount?.let { listOfSaleFields.add(Pair(SaleDetailField.SALE_VALUE,it.toPtBrRealString())) }
        sale.amount?.let { listOfSaleFields.add(Pair(SaleDetailField.SALE_VALUE, it.toPtBrRealString())) }
        sale.netAmount?.let { listOfSaleFields.add(Pair(SaleDetailField.NET_VALUE, it.toPtBrRealString())) }
        sale.administrationFee?.let { listOfSaleFields.add(Pair(SaleDetailField.RATE, "${it} %")) }
        sale.paymentType?.let {
            if (sale.installments != null && sale.installments!! > 0)
                listOfSaleFields.add(Pair(SaleDetailField.PAYMENT_METHODS, it + Text.ONE_SPACE + sale.installments + Text.X))
            else
                listOfSaleFields.add(Pair(SaleDetailField.PAYMENT_METHODS, it))
        }
        sale.paymentScheduleDate?.let {
            listOfSaleFields.add(Pair(SaleDetailField.PAYMENT_FORECAST, it.convertTimeStampToDate()))
        }
        sale.channel?.let { listOfSaleFields.add(Pair(SaleDetailField.SALES_CHANNEL, it)) }
        sale.paymentSolutionType?.let { listOfSaleFields.add(Pair(SaleDetailField.CAPTURE_TYPE,it)) }

        return listOfSaleFields
    }

    private fun verifySaleStatus() {
        sale.statusCode?.let {
            val saleStatusCapitalize = sale.status?.lowercase().capitalizePTBR()
            loadStatus(saleStatusCapitalize, SalesStatementStatus.getColor(it))

            if(it == SalesStatementStatus.APROVADA.value ||
                it == SalesStatementStatus.NEGADA.value ||
                it == SalesStatementStatus.ATUALIZAR.value) {

                setupCancelSale(SalesStatementStatus.isShow(it),SalesStatementStatus.isEnabled(it))
            }
        }
    }

    fun loadStatus(status: String?, @ColorRes color: Int){
        val saleStatementBinding = binding?.contentExtratoComprovante
        status?.let {
            saleStatementBinding?.textViewExtratoDetalheStatus?.text = it
            saleStatementBinding?.textViewExtratoDetalheStatus?.setBackgroundColor(resources.getColor(color))
            if(it.toLowerCasePTBR() == getString(R.string.text_details_sale_status_denied)){
                saleStatementBinding?.contentComprovante?.root.gone()
                saleStatementBinding?.txtDetailsSaleObs?.gone()
            }
            if(it.toLowerCasePTBR() == getString(R.string.text_details_sale_status_refresh)) {
                saleStatementBinding?.textViewExtratoDetalheStatus?.text = getString(R.string.extrato_status_atualizar_label)
                saleStatementBinding?.textViewExtratoDetalheStatus?.setOnClickListener {
                    trackAnalyticsEvent(
                        category = Category.TAP_LABEL,
                        action = ActivityDetector.getActivityDetector().screenCurrentPath(),
                        label = saleStatementBinding.textViewExtratoDetalheStatus.text.toString()
                    )
                    showAlert(getString(R.string.extrato_status_atualizar_title))
                }
            }
        }
    }

    fun showAlert(title: String) {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.extrato_status_atualizar_dialog, null)
        AlertDialogCustom.Builder(this, getString(R.string.extrato_detalhes))
            .setTitle(title)
            .setView(layout)
            .setBtnRight(getString(android.R.string.ok))
            .show()
    }

    //endregion

    private fun trackAnalyticsEvent(category: String,action: String,label: String){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, category),
            action = listOf(action),
            label = listOf(label)
        )
    }

    //region - Chamadas de cancelamento de vendas
    private fun setupMfaRouterHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show)
                showLoading()
            else
                hideLoading()
        }
    }
    private fun showLoading() = binding?.contentExtratoComprovante?.loadingCancelSale.visible()
    private fun hideLoading() = binding?.contentExtratoComprovante?.loadingCancelSale?.gone()
    private fun cancelSale(){
        mfaRouteHandler.runWithMfaToken{
            showLoading()
            presenterCancelSale.balanceInquiry(sale)
            ga4.beginCancel(SCREEN_NAME_SALES_DETAILS, BUTTON)
        }
    }
    private fun setupCancelSale(isShow: Boolean, isEnabled: Boolean) {
        binding?.contentExtratoComprovante?.txtDetailsSaleObs.visible(isShow && (isEnabled && isSaleToday.not()))
        binding?.contentExtratoComprovante?.alertMessage.visible(isShow && (isEnabled.not() || isSaleToday))
        binding?.contentExtratoComprovante?.btnCancelSale.visible(isShow)

        if (isEnabled && isSaleToday.not()) {
            binding?.contentExtratoComprovante?.btnCancelSale?.setOnClickListener {
                cancelSale()
            }
        } else {
            binding?.contentExtratoComprovante?.btnCancelSale?.isEnabled = false
            binding?.contentExtratoComprovante?.btnCancelSale?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            binding?.contentExtratoComprovante?.btnCancelSale?.setBackgroundResource(R.drawable.background_rounded_display_200)
        }
    }
    override fun onSucess(response: ResponseBanlanceInquiry) {
        hideLoading()
        try {
            val intent = CancelDetailActivity.newIntent(baseContext, response, true)
            startActivity(intent)
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    override fun onError(error: ErrorMessage) {
        hideLoading()
        exceptionGA4(error)
        when {
            error.logout -> Utils.logout(this)
            error.errorCode == WITHOUT_BALANCE -> createDialog(
                R.string.error_cancel_sem_saldo_titulo,
                R.string.error_cancel_sem_saldo_msg,
                R.drawable.ic_popup_01
            )
            error.errorCode == INELEGIBLE_SALE -> createDialog(
                R.string.error_cancel_error,
                R.string.error_cancel_error_msg,
                R.drawable.img_no_profile_access
            )
            error.errorCode == NOT_FOUND -> createDialog(
                R.string.dialog_unable_cancel,
                R.string.text_try_again,
                R.drawable.img_no_profile_access
            )
            else -> createDialog(
                R.string.error_cancel_error,
                R.string.error_cancel_error_msg,
                R.drawable.img_no_profile_access
            )
        }
    }
    private fun createDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes drawable: Int
    ) {
        CieloDialog.create(
            getString(title),
            getString(message),
        )
            .setImage(drawable)
            .setTitleColor(R.color.color_204986)
            .setMessageColor(R.color.color_5A646E)
            .setCloseButtonIcon(R.drawable.ic_close_blue)
            .setPrimaryButton(getString(R.string.entendi))
            .show(supportFragmentManager, null)
    }
    //endregion

    private fun exceptionGA4(error: ErrorMessage?){
        ga4.logException(SCREEN_NAME_SALES_DETAILS, errorMessage = error)
    }
}