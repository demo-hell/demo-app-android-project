package br.com.mobicare.cielo.mfa.merchantstatus

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA
import br.com.mobicare.cielo.mfa.merchantstatus.FluxoNavegacaoMerchantStatusMfaActivity.Companion.MERCHANT_STATUS_MFA
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment

class MfaMerchantStatusRouterFragment : Fragment() {

    private var merchantStatusMFA: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    fun init() {
        arguments?.let {
            merchantStatusMFA = it.getString(MERCHANT_STATUS_MFA)
        }
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mfaMerchantStatusRouterFragment, true)
            .build()

        merchantStatusMFA?.let {
            when (it) {
                MerchantStatusMFA.NOT_ACTIVE.name,
                MerchantStatusMFA.EXPIRED.name -> {
                    findNavController()
                        .navigate(
                            MfaMerchantStatusRouterFragmentDirections
                                .actionMfaMerchantStatusRouterFragmentToMerchantValidateChallengeFragment(
                                    merchantStatusMFA
                                ), navOptions
                        )
                }
                MerchantStatusMFA.WAITING_ACTIVATION.name -> {
                    findNavController()
                        .navigate(
                            MfaMerchantStatusRouterFragmentDirections.actionMfaMerchantStatusRouterFragmentToMerchantChallengerActivationFragment(),
                            navOptions
                        )
                }
                MerchantStatusMFA.BLOCKED.name -> {
                    val bottomSheet = BottomSheetGenericFragment
                            .newInstance(
                                    getString(R.string.text_bank_mfa_status_blocked_toolbar),
                                    R.drawable.ic_07,
                                    getString(R.string.text_bank_mfa_status_blocked_toolbar),
                                    getString(R.string.text_bank_mfa_status_blocked_title),
                                    getString(R.string.incomint_fast_cancellation_back_button),
                                    statusBtnClose = false,
                                    statusBtnOk = true,
                                    statusViewLine = true,
                            )
                    bottomSheet.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnOk(dialog: Dialog) {
                            requireActivity().finish()
                        }

                        override fun onSwipeClosed() {
                            requireActivity().finish()
                        }

                    }
                    bottomSheet.isCancelable = false
                    bottomSheet.show(this.childFragmentManager, bottomSheet::class.java.simpleName)
                }
            }
        }
    }
}