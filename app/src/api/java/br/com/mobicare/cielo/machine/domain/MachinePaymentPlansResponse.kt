package br.com.mobicare.cielo.machine.domain

class MachinePaymentPlansResponse(
        val id: Int,
        val name: String,
        val rentalAmountval: Int,
        val antifraudAccessQuantityval: Int,
        val boletoQuantity: Int
)