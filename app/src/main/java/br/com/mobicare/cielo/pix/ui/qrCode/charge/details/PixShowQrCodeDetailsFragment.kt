package br.com.mobicare.cielo.pix.ui.qrCode.charge.details

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentQrCodeDetailsBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.PIX_NULL_AMOUNT
import br.com.mobicare.cielo.pix.constants.PIX_QR_CODE_CHARGE_DETAILS
import br.com.mobicare.cielo.pix.domain.QRCodeChargeResponse

class PixShowQrCodeDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var _binding: FragmentQrCodeDetailsBinding? = null
    private val binding get() = _binding

    private val qrCodeDetails: QRCodeChargeResponse? by lazy {
        arguments?.getParcelable(PIX_QR_CODE_CHARGE_DETAILS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrCodeDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupView()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_pix_generated_qr_code))
            navigation?.showContainerButton(isShow = false)
            navigation?.showButton(isShow = false)
            navigation?.showFirstButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        setupDetails()
        setupQrCodeImage()
    }

    private fun setupDetails() {
        binding?.includeQrCodeDetails?.apply {
            qrCodeDetails?.let { itQrCodeResponse ->
                binding?.tvCode?.text = itQrCodeResponse.qrCodeString
                val amount = if (itQrCodeResponse.originalAmount == null)
                    PIX_NULL_AMOUNT else itQrCodeResponse.originalAmount.toPtBrRealString()

                tvPriceValue.text = amount
                tvNameValue.text = itQrCodeResponse.merchantName
                setQrcodeInformation(
                    groupQrCodeIdentifier,
                    tvIdentifierValue,
                    itQrCodeResponse.txId
                )
                tvDocumentTitle.visible(itQrCodeResponse.key != "")

                setQrcodeInformation(
                    groupQrCodeMessage,
                    tvMessageValue,
                    qrCodeDetails?.message
                )

                setQrcodeInformation(
                    groupQrCodeExpires,
                    tvQrCodeExpiresValue,
                    qrCodeDetails?.expirationDate?.dateFormatToBr()
                )

                tvDocumentValue.text = itQrCodeResponse.merchantDocument

            }
        }
    }

    private fun setQrcodeInformation(group: Group, value: TextView, data: String?) {
        data?.let {
            group.visible()
            value.text = data
        } ?: run {
            group.gone()
        }

    }

    private fun setupQrCodeImage() {
        if (qrCodeDetails?.qrCode?.isNotEmpty() == true) {
            val bitmap = qrCodeToBitmap()

            binding?.ivQrCodeImage?.apply {
                setImageBitmap(bitmap)
                contentDescription = getString(R.string.text_pix_generated_qr_code)
            }

        } else {
            binding?.ivQrCodeImage?.apply {
                setImageResource(R.drawable.img_qr_code_not_found)
                contentDescription = getString(R.string.qr_code_image_content_description)
            }
        }
    }

    private fun qrCodeToBitmap(): Bitmap? {
        if (qrCodeDetails?.qrCode.isNullOrEmpty()) {
            return null
        }
        val decodedBytes = Base64.decode(qrCodeDetails?.qrCode, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(decodedBytes, ZERO, decodedBytes.size)
    }

    private fun setupListeners() {
        qrCodeDetails?.qrCodeString?.let { itCode ->
            binding?.clCode?.setOnClickListener {
                copyCode(itCode)
            }

            binding?.clContainerShare?.setOnClickListener {
                showConfirmShareBottomSheet(itCode, qrCodeToBitmap())
            }
        }
    }

    private fun copyCode(code: String) {
        Utils.copyToClipboard(requireContext(), code, showMessage = false)
        Toast(requireContext()).showCustomToast(
            message = getString(R.string.text_pix_generated_qr_code_copy),
            activity = requireActivity(),
            trailingIcon = R.drawable.ic_check_toast
        )
    }

    private fun showConfirmShareBottomSheet(code: String, bitmap: Bitmap?) {
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_confirm_flow,
            getString(R.string.text_pix_share_your_qr_code_bs_title),
            getString(R.string.text_pix_share_your_qr_code_bs_subtitle),
            getString(R.string.text_pix_share_your_qr_code_bs_button_img),
            getString(R.string.text_pix_share_your_qr_code_bs_button_code),
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    super.onBtnFirst(dialog)
                    bitmap?.let { shareBitmap(it, requireContext()) }
                }

                override fun onBtnSecond(dialog: Dialog) {
                    super.onBtnSecond(dialog)
                    shareText(code, requireContext())
                }

                override fun onSwipeClosed() {
                    dismiss()
                }

                override fun onCancel() {
                    dismiss()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }
}