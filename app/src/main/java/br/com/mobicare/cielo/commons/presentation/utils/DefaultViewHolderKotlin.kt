package br.com.mobicare.cielo.commons.presentation.utils

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankStatus

/**
 * Created by Benhur on 02/06/17.
 */

open class DefaultViewHolderKotlin(var mView: View): RecyclerView.ViewHolder(mView) {

    fun changeColorStatus(status: String, textStatus: TypefaceTextView) {
        when (status) {
            BankStatus.DE.status, BankStatus.PA.status -> {
                textStatus.setTextColor(ContextCompat.getColor(mView.context, R.color.green))
            }
            BankStatus.PR.status, BankStatus.AG.status, BankStatus.AN.status, BankStatus.CA.status -> {
                textStatus.setTextColor(ContextCompat.getColor(mView.context, R.color.gray_light))
            }
            BankStatus.PE.status -> {
                textStatus.setTextColor(ContextCompat.getColor(mView.context, R.color.yellow))
            }
        }
    }
}
