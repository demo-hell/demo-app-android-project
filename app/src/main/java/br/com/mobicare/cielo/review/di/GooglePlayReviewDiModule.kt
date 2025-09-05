package br.com.mobicare.cielo.review.di

import br.com.mobicare.cielo.review.presentation.GooglePlayReviewViewModel
import br.com.mobicare.cielo.review.presentation.utils.GooglePlayReview
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val googlePlayReviewViewModelModule = module {
    viewModel { GooglePlayReviewViewModel(get(), get(), get(), get()) }
}

val googlePlayReviewUtils = module {
    factory(name = "GooglePlayReviewUtil") {
        GooglePlayReview()
    }
}

val googlePlayReviewModulesList = listOf(
    googlePlayReviewViewModelModule,
    googlePlayReviewUtils
)