package br.com.mobicare.cielo.commons.utils

import android.annotation.SuppressLint
import android.text.InputFilter
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import com.fasterxml.jackson.core.io.CharacterEscapes
import com.google.android.material.textfield.TextInputLayout


@SuppressLint("ClickableViewAccessibility")
fun EditText.addRightCompoundClickListener(listener: View.OnClickListener): Unit {

    this.setOnTouchListener(View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_UP) {

            if (this.compoundDrawables[2] != null &&
                    event.rawX >= (this.right -
                            this.compoundDrawables[2].bounds.width())) {
                listener.onClick(view)
                return@OnTouchListener true
            }
        }

        false
    })

}

fun TypefaceEditTextView.validateEmptyField(textInputLayout: TextInputLayout,
                                            errorMessage: String) {

    if (TextUtils.isEmpty(this.text?.trim())) {
        textInputLayout.error = errorMessage
    } else {
        textInputLayout.error = ""
    }

}

fun EditText.numbersFilter() {
    this.filters = arrayOf<InputFilter>(InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isLetter(source[i]).not() && source[i].toString() != ONE_SPACE){
                return@InputFilter ""
            }
        }
        null
    })
}

