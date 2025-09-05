package br.com.mobicare.cielo.mfa.resume

import br.com.mobicare.cielo.mfa.MfaAccount

interface InsertCNPJBottomSheetFragmentContract {
    fun verifyData(mfaAccount: MfaAccount)
    fun dismiss()
}