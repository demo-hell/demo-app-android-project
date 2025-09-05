package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAuthorizationStatusResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus
import com.google.gson.Gson

object PixAuthorizationStatusFactory {

    private val jsonWithPendingStatus = """
        {
           "status": "PENDING",
           "beginTime": "2023-08-30T15:30:00.000Z"
        }
    """.trimIndent()

    val responseWithPendingStatus: PixAuthorizationStatusResponse =
        Gson().fromJson(jsonWithPendingStatus, PixAuthorizationStatusResponse::class.java)

    val entityWithPendingStatus = responseWithPendingStatus.toEntity()
}