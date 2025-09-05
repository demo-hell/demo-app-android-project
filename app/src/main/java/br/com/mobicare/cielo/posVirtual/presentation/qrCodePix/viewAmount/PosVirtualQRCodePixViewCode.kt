package br.com.mobicare.cielo.posVirtual.presentation.qrCodePix.viewAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloButtonListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.QRCodePixUtils.copyQRCodePix
import br.com.mobicare.cielo.commons.utils.QRCodePixUtils.qrCodeBase64ToBitmap
import br.com.mobicare.cielo.databinding.FragmentPosVirtualQrCodeViewAmountBinding
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.SHARE_CODE
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.SHARE_IMAGE
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import org.koin.android.ext.android.inject

class PosVirtualQRCodePixViewCode : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPosVirtualQrCodeViewAmountBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: PosVirtualQRCodePixViewCodeArgs by navArgs()

    private val qrCodeResponse: PosVirtualCreateQRCodeResponse? by lazy {
        args.posvirtualqrcodepixargs
    }

    private val ga4: PosVirtualAnalytics by inject()
    private val screenPath: String get() = PosVirtualAnalytics.SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPosVirtualQrCodeViewAmountBinding.inflate(
        inflater,
        container,
        false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logScreenView()
        logPurchase()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupView()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@PosVirtualQRCodePixViewCode)
                showButton(false)
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        isExpanded = false,
                        disableExpandableMode = true,
                        showBackButton = false,
                        toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                            menuRes = R.menu.menu_close,
                            onOptionsItemSelected = {
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        )
                    )
                )
            }
        }
    }

    private fun setupView() {
        qrCodeResponse?.let { qrCode ->
            binding?.apply {
                tvQRCodeString.text = qrCode.qrCodeString

                includeQrCodeDetails.apply {
                    tvAmount.text = qrCode.amount?.toPtBrRealString()
                    tvAmount.contentDescription =
                        AccessibilityUtils.convertAmount(
                            qrCode.amount ?: ZERO_DOUBLE,
                            requireContext()
                        )
                    tvName.text = qrCode.merchantName ?: EMPTY
                    tvDoc.text = addMaskCPForCNPJ(
                        qrCode.merchantDocument,
                        getString(R.string.mask_cnpj_step4)
                    )
                }

                qrCodeBase64ToBitmap(qrCode.qrCodeBase64)?.let {
                    binding?.ivQrCodeImage?.apply {
                        setImageBitmap(it)
                        contentDescription = getString(R.string.text_pix_generated_qr_code)
                    }
                } ?: logExceptionLoadQRCode()
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            qrCodeResponse?.qrCodeString?.let { code ->
                tvQRCodeString.setOnClickListener {
                    logShareFromCopyCode()
                    copyQRCodePix(requireContext(), code)
                }

                tvShareQRCode.setOnClickListener {
                    logClickShareButton()
                    showBottomSheetShareQRCodePix()
                }
            }
        }
    }

    private fun showBottomSheetShareQRCodePix() {
        CieloButtonListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.pos_virtual_share_qr_code_title)
            ),
            buttons = listOf(
                CieloButtonListBottomSheet.Button(
                    id = ONE,
                    text = getString(R.string.pos_virtual_share_qr_code_button_image),
                    drawableRes = R.drawable.ic_payments_qr_code_scan_cloud_300_24_dp,
                ),
                CieloButtonListBottomSheet.Button(
                    id = TWO,
                    text = getString(R.string.pos_virtual_share_qr_code_button_code),
                    drawableRes = R.drawable.ic_multimidia_share_brand_400_16_dp,
                )
            ),
            onButtonItemClicked = { button, bs ->
                when (button.id) {
                    ONE -> sharePixImage()
                    TWO -> sharePixCode()
                }
                bs.dismiss()
            }
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun sharePixImage() {
        qrCodeBase64ToBitmap(qrCodeResponse?.qrCodeBase64)?.let {
            logShareFromShareButton(SHARE_IMAGE)
            shareBitmap(it, requireContext())
        }
    }

    private fun sharePixCode() {
        qrCodeResponse?.qrCodeString?.let {
            logShareFromShareButton(SHARE_CODE)
            shareText(it, requireContext())
        }
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logPurchase() = qrCodeResponse?.amount?.let {
        ga4.logPurchaseGenerateQRCodePix(it)
    }

    private fun logShareFromCopyCode() = ga4.logShareQRCodePixFromCopyCode()

    private fun logClickShareButton() = ga4.logClickShareButton()

    private fun logShareFromShareButton(typeShare: String) =
        ga4.logShareQRCodePixFromShareButton(typeShare)

    private fun logExceptionLoadQRCode() = ga4.logExceptionLoadQRCodePix()

}