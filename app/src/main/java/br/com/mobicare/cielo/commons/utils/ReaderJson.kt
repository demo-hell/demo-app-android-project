package br.com.mobicare.cielo.commons.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.onboarding.domains.entities.OnboardingObj
import com.google.gson.GsonBuilder
import java.lang.reflect.Modifier

open class ReaderJson {
    companion object {
        fun getOnBoarding(context: Context?): OnboardingObj {
            return convertToObject(R.raw.onboarding, OnboardingObj::class.java, context)
        }

        fun getBandeirasTaxas(context: Context?): CardBrandFees {
            return convertToObject(R.raw.card_brand_fees, CardBrandFees::class.java, context)
        }

        fun <T> convertToObject(resourceId: Int, classFile: Class<T>, context: Context?): T {
            return GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create().fromJson(getResourceAsString(context, resourceId), classFile)
        }

        fun getResourceAsString(context: Context?, resourceId: Int): String? {
            if(context == null){
                return null
            }

            try {
                val res = context?.resources
                val in_s = res?.openRawResource(resourceId)
                if(in_s != null){
                    val b = ByteArray(in_s!!.available())
                    in_s.read(b)

                    return String(b)
                }

                return null

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }


}
