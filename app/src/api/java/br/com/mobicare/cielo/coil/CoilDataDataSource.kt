package br.com.mobicare.cielo.coil

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.coil.domain.MerchantAddressResponse
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domain.MerchantSuppliesResponde
import br.com.mobicare.cielo.coil.domain.MerchantSupplyChosen
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import io.reactivex.Observable

class CoilDataDataSource(context: Context)  {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun merchantSupplies(access_token: String) : Observable<MerchantSuppliesResponde> {
        return api.merchantSupplies(access_token)
    }


    fun merchantAddress(access_token: String) : Observable<MerchantAddressResponse> {
        return api.merchantAddress(access_token)
    }

    fun merchantBuySupply(token: String, supplies: ArrayList<MerchantSupplyChosen>): Observable<MerchantBuySupplyChosenResponse>  {
        return api.merchantBuySupply(token, supplies)
    }

}