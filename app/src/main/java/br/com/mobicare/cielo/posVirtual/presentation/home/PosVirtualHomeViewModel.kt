package br.com.mobicare.cielo.posVirtual.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct
import br.com.mobicare.cielo.posVirtual.presentation.home.utils.PosVirtualProductClickAction
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants

class PosVirtualHomeViewModel : ViewModel() {

    private var products: List<PosVirtualProduct>? = null

    private val _enabledProductsLiveData = MutableLiveData<List<PosVirtualProduct>>()
    val enabledProductsLiveData: LiveData<List<PosVirtualProduct>> get() = _enabledProductsLiveData

    private val _notEnabledProductsLiveData = MutableLiveData<List<PosVirtualProduct>>()
    val notEnabledProductsLiveData: LiveData<List<PosVirtualProduct>> get() = _notEnabledProductsLiveData

    private val enabledProducts
        get() = products
            ?.filter { it.status == PosVirtualStatus.SUCCESS }

    private val notEnabledProducts
        get() = products
            ?.filter { it.status != PosVirtualStatus.SUCCESS }

    fun setProductList(products: List<PosVirtualProduct>) {
        this.products = products
    }

    fun buildMenuItems() {
        _enabledProductsLiveData.value = enabledProducts
        _notEnabledProductsLiveData.value = notEnabledProducts
    }

    fun routeAction(product: PosVirtualProduct, callback: (PosVirtualProductClickAction?) -> Unit) {
        callback(
            when (product.status) {
                PosVirtualStatus.SUCCESS -> getEnabledProductAction(product)
                PosVirtualStatus.PENDING,
                PosVirtualStatus.CANCELED,
                PosVirtualStatus.FAILED -> PosVirtualProductClickAction.RequestDetails(product)

                else -> null
            }
        )
    }

    private fun getEnabledProductAction(product: PosVirtualProduct) = when (product.id) {
        PosVirtualProductId.TAP_ON_PHONE -> PosVirtualProductClickAction.TapOnPhone(hasCardReader)
        PosVirtualProductId.PIX -> PosVirtualProductClickAction.Pix(product.logicalNumber)
        PosVirtualProductId.SUPERLINK_ADDITIONAL -> PosVirtualProductClickAction.SuperLink
        else -> PosVirtualProductClickAction.UnavailableOption
    }

    private val hasCardReader get() = products?.find { it.id == PosVirtualProductId.CARD_READER } != null

}