package br.com.mobicare.cielo.featureToggle.domain

/**
 * Created by mbello on 8/30/17.
 */


class FeatureToggleObj(
        var features: List<Feature>? = null
)

data class Feature(
        var featureName: String? = null,
        var show: Boolean = false,
        var status: String? = null,
        var statusMessage: String? = null)