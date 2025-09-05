package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.linkpgtogerado

import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.HtmlCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.COMPARTILHAR
import br.com.mobicare.cielo.commons.analytics.Action.COPIAR
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.analytics.Label.WHATSAPP
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.constants.IntentAction.TEXT_PLAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp
import br.com.mobicare.cielo.commons.constants.WhatsApp.PLEASE_INSTALL_APP_AGAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_PACKAGE_NAME
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.DICAS_DE_SEGURANCA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIXEIRA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog.PgLinkDetailPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_pagamento_por_link_gerado.*
import kotlinx.android.synthetic.main.fragment_pagamento_por_link_gerado_text_explanation.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PagamentoPorLinkGeradoBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private val presenter: PgLinkDetailPresenter by inject {
        parametersOf(this)
    }
    private lateinit var gaPurposesTypeSale: String

    companion object {
        private const val SEND_PRODUCT = "Enviar um produto"
        const val RESPONSE = "RESPONSE"
        fun newInstance(response: CreateLinkBodyResponse?) = PagamentoPorLinkGeradoBottomSheet().apply {
            this.arguments = Bundle().apply {
                putParcelable(RESPONSE, response)
            }
        }
    }

    val URL = "url"

    var response: CreateLinkBodyResponse? = null
    var onDismissListener: OnDismissListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_pagamento_por_link_gerado, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureListeners()
        loadParameters()
        populateFields()
        initSpanTextView()
    }

    override fun onResume() {
        Analytics.trackScreenView(
            screenName = "/pagamento-por-link/super-link/criar-novo-link/link-gerado",
            screenClass = this.javaClass
        )
        super.onResume()
    }

    fun initSpanTextView() {
        SpanHelper.setSpanOnText(textViewPayAtention, requireContext())
        SpanHelper.setSpanOnText(textViewNotificationescription, requireContext())
        SpanHelper.setSpanOnText(textViewCallExplanation1, requireContext())
        SpanHelper.setSpanOnText(textViewCallExplanation4, requireContext())
        SpanHelper.setSpanOnText(textViewCallExplanation5, requireContext())
    }

    override fun onClick(v: View?) {
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onCloseLinkDetailBottomSheet()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        changeDialog(dialog)
        return dialog
    }

    /**
     * método para vericar quando o dialog muda de estado
     * @param dialog
     * */
    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                    R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun configureListeners() {
        this.btn_close?.setOnClickListener {
            gaSendWhatButton(FECHAR)
            this.dismiss()
        }

        this.btn_trash?.setOnClickListener {
            gaSendDelete(productNameTextView.text.toString())
            gaSendWhatButton(LIXEIRA)
            dialogConfirmationDelete()
        }

        this.btn_zap?.setOnClickListener {
            gaSendShare(productNameTextView.text.toString())
            gaSendWhatButton(WHATSAPP)
            verificationZapInDevice()
        }

        this.btn_share?.setOnClickListener {
            gaSendShare(productNameTextView.text.toString())
            gaSendWhatButton(COMPARTILHAR)
            shareDefaultDevice()
        }

        this.btn_copy?.setOnClickListener {
            gaSendShare(productNameTextView.text.toString())
            gaSendWhatButton(COPIAR)
            copyToClipboard(link_value.text.toString())
        }
        this.imageViewCopy?.setOnClickListener {
            gaSendShare(productNameTextView.text.toString())
            gaSendWhatButton(COPIAR)
            copyToClipboard(link_value.text.toString())
        }
        this.textViewCallExplanation5.setOnClickListener {
            gaSendWhatButton(DICAS_DE_SEGURANCA)
        }
    }

    private fun loadParameters() {
        this.arguments?.getParcelable<CreateLinkBodyResponse?>(RESPONSE)?.let {
            this.response = it
        }
    }

    private fun populateFields() {
        this.response?.let { itResponse ->
            this.productNameTextView?.text = itResponse.name
            this.priceTextView?.text = getString(R.string.tv_value_link, itResponse.price.toPtBrRealString())
            this.link_value?.text = itResponse.url
        }
        this.warningTextView?.text = SpannableString(HtmlCompat
                .fromHtml(getString(R.string.text_loggi_warning),
                        HtmlCompat.FROM_HTML_MODE_LEGACY))
    }

    /**
     * método para copiar o link
     * @param copyText
     * */
    fun copyToClipboard(copyText: String) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(URL, copyText)
        clipboard.setPrimaryClip(clip)
        val toast = Toast.makeText(activity, R.string.link_copiado, Toast.LENGTH_SHORT)
        toast.show()
    }

    /**
     * método mostrar o compartilhamento default do device
     * */
    private fun shareDefaultDevice() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)

        intent.setType(TEXT_PLAIN)
        intent.putExtra(Intent.EXTRA_TEXT, link_value.text.toString())
        startActivity(Intent.createChooser(intent, SHARE_WITH))
    }

    /**
     * método para verificar se o pacote do whatshapp tem no device do usuário
     * @param uri
     * */
    fun appInstalledOrNot(uri: String): Boolean {
        val pm = requireActivity().packageManager
        var app_installed: Boolean
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }

    /**
     * método para verificar se o whatshapp está no devide
     * */
    private fun verificationZapInDevice() {
        if (!appInstalledOrNot(WHATSAPP_PACKAGE_NAME)) {
            // Toast message not installed.
            requireContext().showMessage(message = PLEASE_INSTALL_APP_AGAIN,
                    title = WhatsApp.WHATSAPP_NOT_INSTALLED)
        } else {
            sendMsgWhatsapp()
        }
    }

    /**
     * método que manda msg para o usuário via whatsapp
     * */
    private fun sendMsgWhatsapp() {
        this.response?.let { itResponse ->
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = TEXT_PLAIN
            val text = getString(R.string.payment_link_for, itResponse.name, itResponse.url)
            val pm = requireActivity().packageManager
            pm.getApplicationInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_META_DATA)
            waIntent.setPackage(WHATSAPP_PACKAGE_NAME)
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(waIntent, SHARE_WITH))
        }
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun showProgress() {
        errorView.gone()
        frame_progress_view.visible()
        layout_pg_detail.gone()
    }

    fun hideProgress() {
        errorView.visible()
        frame_progress_view.gone()
        layout_pg_detail.gone()
    }

    /**
     * método que verifica o status code do http callback
     * */
    private fun statusHttpCode(code: Int) {
        when (code) {
            in 500..509 -> {
                hideProgress()
            }
            204 -> {
                this.onDismissListener?.onCloseLinkDetailBottomSheet()
            }
            401 -> {
                requireContext().showMessage(message = getString(R.string.error_403))
            }
        }
    }

    /**
     * método que mostra um dialog de confirmação de exclusão de link
     * */
    private fun dialogConfirmationDelete() {
        var alertDialogCustom: AlertDialogCustom.Builder? = null

        alertDialogCustom = AlertDialogCustom.Builder(this.context, "")
                .setMessage(getString(R.string.want_to_delete_this_link))
                .setBtnRight(getString(R.string.confirmar))
                .setBtnLeft(getString(R.string.cancelar))
                .setCancelable(false)
                .setOnclickListenerRight {
                    this.response?.let {
                        presenter.deleteLink(deleteLink = DeleteLink(it.id), callback = {
                            statusHttpCode(it)
                        })
                    }
                }
                .setOnclickListenerLeft {
                    alertDialogCustom?.show()?.dismiss()
                }
        alertDialogCustom?.show()
    }

    private fun gaSendShare(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(typeLink()),
            label = listOf(COMPARTILHAR, labelButton)
        )
    }

    private fun gaSendDelete(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.EXCLUIR, typeLink()),
            label = listOf(Label.BOTAO, labelButton)
        )
    }

    private fun gaSendWhatButton(labelButton: String) {
        val list = ArrayList<String>()
        list.add(SEND_PRODUCT)
        list.add("Loggi")
        list.add(labelButton)
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.BOTAO, typeLink()),
            label = list
        )
    }

    private fun typeLink() = "link gerado"

    interface OnDismissListener {
        fun onCloseLinkDetailBottomSheet()
    }

    interface OnPgLinkListener {
        fun deleteLink(deleteLink: DeleteLink, callback: (Int)->Unit)
    }
}