package br.com.mobicare.cielo.openFinance.presentation.utils

import android.content.Context

object CheckStatus {
    fun getStatus(context: Context, status: String): StatusEnum {
        return when (status.lowercase()) {
            context.getString(StatusEnum.ACTIVE.status).lowercase() -> {
                StatusEnum.ACTIVE
            }
            context.getString(StatusEnum.TEMPORARILY_UNAVAILABLE.status).lowercase() -> {
                StatusEnum.TEMPORARILY_UNAVAILABLE
            }
            context.getString(StatusEnum.PENDING_AUTHORIZATION.status).lowercase() -> {
                StatusEnum.PENDING_AUTHORIZATION
            }
            context.getString(StatusEnum.CLOSED.status).lowercase() -> {
                StatusEnum.CLOSED
            }
            else -> StatusEnum.EXPIRED
        }
    }
}