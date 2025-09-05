package br.com.mobicare.cielo.commons.secure.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.DeviceInfoPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.secure.presentation.ui.component.inversePercentagem
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpContract
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpPresenter
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.mfa.token.TokenGeneratorActivity
import com.ca.mobile.riskminder.RMDeviceInventory
import com.ca.mobile.riskminder.RMDeviceInventoryImpl
import com.ca.mobile.riskminder.RMDeviceInventoryResponseCallBack
import com.ca.mobile.riskminder.RMError
import kotlinx.android.synthetic.main.fragment_otp_register.errorHandlerOtpRegister
import kotlinx.android.synthetic.main.fragment_otp_register.otpCodeView
import kotlinx.android.synthetic.main.layout_otp_code_view.pbCountDownView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OtpRegisterFragment : BaseFragment(), OtpContract.View, CieloNavigationListener {
    private var cieloNavigation: CieloNavigation? = null

    companion object {
        fun create(): OtpRegisterFragment {
            return OtpRegisterFragment()
        }
    }

    private val otpPresenter: OtpPresenter by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_otp_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureOptView()
        configureNavigation()
        initCollectDeviceDNA()

        otpPresenter.showOtpCodeForFirstTime()
    }

    private fun configureOptView() {
        this.pbCountDownView?.max = 100
        this.pbCountDownView?.progress = 0
    }

    private fun initCollectDeviceDNA() {
        val rmDevice = RMDeviceInventoryImpl
            .getDeviceInventoryInstance(requireContext(), RMDeviceInventory.DDNA_Mode.SDK)
        rmDevice.collectDeviceDNA(object : RMDeviceInventoryResponseCallBack {
            override fun deleteRMDeviceId() = Unit
            override fun storeRMDeviceId(p0: String?) = Unit
            override fun getRMDeviceId() = ""

            override fun onResponse(deviceDna: String?, p1: RMError?) {
                //TODO DNA do dispositivo
                deviceDna?.let { deviceDnaArg ->
                    DeviceInfoPreferences.saveDeviceDna(deviceDnaArg)
                }
            }
        })
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar("Token")
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(true)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    override fun onHelpButtonClicked() {
        findNavController()
            .navigate(
                OtpRegisterFragmentDirections
                    .actionOtpRegisterFragmentToFaqQuestionsFragment()
            )
        this.cieloNavigation?.showContent(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        otpPresenter.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        otpPresenter.onResume()
    }

    override fun showOtp(otpGenerated: String) {
        errorHandlerOtpRegister.gone()
        cieloNavigation?.showContent(true)

        if (isAttached()) {
            otpCodeView?.visible()
            otpCodeView?.setCode(otpGenerated)
        }
    }

    override fun updateCountdownAnimation(elapsedSlicePercent: Double) {
        if (isAttached()) {
            this.otpCodeView?.setProgress(inversePercentagem(elapsedSlicePercent, 1.0), 1.0)
        }
    }

    override fun errorOnOtpGeneration(@StringRes errorCustomMessage: Int) {
        this.cieloNavigation?.showContent(true)
        this.cieloNavigation?.showHelpButton(false)

        this.otpCodeView?.gone()

        errorHandlerOtpRegister?.apply {
            visible()
            errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            configureButtonLabel(getString(R.string.ok))
            cieloErrorTitle = getString(R.string.text_title_generic_error)
            cieloErrorMessage = getString(if (errorCustomMessage == 0) R.string.text_message_generic_error else errorCustomMessage)
            configureActionClickListener(View.OnClickListener {
                requireActivity().onBackPressed()
            })
        }
    }
}


