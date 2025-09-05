package br.com.mobicare.cielo.commons.data.dataSource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import br.com.mobicare.cielo.featureToggle.utils.saveFeatureToggleLocally
import br.com.mobicare.cielo.p2m.domain.model.TaxModel
import com.google.gson.Gson

class FeatureTogglePreferenceLocalDataSource(private val featureTogglePreference: FeatureTogglePreference) {

    fun getFeatureTogglePreference(key: String): CieloDataResult<Boolean> {
        return try {
            val value = featureTogglePreference.getFeatureToggleObject(key)?.show ?: false
            CieloDataResult.Success(value)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Success(value = false)
        }
    }

    fun getFeatureToggleMessage(key: String): CieloDataResult<TaxModel> {
        return try {
            val value = featureTogglePreference.getFeatureToggleObject(key)?.statusMessage
            CieloDataResult.Success(
                Gson().fromJson(
                    value,
                    TaxModel::class.java
                )
            )
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }

    fun getFeatureToggle(key: String): CieloDataResult<Feature> {
        return try {
            val value = featureTogglePreference.getFeatureToggleObject(key)
            value?.let {
                return CieloDataResult.Success(value)
            }
            return CieloDataResult.Empty()

        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }

    fun saveFeatureTogglePreference(featureToggleResponse: FeatureToggleResponse) {
        try {
            saveFeatureToggleLocally(featureToggleResponse.content)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
    }
}
