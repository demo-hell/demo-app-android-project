package br.com.mobicare.cielo.arv.presentation.model.enum

import br.com.mobicare.cielo.R

enum class ReceivableStatusEnum(val status: String) {

    EFFECTIVE("Efetivada") {
        override fun getColor() = R.color.success_400
        override fun getIcon() = R.drawable.ic_check_round_success_400
    },
    PAYMENT("Enviada para pagamento") {
        override fun getColor() = R.color.alert_500
        override fun getIcon() = R.drawable.ic_clock_alert_500
    },
    PROCESSING("Em processamento") {
        override fun getColor() = R.color.alert_500
        override fun getIcon() = R.drawable.ic_clock_alert_500
    },
    PENDING("Pendente") {
        override fun getColor() = R.color.alert_500
        override fun getIcon() = R.drawable.ic_clock_alert_500
    },
    ERROR("Erro registradora") {
        override fun getColor() = R.color.danger_400
        override fun getIcon() = R.drawable.ic_error_around_danger_400
    },
    ERROR_REGISTER("Erro na registradora") {
        override fun getColor() = R.color.danger_400
        override fun getIcon() = R.drawable.ic_error_around_danger_400
    },
    REGISTRATION("Enviado para registro") {
        override fun getColor() = R.color.alert_500
        override fun getIcon() = R.drawable.ic_clock_alert_500
    },
    CANCELLATION_REQUEST("Cancelamento solicitado") {
        override fun getColor() = R.color.alert_500
        override fun getIcon() = R.drawable.ic_clock_alert_500
    },
    REJECTED("Recusada") {
        override fun getColor() = R.color.danger_400
        override fun getIcon() = R.drawable.ic_error_around_danger_400
    },
    CANCELED("Cancelamento efetivado") {
        override fun getColor() = R.color.success_400
        override fun getIcon() = R.drawable.ic_check_round_success_400
    },
    CANCELLATION_ERROR("Cancelamento não efetivado") {
        override fun getColor() = R.color.danger_400
        override fun getIcon() = R.drawable.ic_error_around_danger_400
    },
    INTERNAL_ERROR("Erro interno") {
        override fun getColor() = R.color.danger_400
        override fun getIcon() = R.drawable.ic_error_around_danger_400
    };

    abstract fun getColor(): Int
    abstract fun getIcon(): Int
}