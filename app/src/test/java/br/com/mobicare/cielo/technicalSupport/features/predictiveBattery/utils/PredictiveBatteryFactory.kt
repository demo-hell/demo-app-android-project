package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils

import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse

object PredictiveBatteryFactory {

    const val equipmentID = "12345678"
    const val phoneNumber = "(11) 99999-9999"
    const val batteryResponseID = "123456"

    val deepLinkModel = DeepLinkModel(
        id = DeeplinkFlowIdEnum.PREDICTIVE_BATTERY.id,
        params = hashMapOf(
            PredictiveBatteryConstants.PREDICTIVE_BATTERY_PARAM_URL_DEEP_LINK to equipmentID
        )
    )
    val deepLinkModelWithInvalidLogicalNumber = DeepLinkModel(
        id = DeeplinkFlowIdEnum.PREDICTIVE_BATTERY.id,
        params = hashMapOf()
    )

    val batteryRequestWithChargeBatteryIsTrue = BatteryRequest(
        equipmentId = equipmentID,
        chargeBattery = true,
        phone = phoneNumber
    )

    val batteryRequestWithChargeBatteryIsFalse = BatteryRequest(
        equipmentId = equipmentID,
        chargeBattery = false,
        phone = null
    )

    val batteryResponseSuccess = BatteryResponse(id = batteryResponseID)

}