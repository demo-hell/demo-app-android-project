package br.com.mobicare.cielo.extensions

fun <T> List<T>?.hasIndex(index: Int): Boolean{
    return (this?.size ?: 0) > index
}