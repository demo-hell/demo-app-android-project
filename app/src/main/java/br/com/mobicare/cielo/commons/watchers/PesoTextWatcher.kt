package br.com.mobicare.cielo.commons.watchers

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import br.com.concrete.canarinho.validator.Validador.ResultadoParcial
import br.com.concrete.canarinho.watcher.BaseCanarinhoTextWatcher
import br.com.mobicare.cielo.commons.validators.ValidadorPeso

class PesoTextWatcher: BaseCanarinhoTextWatcher() {
    private val PESO_MASK = "#g".toCharArray()
    private val FILTRO_SET_DIGITOS = arrayOf<InputFilter>(LengthFilter(7))

    private val resultadoParcial = ResultadoParcial()

    override fun afterTextChanged(s: Editable?) {
        if (isMudancaInterna) {
            return
        }

        s!!.filters = FILTRO_SET_DIGITOS

        val builder = trataAdicaoRemocaoDeCaracter(s, PESO_MASK)

        atualizaTexto(ValidadorPeso.instance, resultadoParcial, s, builder)
    }
}