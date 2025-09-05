package br.com.mobicare.cielo.chat.domains

/**
 * Created by gustavon on 27/09/17.
 */
enum class ParamsUrl constructor(var desc: String?) {

    PREFIX("/"),
    token("?token="),
    merchant("&merchant=")
}
