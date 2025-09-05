package br.com.mobicare.cielo.commons.data.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Benhur on 18/10/17.
 */
class CustomGsonConverterFactory(var gson: Gson, var context: Context): Converter.Factory(){

    companion object {

        fun getInstance(context: Context, gson: Gson): CustomGsonConverterFactory {
            return CustomGsonConverterFactory(gson, context)
        }

        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        fun create(context: Context, gson: Gson): CustomGsonConverterFactory {
            return CustomGsonConverterFactory.getInstance(context, gson)
        }

        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        fun create(context: Context): CustomGsonConverterFactory {
            return CustomGsonConverterFactory.create(context, Gson())
        }

    }

    private fun GsonConverterFactory(gson: Gson?) {
        if (gson == null) throw NullPointerException("gson == null")
        this.gson = gson
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?,
                                       retrofit: Retrofit?): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type!!))
        return CustomGsonResponseBodyConverter(context, gson, adapter)
    }

    override fun requestBodyConverter(type: Type?,
                                      parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type!!))
        return CustomGsonRequestBodyConverter(gson, adapter)
    }

}