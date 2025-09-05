package br.com.mobicare.cielo.newRecebaRapido.util

sealed class PlanState {
    object Daily: PlanState()
    class Weekly(val weekday: String): PlanState()
    class Monthly(val monthDay: Int): PlanState()
    object Empty: PlanState()
}

