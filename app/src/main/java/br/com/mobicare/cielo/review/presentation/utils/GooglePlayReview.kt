package br.com.mobicare.cielo.review.presentation.utils

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

class GooglePlayReview {

    fun onRequestReview(
        context: Context,
        activity: Activity,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(activity, task.result).addOnCompleteListener {
                    onSuccess()
                }
            } else {
                onFail()
            }
        }
    }

}