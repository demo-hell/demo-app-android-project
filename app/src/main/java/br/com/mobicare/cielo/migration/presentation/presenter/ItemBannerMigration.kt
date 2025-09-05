package br.com.mobicare.cielo.migration.presentation.presenter

import android.graphics.drawable.Drawable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ItemBannerMigration(val firstName: String, val imageUrl: Drawable?, val id: Int)

data class ItemBannerIntroduce(val title: String, val subtitle: String, val imageUrl: Drawable?, val id: Int)

@Parcelize
data class ItemMenuGrid(
        val title: String?,
        val imageUrl: String?
) : Parcelable

@Parcelize
data class GenericMenuGrid(
        val items: ArrayList<ItemMenuGrid>
) : Parcelable