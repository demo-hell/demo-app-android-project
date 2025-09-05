package br.com.mobicare.cielo.centralDeAjuda.presentation.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionDetailModelView(val question: String?, val answer: String?, val id: String,
                                   val videoLink: String?) : Parcelable