package br.com.mobicare.cielo.featureToggle.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeatureToggleModal(
        val id: String,
        val name: String,
        val imageUrl: String?,
        val title: String,
        val message: String,
        val external: Boolean,
        val actionUrl: String?,
        val actionTitle: String?,
        val stickyModal: Boolean,
        val loggedModal: Boolean
) : Parcelable