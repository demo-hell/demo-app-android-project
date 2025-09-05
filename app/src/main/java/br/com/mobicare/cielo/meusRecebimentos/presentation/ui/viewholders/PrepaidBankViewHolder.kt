package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.viewholders

import android.text.SpannableStringBuilder
import android.view.View
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankDataObj
import kotlinx.android.synthetic.main.item_prepaid_card_bank.view.*

class PrepaidBankViewHolder(val view: View) : DefaultViewHolderKotlin(view) {

    fun bind(bankDataObj: BankDataObj) {

        //BindingUtils.loadImage(view.imagePrepaidBankLogo, bankDataObj.imgUrl)

        view.textPrepaidBalance.text = SpannableStringBuilder.valueOf(Utils
                .formatValue(bankDataObj.amount?.toDouble() as Double))

        view.textPrepaidStatementCount.text = SpannableStringBuilder
                .valueOf(bankDataObj.postingsQty)

        bankDataObj.status?.run {
            view.textOperationLabel.text = SpannableStringBuilder.valueOf(this)
            super.changeColorStatus(this, view.textOperationLabel)
        }

    }

}