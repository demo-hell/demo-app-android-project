package br.com.mobicare.cielo.centralDeAjuda.presentation.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class QuestionRequestModelView(
        val title: String?,
        val faqId: String,
        val subcategoryId: String,
        val questionId: String) : Parcelable