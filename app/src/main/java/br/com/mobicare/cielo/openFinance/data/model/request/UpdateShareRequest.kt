package br.com.mobicare.cielo.openFinance.data.model.request

import br.com.mobicare.cielo.openFinance.domain.model.DeadLine

data class UpdateShareRequest(
    val dataPermissions: List<DataPermission>,
    val deadLine: DeadLine?,
    val redirectUri: String
)
data class DataPermission(
    val permissionCode: String,
    val displayName: String,
    val permissionDescription: String
)
