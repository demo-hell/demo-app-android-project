package br.com.mobicare.cielo.commons.utils.totp

interface Clock {

    /** Obtém o tempo corrente em milisegundos */
    fun nowMillis(): Long

}