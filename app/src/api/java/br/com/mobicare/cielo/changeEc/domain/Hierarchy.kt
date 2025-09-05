package br.com.mobicare.cielo.changeEc.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hierarchy(
        val clienteIndividual: Boolean,
        val formaRecebimento: String,
        val id: String, // EC
        val matrizPagamentoEnabled: Boolean,
        val nivelHierarquia: String,
        val noHierarquia: String,
        val nome: String,
        val nomeFantasia: String,
        val nomeHierarquia: String,
        val tipoPessoa: String,
        val cnpj: CnpjHierarchy) : Parcelable

@Parcelize
data class CnpjHierarchy(
        val raiz: String,
        val completo: String
) : Parcelable