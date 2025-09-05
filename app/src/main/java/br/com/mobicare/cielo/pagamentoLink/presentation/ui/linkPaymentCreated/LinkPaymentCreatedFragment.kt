package br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action.COMPARTILHAR
import br.com.mobicare.cielo.commons.analytics.Action.COPIAR
import br.com.mobicare.cielo.commons.analytics.Label.WHATSAPP
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.constants.IntentAction.TEXT_PLAIN
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.WhatsApp.PLEASE_INSTALL_APP_AGAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_NOT_INSTALLED
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_PACKAGE_NAME
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.WebviewActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentLinkPaymentCreatedBinding
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.linkpgtogerado.SpanHelper
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkAF
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.CLIENT_SCREEN
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.LINK_GENERATE
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.DICAS_DE_SEGURANCA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.FRETE_FIXO
import br.com.mobicare.cielo.superlink.utils.SuperLinkNavStartRouter
import org.koin.android.ext.android.inject

class LinkPaymentCreatedFragment : BaseFragment(), CieloNavigationListener, View.OnClickListener {

    private var _binding: FragmentLinkPaymentCreatedBinding? = null
    private val binding get() = _binding!!

    private val navStartRouter: SuperLinkNavStartRouter by inject()
    private val presenter: LinkPaymentCreatedPresenter by inject()

    private var navigation: CieloNavigation? = null

