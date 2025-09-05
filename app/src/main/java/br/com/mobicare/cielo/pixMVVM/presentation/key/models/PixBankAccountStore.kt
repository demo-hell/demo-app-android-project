package br.com.mobicare.cielo.pixMVVM.presentation.key.models

import android.os.Parcelable
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.commons.utils.CPF_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.mask
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBankAccountType
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBeneficiaryType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixBankAccountStore(
    val bank: PixTransferBank? = null,
    val bankAccountType: PixBankAccountType? = null,
    val bankBranchNumber: String? = null,
    val bankAccountNumber: String? = null,
    val bankAccountDigit: String? = null,
    val beneficiaryType: PixBeneficiaryType? = null,
    val documentNumber: String? = null,
    val recipientName: String? = null
) : Parcelable {

    val bankAccountNumberWithDigit get() = "$bankAccountNumber-$bankAccountDigit"

    val formattedDocumentNumber get() = documentNumber?.mask(
        if (beneficiaryType == PixBeneficiaryType.CPF) {
            CPF_MASK_FORMAT
        } else {
            CNPJ_MASK_COMPLETE_FORMAT
        }
    )

    val validateBank get() = bank != null

    val validateAccountType get() = bankAccountType != null

    val validateAccountData get() = run {
        listOf(
            bankBranchNumber,
            bankAccountNumber,
            bankAccountDigit
        ).none { it.isNullOrBlank() }
    }

    val validateDocument get() = run {
        (beneficiaryType == PixBeneficiaryType.CPF && ValidationUtils.isCPF(documentNumber)) ||
                (beneficiaryType == PixBeneficiaryType.CNPJ && ValidationUtils.isCNPJ(documentNumber))
    }

    val validateRecipient get() = recipientName.isNullOrBlank().not()

}