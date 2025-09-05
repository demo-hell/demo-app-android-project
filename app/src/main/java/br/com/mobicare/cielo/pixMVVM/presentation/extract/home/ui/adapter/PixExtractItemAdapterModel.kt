package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.adapter

import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel

data class PixExtractItemAdapterModel(
    val type: PixExtractTransactionAdapterTypeEnum,
    val receipt: PixExtract.PixExtractReceipt? = null,
    val receiptScheduled: PixReceiptsScheduled.Item.Receipt? = null,
    var filterData: PixExtractFilterModel? = null,
)
