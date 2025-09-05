package br.com.mobicare.cielo.commons.utils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.captureValues(): List<T?> {
    val list = mutableListOf<T>()
    this.observeForever {
        list.add(it)
    }
    return list
}
