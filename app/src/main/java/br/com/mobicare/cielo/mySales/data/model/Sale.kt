package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sale(
        val id: String?,
        val transactionPixId: String?,
        val date: String?,
        val cardBrand: String?,
        val cardBrandDescription: String?,
        val paymentTypeCode: String?,
        val paymentType: String?,
        val amount: Double?,
        val truncatedCardNumber: String?,
        val terminal: String?,
        val authorizationCode: String?,
        val authorizationDate: String?,
        val status: String?,
        val channel:String?,
        // Inicialmente permite a alteração porque o endpoint de /sales não retorna esse valor
        var statusCode: Int?,

        val nsu: String?,
        val tid: String?,
        val paymentSolutionType: String?,
        val paymentSolutionCode: Int?,
        //Sale solution
        val paymentScheduleDate: String?,
        val paymentDate: String?,
        val grossAmount: Double?,
        val netAmount: Double?,
        val administrationFee: Double?,
        val cardBrandCode: Int?,
        val saleGrossAmount: Double?,
        @SerializedName("saleDate") val saleDate: String?,
        val merchantId: String?,
        val mdrFee: Double?,
        val mdrFeeAmount: Double?,
        val cardNumber: String?,
        val installments: Int?,
        val transactionId: String?,
        val paymentNode: Long?
) : Parcelable