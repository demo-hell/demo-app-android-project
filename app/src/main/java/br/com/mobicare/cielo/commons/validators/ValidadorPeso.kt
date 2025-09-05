package br.com.mobicare.cielo.commons.validators

import android.text.Editable
import br.com.concrete.canarinho.formatador.Formatador
import br.com.concrete.canarinho.validator.Validador
import br.com.concrete.canarinho.validator.Validador.ResultadoParcial

class ValidadorPeso private constructor() : Validador {
    override fun ehValido(valor: String): Boolean {
        if (valor == null || valor.length < 8) {
            return false
        }
        val desformatado = Formatador.Padroes.PADRAO_SOMENTE_NUMEROS.matcher(valor).replaceAll("")
        return desformatado.length == 8
    }

    override fun ehValido(valor: Editable, resultadoParcial: ResultadoParcial): ResultadoParcial {
        require(!(resultadoParcial == null || valor == null)) { "Valores não podem ser nulos" }
        val desformatado = Formatador.Padroes.PADRAO_SOMENTE_NUMEROS.matcher(valor).replaceAll("")
        return if (!ehValido(desformatado)) {
            resultadoParcial
                    .parcialmenteValido(desformatado.length < 1)
                    .mensagem("Peso inválido")
                    .totalmenteValido(false)
        } else resultadoParcial
                .parcialmenteValido(true)
                .totalmenteValido(true)
    }

    companion object {
        private val INSTANCE = ValidadorPeso()
        val instance: ValidadorPeso
            get() = INSTANCE
    }
}