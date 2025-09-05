package br.com.mobicare.cielo.featureToggle.utils

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MODAL_DINAMICA
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggle
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson

fun saveFeatureToggleLocally(featureToggles: List<FeatureToggle>) {
    FeatureTogglePreference.instance.saveAllFeatureToggles(Gson().toJson(featureToggles))

    featureToggles.forEach { feature ->
        FeatureTogglePreference.instance.saveFeatureTogle(
            feature.featureName,
            Feature(
                feature.featureName,
                feature.show == true,
                feature.status,
                feature.statusMessage
            )
        )

        if (feature.featureName == MODAL_DINAMICA && feature.show == true)
            try {
                FeatureTogglePreference.instance.saveFeatureToggleModal(
                    Gson().fromJson(
                        feature.statusMessage,
                        FeatureToggleModal::class.java
                    )
                )
            } catch (ex: Exception) {
                ex.message?.let { error ->
                    FirebaseCrashlytics.getInstance().log(error)
                }
            }
    }
}