package br.com.mobicare.cielo.meuCadastro.domains.entities

import android.os.Parcelable
import br.com.mobicare.cielo.BuildConfig
import kotlinx.android.parcel.Parcelize
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Created by silvia.miranda on 25/04/2017.
 */
@Parcelize
class MeuCadastroEndereco : Parcelable {

    object Tipo {
        @JvmField val FISICO = "fisico"
        @JvmField val CONTATO = "contato"
    }

    var type: String? = null
    var street: String? = null
    var complement: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var status: String? = null

    //    public enum Tipo {
    //        FISICO("fisico"), CONTATO("contato");
    //
    //        private final String name;
    //        Tipo(String s) {
    //            name = s;
    //        }
    //
    //        public boolean equalsName(String otherName) {
    //            return name.equals(otherName);
    //        }
    //
    //        public String toString() {
    //            return this.name;
    //        }
    //    }
    fun showAddress(): Boolean = (status != null)

    fun getUrl(): String {
        if(status == "OK") {
            var encoded = ""
            try {
                encoded = URLEncoder.encode(addressConcatenadoMap, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return "http://maps.google.com/maps/api/staticmap?zoom=16&size=400x400&sensor=true&key=${BuildConfig.GOOGLE_MAPS_KEY}&markers=color:red%7C$encoded&center=$encoded"
        }else{
            return "error"
        }
    }

    val addressConcatenadoMap: String
        get() = "$street, $city, $state,$postalCode"

    val addressConcatenado: String
        get() {
          return "$street${toSepareteStreet()}$complement${toSepareteCity()}$city${toSepareteState()}$state${toSepareteCep()}${toIncludeCep()}$postalCode"
        }

    fun toSepareteStreet() : String {
        return if (street.isNullOrEmpty() || complement.isNullOrEmpty()) "" else " - "
    }

    fun toSepareteCity() : String {
        return if (toSepareteStreet().isEmpty() && city.isNullOrEmpty()) "" else ", "
    }

    fun toSepareteState(): String {
        return if (toSepareteCity().isEmpty() && state.isNullOrEmpty()) "" else " - "
    }

    fun toSepareteCep(): String {
        return if (toSepareteState().isEmpty() && postalCode.isNullOrEmpty()) "" else " | "
    }

    fun toIncludeCep(): String {
        return if (postalCode.isNullOrEmpty()) "" else "CEP: "
    }

}