    private val analytics: SuperLinkAnalytics by inject()
    private val ga4: PaymentLinkGA4 by inject()
    private val screenPath
        get() = presenter.paymentLinkDto?.typeSale?.screenPath?.let { it + LINK_GENERATE }
            ?: Text.EMPTY
    private val screenPathClientScreen
        get() = presenter.paymentLinkDto?.typeSale?.screenPath?.let { it + CLIENT_SCREEN }
            ?: Text.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            presenter.apply {
                paymentLink = it.parcelable(PAYMENT_LINK)
                paymentLinkDto = it.parcelable(PAYMENT_LINK_DTO)
                setLinkType(it.getString(IS_CHECK))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLinkPaymentCreatedBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        configureListeners()
        configureValues()
        configureInformationText()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackButtonClicked(): Boolean {
        return if (navStartRouter.isFlowOriginFromPosVirtual) {
            requireActivity().finish()
            true
        } else false
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setTextToolbar(getString(R.string.text_super_link))
                it.showHelpButton(false)
                it.showButton(false)
                it.showContent(true)
                it.setNavigationListener(this)
                it.getDataIntent()
                    ?.getStringExtra(SuperLinkNavStartRouter.FlowStartArg.KEY)
                    ?.let { flowStartOrigin -> navStartRouter.setFlowStartOrigin(flowStartOrigin) }
            }
        }
    }

    private fun configureValues() {
        presenter.paymentLink?.let {
            logPurchase()

            binding.apply {
                linkValue.text = it.url
                productName.text = it.name
                productPrice.text = getString(R.string.tv_value_link, it.price?.toPtBrRealString())
            }
        }
    }

    private fun configureInformationText() {
        SpanHelper.setSpanOnText(binding.infoText, requireContext())
    }

    private fun configureListeners() {
        binding.apply {
            copyArea.setOnClickListener(::onCopyAreaClick)
            btnShare.setOnClickListener(::onShareClick)
            btnWhatsapp.setOnClickListener(::onWhatsAppClick)
            infoText.setOnClickListener(this@LinkPaymentCreatedFragment)
            btnGeneratedLinks.setOnClickListener(::onGeneratedLinksClick)
            seeLink.setOnClickListener(::onSeeLinkClick)
        }
    }

    private fun onCopyAreaClick(view: View) {
        gaSendWhatButton(COPIAR)
        gaSendShare(binding.productName.text.toString())
        copyToClipboard(binding.linkValue.text.toString())
    }

    private fun onShareClick(view: View) {
        gaSendWhatButton(COMPARTILHAR)
        gaSendShare(binding.productName.text.toString())
        shareDefaultDevice()
    }

    private fun onWhatsAppClick(view: View) {
        gaSendWhatButton(WHATSAPP)
        gaSendShare(binding.productName.text.toString())
        checkIfWhatsappIsInstalled()
    }

    private fun onSeeLinkClick(view: View) {
        startActivity(
            Intent(activity, WebviewActivity::class.java).apply {
                putExtra(WebviewActivity.URL, presenter.paymentLink?.url)
                putExtra(WebviewActivity.SCREEN_NAME, SuperLinkAnalytics.GENERATED_LINK)
                putExtra(WebviewActivity.SCREEN_PATH_GA4, screenPathClientScreen)
                putExtra(WebviewActivity.TITLE, getString(R.string.text_super_link))
            }
        )
    }

    private fun onGeneratedLinksClick(view: View) {
        findNavController()
            .navigate(LinkPaymentCreatedFragmentDirections.actionCreateLinkPaymentCreatedToLinkPaymentFragment())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewCallExplanation5 -> gaSendWhatButton(DICAS_DE_SEGURANCA)
        }
    }

    private fun checkIfWhatsappIsInstalled() {
        if (isAppInstalled(WHATSAPP_PACKAGE_NAME))
            sendWhatsAppMessage()
        else
            requireContext().showMessage(
                message = PLEASE_INSTALL_APP_AGAIN,
                title = WHATSAPP_NOT_INSTALLED
            )
    }

    private fun sendWhatsAppMessage() {
        val waIntent = Intent(Intent.ACTION_SEND)
        waIntent.type = TEXT_PLAIN
        val text = getString(
            R.string.payment_link_for,
            presenter.paymentLink?.name,
            presenter.paymentLink?.url
        )
        val pm = requireActivity().packageManager
        pm.getApplicationInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_META_DATA)
        waIntent.setPackage(WHATSAPP_PACKAGE_NAME)
        waIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(waIntent, SHARE_WITH))
    }

    private fun shareDefaultDevice() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = TEXT_PLAIN
        intent.putExtra(Intent.EXTRA_TEXT, binding.linkValue.text.toString())
        startActivity(Intent.createChooser(intent, getString(R.string.to_share_capitalize)))
    }

    private fun copyToClipboard(copyText: String) {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(URL, copyText)

        clipboard.setPrimaryClip(clip)

        Toast.makeText(activity, R.string.link_copiado, Toast.LENGTH_SHORT).show()
    }

    private fun isAppInstalled(uri: String): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun gaSendShare(labelButton: String) {
        if (isAttached()) analytics.sendGaShare(labelButton, presenter.linkTypeTag)
    }

    private fun gaSendWhatButton(labelButton: String) {
        val labelList = ArrayList<String>()
        presenter.paymentLinkDto?.let { paymentLinkDto ->
            if (paymentLinkDto.typeSale == TypeSaleEnum.SEND_PRODUCT) {
                labelList.add(getString(R.string.send_a_product))
                when (paymentLinkDto.responsibleDelivery) {
                    ResponsibleDeliveryEnum.CUSTOM -> labelList.add(FRETE_FIXO)
                    ResponsibleDeliveryEnum.CORREIOS -> labelList.add(getString(R.string.correios))
                    else -> EMPTY
                }
            } else labelList.add(getString(R.string.charge_value))
        }
        labelList.add(labelButton)
        analytics.sendGaButton(labelList, presenter.linkTypeTag)
    }

    private fun logScreenView() {
        analytics.sendGaScreenView(SuperLinkAnalytics.GENERATED_LINK_SCREEN)
        ga4.logScreenView(screenPath)
        PaymentLinkAF.logLinkCreatedScreenView()
    }

    private fun logPurchase() {
        presenter.paymentLink?.price?.let {
            ga4.logPurchaseCreatedLink(screenPath, it)
        }
    }

    companion object {
        const val URL = "url"

        private const val PAYMENT_LINK_DTO = "PAYMENT_LINK_DTO_ARGS"
        private const val PAYMENT_LINK = "PAYMENT_LINK_ARGS"
        private const val IS_CHECK = "ISCHECK_ARGS"
    }

}