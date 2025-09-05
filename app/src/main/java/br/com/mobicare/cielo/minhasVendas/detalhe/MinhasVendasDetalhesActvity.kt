package br.com.mobicare.cielo.minhasVendas.detalhe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ItemDetalhesVendas
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.ActivityDetector.Companion.getActivityDetector
import br.com.mobicare.cielo.commons.utils.Utils.logout
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.presentation.ui.adapters.ExtratoDetalheAdapter
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.minhasVendas.activities.CancelDetailActivity
import br.com.mobicare.cielo.minhasVendas.constants.INELEGIBLE_SALE
import br.com.mobicare.cielo.minhasVendas.constants.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.minhasVendas.constants.NOT_FOUND
import br.com.mobicare.cielo.minhasVendas.constants.WITHOUT_BALANCE
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_DETAILS
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_minhas_vendas_detalhe.*
import kotlinx.android.synthetic.main.content_comprovante.*
import kotlinx.android.synthetic.main.content_extrato_detalhe.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * create by Enzo Teles
 * */


//TODO [MYSALES] - Remover esta classe posteriormente a migracao do MVVM
class MinhasVendasDetalhesActvity : BaseLoggedActivity(), MinhasVendasDetalhesContract.View,
    AllowMeContract.View, OnCancelamentoContract.View {

    private val ga4: MySalesGA4 by inject()
    private var isShowSharedButton = true
    private val PIX = "PIX"
    private lateinit var sale: Sale
    val presenter: MinhasVendasDetalhesPresenter by inject {
        parametersOf(this)
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

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(this)
    }

    private val presenterCancelSale: CancelamentoPresenter by inject {
        parametersOf(this)
    }

    private val isSaleToday: Boolean by lazy {
        intent.getBooleanExtra(IS_SALE_TODAY_ARGS,false)
    }

    companion object {
        const val MINHAS_VENDAS_DETALHES = "MINHAS_VENDAS_DETALHES"
        fun newIntent(context: Context, sale: Sale) = Intent(context, MinhasVendasDetalhesActvity::class.java).apply {
            putExtra(MINHAS_VENDAS_DETALHES, sale)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_vendas_detalhe)
        mAllowMeContextual = allowMePresenter.init(this)
        setupToolbar(toolbarDetailMinhasVendas as Toolbar, resources.getString(R.string.extrato_detalhes))
        intent?.extras?.getParcelable<Sale>(MINHAS_VENDAS_DETALHES)?.let {
            it.statusCode?.let { itStatus ->
                this.isShowSharedButton = itStatus != ExtratoStatusDef.NEGADA
            }
            useSecurityHash?.let { useSecurityHash ->
                if (useSecurityHash) {
                    sale = it
                    allowMePresenter.collect(
                        mAllowMeContextual = mAllowMeContextual,
                        this,
                        mandatory = false
                    )
                } else {
                    presenter.load(it, EMPTY)
                }
            }

        }
        setupMfaRouterHandler()
    }

    override fun onResume() {
        super.onResume()
        mfaRouteHandler.onResume()
        ga4.logScreenView(SCREEN_NAME_SALES_DETAILS)
    }

    override fun onPause() {
        super.onPause()
        mfaRouteHandler.onPause()
    }

    private fun getFormatedSalesDate(dt: String?): String {
        val simpleInputDtFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault())
        val simpleOutputDtFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                Locale.getDefault())

        val dtOfSale = try {
            simpleInputDtFormat.parse(dt)
        } catch (ex: ParseException) {
            Date()
        }

        return simpleOutputDtFormat.format(dtOfSale)
    }

    override fun populateDetail(items: ArrayList<ItemDetalhesVendas>) {
        recycler_view_extrato_detalhe.layoutManager = (androidx.recyclerview.widget.LinearLayoutManager(this))
        recycler_view_extrato_detalhe.adapter = (ExtratoDetalheAdapter(this, items))
    }

    /**
     * método que seta o texto na label com o nome do status que vem da api
     * @param status , color
     * */
    override fun loadStatus(status: String?, @ColorRes color: Int) {
        status?.let {
            text_view_extrato_detalhe_status.text = it
            text_view_extrato_detalhe_status.setBackgroundResource(color)
            if (it.toLowerCasePTBR() == getString(R.string.text_details_sale_status_denied)) {
                content_comprovante?.gone()
                txtDetailsSaleObs?.gone()
                setupCancelSale(isShow = false, isEnabled = false)
            } else if (it.toLowerCasePTBR() == getString(R.string.text_details_sale_status_refresh)) {
                text_view_extrato_detalhe_status.text = getString(R.string.extrato_status_atualizar_label)
                text_view_extrato_detalhe_status.setOnClickListener {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAP_LABEL),
                        action = listOf(getActivityDetector().screenCurrentPath()),
                        label = listOf(text_view_extrato_detalhe_status.text.toString())
                    )
                    showAlert(getString(R.string.extrato_status_atualizar_title))
                }
                setupCancelSale(isShow = true, isEnabled = false)
            }
        }
    }

    override fun setupCancelSale(isShow: Boolean, isEnabled: Boolean) {
        txtDetailsSaleObs?.visible(isShow && (isEnabled && isSaleToday.not()))
        alertMessage?.visible(isShow && (isEnabled.not() || isSaleToday))
        btnCancelSale?.visible(isShow)
        if (isEnabled && isSaleToday.not()) {
            btnCancelSale?.setOnClickListener {
                cancelSale()
                ga4.beginCancel(SCREEN_NAME_SALES_DETAILS, BUTTON)
            }
        } else {
            btnCancelSale?.isEnabled = false
            btnCancelSale?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            btnCancelSale?.setBackgroundResource(R.drawable.background_rounded_display_200)
        }
    }

    private fun setupMfaRouterHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show)
                showLoading()
            else
                hideLoading()
        }
    }

    private fun showLoading() = loadingCancelSale?.visible()

    private fun hideLoading() = loadingCancelSale?.gone()

    private fun cancelSale(){
        mfaRouteHandler.runWithMfaToken{
            showLoading()
            presenterCancelSale.balanceInquiry(sale)
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
            error.logout -> logout(this)
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

    /**
     * modal mostrado para o cliente quando a venda não está aprovada
     * @param title
     * */
    fun showAlert(title: String) {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.extrato_status_atualizar_dialog, null)

        AlertDialogCustom.Builder(this, getString(R.string.extrato_detalhes))
                .setTitle(title)
                .setView(layout)
                .setBtnRight(getString(android.R.string.ok))
                .show()
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
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf(Action.EXTRATO_DETALHES_VENDA),
                label = listOf(Action.COMPARTILHAR)
            )
            shareScreenshot()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun createScreenshot(): Uri? {
        layout_extrato_recibo.isDrawingCacheEnabled = true
        val b = layout_extrato_recibo.drawingCache

        val screenshotDir = File(baseContext.cacheDir, "images")
        screenshotDir.mkdirs()
        val screenshotFile = File(baseContext.cacheDir, "images/image.png")

        val stream = FileOutputStream(screenshotFile)
        b.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        stream.close()

        return FileProvider.getUriForFile(baseContext, "br.com.mobicare.cielo.fileprovider", screenshotFile)
    }

    fun shareScreenshot() {
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


    /**
     * método que popula a felipeta do detalhe da venda
     * */
    @SuppressLint("NewApi")
    override fun populateNote(sale: Sale) {
        text_view_extrato_comprovante_brand.text = sale.cardBrandDescription ?: sale.cardBrand
        text_view_extrato_comprovante_paymentType.text = sale.paymentType
        text_view_extrato_comprovante_creditCardNumber.text = "**** **** **** " + (if (sale.cardNumber != null) sale.cardNumber.replace("*", "") else sale.truncatedCardNumber)
        text_view_extrato_comprovante_terminalNumber.text = getString(R.string.extrato_recibo_pos) + sale.terminal
        text_view_extrato_comprovante_doc.text = getString(R.string.extrato_recibo_doc) + sale.nsu
        text_view_extrato_comprovante_aut.text = getString(R.string.extrato_recibo_aut) + sale.authorizationCode
        text_view_extrato_comprovante_date.text = getFormatedSalesDate(sale.date)
        text_view_extrato_comprovante_amount.text = if (sale.amount != null) sale.amount.toPtBrRealString() else sale.grossAmount?.toPtBrRealString()

        if (sale.cardBrand?.uppercase(Locale.getDefault()).equals(PIX)){
            text_view_extrato_comprovante_creditCardNumber.gone()
            text_view_extrato_comprovante_aut.gone()
        }
    }


    /**
     * método para limpar o composite
     * */
    override fun onDestroy() {
        super.onDestroy()
        //presenter.onCleared()
    }

    override fun merchantResponse(response: UserOwnerResponse) {
        vf_mv_detail.displayedChild = 1

        if (isAttached()) {
            response.cnpj?.let { text_view_extrato_comprovante_DocumentNumber.text = getString(R.string.extrato_recibo_cnpj) + " " + addMaskCPForCNPJ(it, getString(R.string.mask_cnpj_step4)) }
            response.companyName?.let { text_view_extrato_comprovante_name.text = it }
            response.addresses[0]?.let { text_view_extrato_comprovante_address.text = "${it.streetAddress} ${it.neighborhood} ${it.city} ${it.state}" }

        }
    }

    override fun onError() {
        vf_mv_detail.visibility = View.GONE
    }



    override fun successCollectToken(result: String) {
        proceedLoad(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlertBS(errorMessage)
        } else {
            FirebaseCrashlytics.getInstance().log(errorMessage)
            proceedLoad(result)
        }
    }

    private fun proceedLoad(result: String?) {
        result?.let {
            presenter.load(sale, it)
        }
    }

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

    private fun exceptionGA4(error: ErrorMessage?){
        ga4.logException(SCREEN_NAME_SALES_DETAILS, errorMessage = error)
    }
}