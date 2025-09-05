package br.com.mobicare.cielo

import org.junit.Assert

fun <T> Iterable<T>.assertAny(predicate: (T) -> Boolean) {
    Assert.assertTrue(this.any(predicate))
}