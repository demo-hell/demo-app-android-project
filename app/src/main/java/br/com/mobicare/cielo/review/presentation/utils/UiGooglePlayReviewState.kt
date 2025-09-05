package br.com.mobicare.cielo.review.presentation.utils

sealed class UiGooglePlayReviewState {
    object OnFinishPlayReview : UiGooglePlayReviewState()
    object OnUnableToShowPlayReview : UiGooglePlayReviewState()

}