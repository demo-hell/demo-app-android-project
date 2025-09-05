package br.com.mobicare.cielo.component.requireDataField.utils

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Field
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Order

object RequiredDataFieldFactory {

    const val orderId = "123456"

    val fields = listOf(
        Field(
            id = "name",
            value = "João"
        )
    )

    val order = Order()

}