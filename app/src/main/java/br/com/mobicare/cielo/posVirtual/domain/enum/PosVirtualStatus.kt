package br.com.mobicare.cielo.posVirtual.domain.enum

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PosVirtualStatus(@StringRes val label: Int) {
    PENDING(R.string.pos_virtual_text_pending),
    SUCCESS(R.string.pos_virtual_text_success),
    FAILED(R.string.pos_virtual_text_failed),
    CANCELED(R.string.pos_virtual_text_canceled);

    companion object {
        fun find(value: String?) = values().firstOrNull { it.name == value }
    }

}