package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPixExtractPageState {
    object ShowLoading : UIPixExtractPageState()

    object ShowLoadingMoreTransactions : UIPixExtractPageState()

    object HideLoading : UIPixExtractPageState()

    object HideLoadingSwipe : UIPixExtractPageState()

    object HideLoadingMoreTransactions : UIPixExtractPageState()

    data class Success(
        val transactions: ArrayList<Any>,
        val isMoreTransactions: Boolean,
    ) : UIPixExtractPageState()

    object EmptyTransactions : UIPixExtractPageState()

    object EmptyTransactionsWithActiveFilter : UIPixExtractPageState()

    data class Error(
        val error: NewErrorMessage? = null,
    ) : UIPixExtractPageState()
}
