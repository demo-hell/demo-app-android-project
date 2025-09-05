package br.com.mobicare.cielo.extensions

val Long?.orZero: Long
    get() = this ?: 0L