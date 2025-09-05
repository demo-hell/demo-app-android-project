package br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.COMPARTILHAR
import br.com.mobicare.cielo.commons.analytics.Action.COPIAR
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Label.WHATSAPP
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.constants.IntentAction.TEXT_PLAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.PLEASE_INSTALL_APP_AGAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_NOT_INSTALLED
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_PACKAGE_NAME
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.DICAS_DE_SEGURANCA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIXEIRA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.linkpgtogerado.SpanHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_pagamento_por_link_gerado.*
import kotlinx.android.synthetic.main.fragment_pagamento_por_link_gerado_text_explanation.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author Enzo teles
 * */
class PgLinkDetailBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        val URL = "url"
        val PAYMENTE_LINK = "payment_link"
        val LISTARESUMO = "lista_resumo"
        val LINK_ACTIVE = 2
        val LINK_CREATE = 1
        val ISCHECK = "check"
        private const val GA_DELIVERY_TYPE = "GA_DELIVERY_TYPE"
        private const val SEND_PRODUCT = "Enviar um produto"
        private const val COBRAR_VALOR = "Cobrar valor"
        private const val PAYMENTLINK_DTO = "PAYMENTLINK_DTO"
        private var paymentLinkDTO: PaymentLinkDTO? = null

        fun newInstance(it: PaymentLink, link: Int = 0, listaResumo: Boolean = false,
                        paymentLinkDTO: PaymentLinkDTO?): PgLinkDetailBottomSheet {
            return PgLinkDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYMENTE_LINK, it)
                    putParcelable(PAYMENTLINK_DTO, paymentLinkDTO)
                    putInt(ISCHECK, link)
                    putBoolean(LISTARESUMO, listaResumo)
                }
            }
        }
    }

    //variable
    lateinit var paymentLink: PaymentLink
    var listaResumo: Boolean? = null
    var link: Int? = null
    lateinit var listener: OnPgLinckListener
    var onDismissListener: OnDismissListener? = null

    private val presenter: PgLinkDetailPresenter by inject {
        parametersOf(this)
    }


    interface OnDismissListener {
        fun onCloseLinkDetailBottomSheet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pagamento_por_link_gerado, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initOnClink()
        listener = presenter

        arguments?.let {
            paymentLink = it.getParcelable(PAYMENTE_LINK)!!
            paymentLinkDTO = it.getParcelable(PAYMENTLINK_DTO)
            link = it.getInt(ISCHECK)
            listaResumo = it.getBoolean(LISTARESUMO)
        }

        link?.let {
            img_link_sucess?.visible(it != LINK_ACTIVE)
            textViewLinkSucess?.visible(it != LINK_ACTIVE)
        }

        constraintSecond?.gone()

        //set object in the screen
        paymentLink?.run {
            link_value?.text = this.url
            productNameTextView?.text = this.name
            priceTextView?.text = getString(R.string.tv_value_link, price?.toPtBrRealString())
        }
        SpanHelper.setSpanOnText(textViewCallExplanation5, requireContext())
    }


    override fun onResume() {
        super.onResume()
        if (link == LINK_CREATE) {
            Analytics.trackScreenView(
                screenName = "/pagamento-por-link/super-link/criar-novo-link/link-gerado",
                screenClass = this.javaClass
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onCloseLinkDetailBottomSheet()
    }

    /**
     * set onClickListener
     * */
    private fun initOnClink() {
        btn_copy?.setOnClickListener(this)
        imageViewCopy?.setOnClickListener(this)
        btn_share?.setOnClickListener(this)
        btn_zap?.setOnClickListener(this)
        btn_close?.setOnClickListener(this)
        textViewCallExplanation5?.setOnClickListener(this)
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


    /**
     * Método que pega o click da tela de details
     * @param v
     * */
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_trash -> {
                gaSendWhatButton(LIXEIRA)
                gaSendDelete(productNameTextView.text.toString())
                dialogConfirmationDelete()
            }
            R.id.btn_copy, R.id.imageViewCopy -> {
                gaSendWhatButton(COPIAR)
                gaSendShare(productNameTextView.text.toString())
                copyToClipboard(link_value.text.toString())
            }
            R.id.btn_share -> {
                gaSendWhatButton(COMPARTILHAR)
                gaSendShare(productNameTextView.text.toString())
                shareDefaultDevice()
            }
            R.id.btn_zap -> {
                gaSendWhatButton(WHATSAPP)
                gaSendShare(productNameTextView.text.toString())
                verificationZapInDevice()
            }
            R.id.btn_close -> {
                gaSendWhatButton(FECHAR)
                dismiss()
            }
            R.id.textViewCallExplanation5 -> {
                gaSendWhatButton(DICAS_DE_SEGURANCA)
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
                if (isAttached()) {
                    deleteLInk()
                }
            }
            .setOnclickListenerLeft {
                alertDialogCustom?.show()?.dismiss()
            }
        alertDialogCustom?.show()
    }

    /**
     * método para verificar se o whatshapp está no devide
     * */
    private fun verificationZapInDevice() {
        if (!appInstalledOrNot(WHATSAPP_PACKAGE_NAME)) {
            // Toast message not installed.
            requireContext().showMessage(
                message = PLEASE_INSTALL_APP_AGAIN,
                title = WHATSAPP_NOT_INSTALLED
            )
        } else {
            sendMsgWhatSapp()
        }
    }

    /**
     * método que manda msg para o usuário via whatsapp
     * */
    private fun sendMsgWhatSapp() {
        val waIntent = Intent(Intent.ACTION_SEND)
        waIntent.type = TEXT_PLAIN
        val text = getString(R.string.payment_link_for, paymentLink.name, paymentLink.url)
        val pm = requireActivity().packageManager
        pm.getApplicationInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_META_DATA)
        waIntent.setPackage(WHATSAPP_PACKAGE_NAME)
        waIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(waIntent, SHARE_WITH))
    }

    /**
     * método para deletar um link da api
     * */
    private fun deleteLInk() {
        //verificando se o usuário está conectado com o wifi
        if (Utils.isNetworkAvailable(requireActivity())) {
            showProgress()
            link_value?.apply {
                paymentLink?.id?.let { itId ->
                    presenter.deleteLink(deleteLink = DeleteLink(itId), callback = {
                        if (isAttached()) {
                            statusHttpCode(it)
                        }
                    })
                }
            }
        } else {
            requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                    title = getString(R.string.title_error_wifi_title))
        }
    }

    /**
     * método mostrar o compartilhamento default do device
     * */
    private fun shareDefaultDevice() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)

        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, link_value.text.toString())
        startActivity(Intent.createChooser(intent, SHARE_WITH))
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
                successDeleleLink()
            }
            401 -> {
                requireContext().showMessage(message = getString(R.string.error_403))
            }
        }
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
     * interface
     * */
    interface OnPgLinckListener {
        fun deleteLink(deleteLink: DeleteLink, callback: (Int) -> Unit)
    }

    fun isAttached(): Boolean {
        return isAdded && activity != null && view != null
    }

    fun showProgress() {
        errorView?.gone()
        frame_progress_view?.visible()
        layout_pg_detail?.gone()
    }

    fun hideProgress() {
        errorView?.visible()
        frame_progress_view?.gone()
        layout_pg_detail?.gone()
    }

    /**
     * método que volta para tela de criação de link
     * */
    fun successDeleleLink() {
        dismiss()
        if (listaResumo == true) {
            requireActivity().onBackPressed()
        } else {
            onDismissListener?.onCloseLinkDetailBottomSheet()
        }
    }


    private fun gaSendShare(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(typeLink()),
                label = listOf(COMPARTILHAR, labelButton)
            )
        }
    }

    private fun gaSendDelete(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.EXCLUIR, typeLink()),
                label = listOf(Label.BOTAO, labelButton)
            )
        }
    }

    private fun typeLink(): String = link?.let {
        return if (it == LINK_CREATE) {
            "link gerardo"
        } else {
            "link para pagamento"
        }
    } ?: kotlin.run {
        return ""
    }

    private fun gaSendWhatButton(labelButton: String) {
        val list = ArrayList<String>()
        paymentLinkDTO?.let {
            if (it.typeSale.name
                            .equals(TypeSaleEnum.SEND_PRODUCT.name)) {
                list.add(SEND_PRODUCT)
                if (it.responsibleDelivery!!.equals(ResponsibleDeliveryEnum.CUSTOM)) {
                    list.add("Frete-fixo")
                } else if (it.responsibleDelivery!!.equals(ResponsibleDeliveryEnum.CORREIOS)) {
                    list.add("Correios")
                } else ""
            } else list.add(COBRAR_VALOR)
        }
        list.add(labelButton)
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.BOTAO, typeLink()),
            label = list
        )
    }
}