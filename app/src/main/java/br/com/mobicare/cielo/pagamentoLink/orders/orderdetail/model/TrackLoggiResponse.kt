package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail.model

data class TrackLoggiResponse(val orderId: String,
                              val status: String,
                              val deliveryStatus: String,
                              val trackingUrl: String)