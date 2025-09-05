package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod

abstract class PixReceiptMethodListViewBuilder(
    private val context: Context,
    private val layoutInflater: LayoutInflater
) {

    abstract fun build(): List<View>

    protected fun buildCardView(
        receiptMethod: PixReceiptMethod,
        isActiveModel: Boolean = false,
        scheduleHourList: List<String>? = null,
        onTap: ((PixReceiptMethod) -> Unit)? = null
    ) = PixReceiptMethodCardViewBuilder(
            layoutInflater = layoutInflater,
            data = PixReceiptMethodCardViewBuilder.Data(
                title = getString(receiptMethod.titleRes),
                description = getString(receiptMethod.descriptionRes),
                iconRes = receiptMethod.iconRes,
                isActiveModel = isActiveModel,
                scheduleHourList = scheduleHourList,
                onTap = if (onTap != null) {{ onTap(receiptMethod) }} else null
            )
        ).build()

    protected fun getString(@StringRes stringRes: Int) = context.getString(stringRes)
}