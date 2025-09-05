package br.com.mobicare.cielo.commons.helpers

import br.com.concrete.canarinho.formatador.Formatador
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

class FormHelper {
    companion object {
        fun cepFormat(number: String): String {
            if (number.isNotEmpty() && number.length == 8) {
                return Formatador.CEP.formata(number)
            }
            return number
        }

        fun maskFormatter(value: String, mask: String): Mask.Result {
            val formatter = Mask(mask)
            return formatter.apply(CustomCaretString.forward(value))
        }
    }
}