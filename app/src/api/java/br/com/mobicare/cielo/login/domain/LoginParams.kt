package br.com.mobicare.cielo.login.domain

import br.com.mobicare.cielo.commons.utils.ValidationUtils
import com.google.gson.annotations.SerializedName

/**
 * Created by Benhur on 12/05/17.
 */

class LoginParams(ec: String, password: String, userName: String) {
    @SerializedName("ec")
    var ec: String = ec

    @SerializedName("userName")
    var userName: String = userName

    @SerializedName("password")
    var password: String

    init {
        this.password = ValidationUtils.getBase64(password).trim { it <= ' ' }
    }

}
