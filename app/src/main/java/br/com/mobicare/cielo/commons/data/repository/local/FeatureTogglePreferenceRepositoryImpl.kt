package br.com.mobicare.cielo.commons.data.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.dataSource.local.FeatureTogglePreferenceLocalDataSource
import br.com.mobicare.cielo.commons.data.dataSource.remote.FeatureTogglePreferenceRemoteDataSource
import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse

class FeatureTogglePreferenceRepositoryImpl(
    private val localDataSource: FeatureTogglePreferenceLocalDataSource,
    private val remoteDataSource: FeatureTogglePreferenceRemoteDataSource
) : FeatureTogglePreferenceRepository {

    override suspend fun getFeatureTogglePreference(
        key: String,
        isLocal: Boolean
    ): CieloDataResult<Boolean> {
        return if (isLocal) {
            getFeatureToggleLocal(key)
        } else {
            getFeatureToggleRemote(key)
        }
    }

    private suspend fun getFeatureToggleLocal(key: String): CieloDataResult<Boolean> {
        val localResult = localDataSource.getFeatureTogglePreference(key)

        localResult.onSuccess {
            return localResult
        }

        return getFeatureToggleRemote(key)
    }

    private suspend fun getFeatureToggleRemote(key: String): CieloDataResult<Boolean> {
        val remoteResult = remoteDataSource.getFeatureTogglePreference()

        remoteResult.onSuccess { ftResponse ->
            saveFeatureTogglePreference(ftResponse)

            val featureToggle = ftResponse.content.firstOrNull { key == it.featureName }

            return CieloDataResult.Success(featureToggle?.show ?: false)
        }

        return CieloDataResult.Empty()
    }

    override suspend fun getFeatureToggleMessage(key: String) = localDataSource.getFeatureToggleMessage(key)

    override suspend fun
            getFeatureToggle(key: String): CieloDataResult<Feature> = localDataSource.getFeatureToggle(key)

    override suspend fun saveFeatureTogglePreference(featureToggleResponse: FeatureToggleResponse) =
        localDataSource.saveFeatureTogglePreference(featureToggleResponse)
}