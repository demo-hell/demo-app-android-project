package br.com.mobicare.cielo.minhasVendas.enums

import androidx.annotation.ColorRes
import br.com.mobicare.cielo.R

enum class CanceledSalesStatusEnum(val value: String, @ColorRes val color: Int) {
    REVIEW("Em análise", R.color.alert_400),
    EFFECTIVE("Efetivado", R.color.color_009E55),
    REVERSED("Revertido", R.color.color_cloud_600),
    REJECTED("Rejeitado", R.color.danger_400);

    companion object {
        fun getColor(status: String): Int {
            return values().firstOrNull { it.value == status }?.color ?: R.color.color_cloud_600
        }
    }

}