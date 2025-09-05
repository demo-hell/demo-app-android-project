package br.com.mobicare.cielo.accessManager.data.mapper

import br.com.mobicare.cielo.accessManager.data.model.response.CustomProfileResourcesResponse
import br.com.mobicare.cielo.accessManager.data.model.response.GetCustomActiveProfilesResponse
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfileResources
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles

fun GetCustomActiveProfilesResponse.toCustomProfiles() =
    CustomProfiles(
        id = this.id,
        name = this.name,
        description = this.description,
        global = this.global,
        status = this.status,
        resources = this.resources?.map { it.toCustomProfilesResources() }
    )

fun CustomProfileResourcesResponse.toCustomProfilesResources() =
    CustomProfileResources(
        resourceId = this.resourceId,
        resourceName = this.resourceName,
        accessTypes = this.accessTypes
    )
