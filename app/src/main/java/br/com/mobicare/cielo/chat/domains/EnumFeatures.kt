package br.com.mobicare.cielo.chat.domains

/**
 * Created by gustavon on 27/09/17.
 */
enum class EnumFeatures constructor(val id: Int, val desc: String) {

    CHAT(1, "CHAT"),
    MY_MACHINES(2, "MY_MACHINES"),
    MY_SOLICITATIONS(3, "MY_SOLICITATIONS"),
    RATING(4, "RATING")

}
