package br.com.mobicare.cielo.pagamentoLink.domain

data class PaginationPaymentLink (
        val pageNumber: Int,
        val pageSize: Int,
        val totalElements: Int,
        val firstPage: Boolean,
        val lastPage: Boolean,
        val numPages: Int
)