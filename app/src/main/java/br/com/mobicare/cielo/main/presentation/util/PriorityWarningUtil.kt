package br.com.mobicare.cielo.main.presentation.util

import br.com.mobicare.cielo.commons.domains.entities.PriorityWarningVisualization

object PriorityWarningUtil {

    fun hasId(
        priorityWarningVisualization: List<PriorityWarningVisualization?>?,
        id: String,
        ec: String? = null
    ): Boolean {
        var hasId = false
        priorityWarningVisualization?.forEach { warning ->
            if (id == warning?.idModal) {
                if (ec == null || warning.ec == ec)
                    hasId = true
            }
        }
        return hasId
    }

    fun hasEC(
        priorityWarningVisualization: List<PriorityWarningVisualization?>?,
        ec: String?
    ): Boolean {
        var hasEc = false
        priorityWarningVisualization?.forEach { warning ->
            if (warning?.ec == ec) hasEc = true
        }
        return hasEc
    }
}