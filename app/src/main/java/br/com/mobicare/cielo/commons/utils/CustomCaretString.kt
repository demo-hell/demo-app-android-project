package br.com.mobicare.cielo.commons.utils

import com.redmadrobot.inputmask.model.CaretString

object CustomCaretString {

    fun forward(value: String, autocompleteValue: Boolean = false) =
        CaretString(value, value.length, CaretString.CaretGravity.FORWARD(autocompleteValue))

}