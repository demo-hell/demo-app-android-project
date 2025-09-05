package br.com.mobicare.cielo.meuCadastroDomicilio.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomicilioFlagVo(
        val code: Int,
        val imgSource: String,
        val name: String,
        var checked: Boolean,
        val bank: DomicilioBankVo? = null
) : Parcelable