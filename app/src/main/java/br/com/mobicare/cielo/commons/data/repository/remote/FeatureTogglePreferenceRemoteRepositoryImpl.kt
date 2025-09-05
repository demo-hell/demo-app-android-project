package br.com.mobicare.cielo.commons.data.repository.remote

import br.com.mobicare.cielo.commons.data.dataSource.remote.FeatureTogglePreferenceRemoteDataSource
import br.com.mobicare.cielo.commons.domain.repository.remote.FeatureTogglePreferenceRemoteRepository

class FeatureTogglePreferenceRemoteRepositoryImpl(private val remoteDataSource: FeatureTogglePreferenceRemoteDataSource): FeatureTogglePreferenceRemoteRepository {
    override suspend fun getFeatureTogglePreference() = remoteDataSource.getFeatureTogglePreference()
}