package br.com.mobicare.cielo.commons.utils

inline fun <R> R?.ifNull(block: () -> R): R = this ?: block()