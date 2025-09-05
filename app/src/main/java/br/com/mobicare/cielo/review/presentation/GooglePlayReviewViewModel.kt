package br.com.mobicare.cielo.review.presentation

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.arv.utils.UiArvOnboardingState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.GOOGLE_PLAY_REVIEW
import br.com.mobicare.cielo.review.presentation.utils.GooglePlayReview
import br.com.mobicare.cielo.review.presentation.utils.UiGooglePlayReviewState
import kotlinx.coroutines.launch

class GooglePlayReviewViewModel(
    private val googlePlayReview: GooglePlayReview,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase,
    private val getUserViewHistory: GetUserViewHistoryUseCase,
    private val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase
) : ViewModel() {

    private val _googlePlayReviewLiveData = MutableLiveData<UiGooglePlayReviewState>()
    val googlePlayReviewLiveData: LiveData<UiGooglePlayReviewState> get() = _googlePlayReviewLiveData

    fun onRequestReview(
        context: Context,
        activity: Activity,
        featureToggleFlowKey: String = EMPTY,
        featureKey: String = EMPTY,
        isCheckCache: Boolean = false,
        isFeatureToggleFlow: Boolean = false
    ) {
        viewModelScope.launch {
            getFeatureTogglePreference(key = GOOGLE_PLAY_REVIEW).onSuccess { isShow ->
                when {
                    isShow.not() -> onUnableToShowPlayReview()

                    isCheckCache && isFeatureToggleFlow.not() -> onCheckCache(
                        context,
                        activity,
                        featureKey
                    )

                    isCheckCache.not() && isFeatureToggleFlow.not() -> onShowPlayReview(
                        context,
                        activity,
                        featureKey
                    )

                    else -> onCheckFeatureToggleFlow(
                        context,
                        activity,
                        featureToggleFlowKey,
                        featureKey,
                        isCheckCache
                    )
                }
            }
        }
    }

    private fun onCheckFeatureToggleFlow(
        context: Context,
        activity: Activity,
        featureToggleKey: String,
        featureKey: String,
        isCheckCache: Boolean
    ) {
        viewModelScope.launch {
            getFeatureTogglePreference(key = featureToggleKey).onSuccess { isShow ->
                when {
                    isShow && isCheckCache -> onCheckCache(
                        context,
                        activity,
                        featureKey
                    )

                    isShow && isCheckCache.not() -> onShowPlayReview(context, activity, featureKey)

                    else -> onUnableToShowPlayReview()
                }
            }
        }
    }

    private suspend fun onCheckCache(
        context: Context,
        activity: Activity,
        featureKey: String
    ) {
        getUserViewHistory(key = featureKey)
            .onSuccess { isShow ->
                if (isShow) {
                    onUnableToShowPlayReview()
                } else {
                    onShowPlayReview(context, activity, featureKey)
                }
            }.onEmpty {
                onShowPlayReview(context, activity, featureKey)
            }
    }

    private fun onShowPlayReview(context: Context, activity: Activity, featureKey: String) {
        googlePlayReview.onRequestReview(
            context = context,
            activity = activity,
            onSuccess = {
                if (featureKey.isNullOrEmpty()) {
                    onFinishReview()
                } else {
                    onSaveReview(featureKey)
                }
            },
            onFail = {
                onUnableToShowPlayReview()
            }
        )
    }

    private fun onSaveReview(featureKey: String) {
        viewModelScope.launch {
            saveUserViewHistoryUseCase(key = featureKey)
                .onSuccess {
                    onFinishReview()
                }.onEmpty {
                    onFinishReview()
                }
        }
    }

    private fun onFinishReview() {
        _googlePlayReviewLiveData.value = UiGooglePlayReviewState.OnFinishPlayReview
    }

    private fun onUnableToShowPlayReview() {
        _googlePlayReviewLiveData.value = UiGooglePlayReviewState.OnUnableToShowPlayReview
    }
}