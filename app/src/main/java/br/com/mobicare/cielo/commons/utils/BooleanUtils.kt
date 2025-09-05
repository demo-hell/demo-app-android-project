package br.com.mobicare.cielo.commons.utils

fun <T>Boolean.ifTrue(supplier: () -> T) = if (this) supplier() else null