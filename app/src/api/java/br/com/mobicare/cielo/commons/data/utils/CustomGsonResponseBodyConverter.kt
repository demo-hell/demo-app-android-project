package br.com.mobicare.cielo.commons.data.utils

import android.content.Context
import br.com.mobicare.cielo.commons.domains.entities.ErroList
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * Created by Benhur on 18/10/17.
 */
class CustomGsonResponseBodyConverter<T>(var context: Context, var gson: Gson, var adapter: TypeAdapter<T>): Converter<ResponseBody, T>{

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val jsonReader = gson.newJsonReader(value.charStream())
        var ad = adapter.read(jsonReader)
        sendErrorToGA(adapter.toJson(ad))

        try {
            return ad
        }finally {
            value.close()
        }
    }

    private fun sendErrorToGA(json: String){
        val errorList = gson.fromJson(json, ErroList::class.java)
        if(errorList != null && errorList.errorMessages != null && errorList.errorMessages!!.isNotEmpty()){
            for(error: ErrorMessage in errorList.errorMessages!!){
                //TODO verificar lógica das exceções do GA
            }
        }
    }
}
