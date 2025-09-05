package br.com.mobicare.cielo.machine.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MachineItemOfferResponse(
        val id: Int,
        val title: String,
        val priority: Int,
        val model: String,
        val technology: String,
        val rentalAmount: Double,
        val advertisement: String,
        val description: String,
        val imageUrl: String,
        val paymentCondition: String,
        val notification: String,
        val allowedQuantity: Int
        //val linkUrl: String,
        //val prefixoAmount: String,
        //val paymentConditionURL: Boolean,
        //val allowedQuantity: Int,
        //val paymentPlans : List<MachinePaymentPlansResponse>
) : Parcelable