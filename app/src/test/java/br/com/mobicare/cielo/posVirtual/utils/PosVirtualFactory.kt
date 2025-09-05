package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.*
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants
import br.com.mobicare.cielo.posVirtual.data.mapper.MapperPosVirtualBrands
import br.com.mobicare.cielo.posVirtual.data.mapper.toEntity
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.BankResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualBrandsResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualResponse
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_BANK_ACCOUNT
import com.google.gson.Gson

object PosVirtualFactory {

    object Eligibility {
        private val posVirtualJson = """
            {
                "status": "SUCCESS",
                "merchantId": "2014158970",
                "impersonateRequired": false,
                "products": [
                    {
                        "id": "TAP-ON-PHONE",
                        "logicalNumber": "123",
                        "status": "PENDING"
                    },
                    {
                        "id": "PIX",
                        "logicalNumber": "123",
                        "status": "SUCCESS"
                    },
                    {
                        "id": "SUPERLINK-ADDITIONAL",
                        "logicalNumber": "123",
                        "status": "CANCELED"
                    },
                    {
                        "id": "CARD-READER",
                        "logicalNumber": "123",
                        "status": "FAILED"
                    }
                ]
            }
        """.trimIndent()

        val posVirtualResponse: PosVirtualResponse =
            Gson().fromJson(posVirtualJson, PosVirtualResponse::class.java)

        val posVirtualEntity = posVirtualResponse.toEntity()
        val posVirtualProducts = posVirtualEntity.products!!
    }

    object OfferResponseFactory {
        private val offerJson = """
            {
                "offer": {
                    "id": "225049cb-a825-480a-a1c8-61e6dc812da3",
                    "description": "Oferta de POS Virtual + Tap on Phone + Receba Rápido + Pix",
                    "expirationDate": "2023-06-29T20:59:59.999",
                    "settlementTerm": 2,
                    "products": [
                        {
                            "id": "2a5ca444-cfec-42e9-bcf4-8bb650aa47c0",
                            "name": "Tap on Phone",
                            "reference": "TAP-ON-PHONE",
                            "description": "Efetuar os pagamentos por meio aproximação através de um telefone celular de forma digital",
                            "settlementTerm": 2,
                            "brands": [
                                {
                                    "code": "1",
                                    "name": "VISA",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_1.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "code": "2",
                                    "name": "MASTER",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_2.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "id": "45f3162c-0669-4ca6-9bd4-5c5178cce1a7",
                            "name": "Receba Rápido",
                            "reference": "RECEBA-RAPIDO",
                            "description": "Antecipação dos recebíveis.",
                            "settlementTerm": 2,
                            "validity": 12,
                            "brands": [
                                {
                                    "code": "1",
                                    "name": "VISA",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_1.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "code": "2",
                                    "name": "MASTER",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_2.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "id": "f6654ba5-f319-42ea-8b4e-4ec07e8bdd07",
                            "name": "Pix",
                            "reference": "PIX",
                            "description": "Efetuar os pagamentos por meio de uma transferência instantânea, de forma digital, recebendo suas vendas onde preferir.",
                            "settlementTerm": 2,
                            "validity": 12,
                            "note": "Verificar a elegibilidade na lista de bancos disponível no serviço payout-eligible-banks.",
                            "brands": [
                                {
                                    "code": "PIX",
                                    "name": "Pix",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_PIX.png",
                                    "conditions": [
                                        {
                                            "mdr": 0.92
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "agreements": [
                        {
                            "code": "OPTIN-TERMOS-E-CONTRATOS",
                            "isMandatory": true,
                            "description": "Termos e Contratos - Cielo",
                            "status": "PENDING",
                            "terms": [
                                {
                                    "description": "Contrato de credenciamento Cielo",
                                    "version": "2.0",
                                    "url": "https://www.cielo.com.br/docs/Contrato_de_Credenciamento_Consolidado_novo.pdf"
                                },
                                {
                                    "description": "Termos e condições de uso do site Cielo",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/termos-condicoes-de-uso"
                                },
                                {
                                    "description": "Política de privacidade e uso de dados pessoais",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/privacidade"
                                }
                            ]
                        },
                        {
                            "code": "OPTIN-PIX",
                            "isMandatory": false,
                            "description": "Termos e Contratos - PIX",
                            "status": "PENDING",
                            "terms": [
                                {
                                    "description": "Termos e condições de uso da conta PIX",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/pix/termo-de-uso/"
                                }
                            ]
                        }
                    ]
                }
            }
        """.trimIndent()

        private val offerWithRequiredJson = """
            {
                "offer": {
                    "id": "225049cb-a825-480a-a1c8-61e6dc812da3",
                    "description": "Oferta de POS Virtual + Tap on Phone + Receba Rápido + Pix",
                    "expirationDate": "2023-06-29T20:59:59.999",
                    "settlementTerm": 2,
                    "products": [
                        {
                            "id": "2a5ca444-cfec-42e9-bcf4-8bb650aa47c0",
                            "name": "Tap on Phone",
                            "reference": "TAP-ON-PHONE",
                            "description": "Efetuar os pagamentos por meio aproximação através de um telefone celular de forma digital",
                            "settlementTerm": 2,
                            "brands": [
                                {
                                    "code": "1",
                                    "name": "VISA",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_1.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "code": "2",
                                    "name": "MASTER",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_2.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "id": "45f3162c-0669-4ca6-9bd4-5c5178cce1a7",
                            "name": "Receba Rápido",
                            "reference": "RECEBA-RAPIDO",
                            "description": "Antecipação dos recebíveis.",
                            "settlementTerm": 2,
                            "validity": 12,
                            "brands": [
                                {
                                    "code": "1",
                                    "name": "VISA",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_1.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "code": "2",
                                    "name": "MASTER",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_2.png",
                                    "conditions": [
                                        {
                                            "type": "CREDIT_IN_CASH",
                                            "label": "Crédito à Vista",
                                            "mdr": 3.49,
                                            "rateContractedRR": 1,
                                            "flexibleTermPaymentMDR": 4.49
                                        },
                                        {
                                            "type": "DEBIT",
                                            "label": "Débito",
                                            "mdr": 1.89
                                        },
                                        {
                                            "type": "CREDIT_IN_INSTALLMENTS",
                                            "label": "Crédito Parcelado",
                                            "installments": [
                                                {
                                                    "installment": 2,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 3.98,
                                                    "flexibleTermPaymentMDR": 8.47
                                                },
                                                {
                                                    "installment": 3,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 5.97,
                                                    "flexibleTermPaymentMDR": 10.46
                                                },
                                                {
                                                    "installment": 4,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 7.96,
                                                    "flexibleTermPaymentMDR": 12.45
                                                },
                                                {
                                                    "installment": 5,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 9.95,
                                                    "flexibleTermPaymentMDR": 14.44
                                                },
                                                {
                                                    "installment": 6,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 11.94,
                                                    "flexibleTermPaymentMDR": 16.43
                                                },
                                                {
                                                    "installment": 7,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 13.93,
                                                    "flexibleTermPaymentMDR": 18.42
                                                },
                                                {
                                                    "installment": 8,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 15.92,
                                                    "flexibleTermPaymentMDR": 20.41
                                                },
                                                {
                                                    "installment": 9,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 17.91,
                                                    "flexibleTermPaymentMDR": 22.40
                                                },
                                                {
                                                    "installment": 10,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 19.90,
                                                    "flexibleTermPaymentMDR": 24.39
                                                },
                                                {
                                                    "installment": 11,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 21.89,
                                                    "flexibleTermPaymentMDR": 26.38
                                                },
                                                {
                                                    "installment": 12,
                                                    "mdr": 4.49,
                                                    "rateContractedRR": 23.88,
                                                    "flexibleTermPaymentMDR": 28.37
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "id": "f6654ba5-f319-42ea-8b4e-4ec07e8bdd07",
                            "name": "Pix",
                            "reference": "PIX",
                            "description": "Efetuar os pagamentos por meio de uma transferência instantânea, de forma digital, recebendo suas vendas onde preferir.",
                            "settlementTerm": 2,
                            "validity": 12,
                            "note": "Verificar a elegibilidade na lista de bancos disponível no serviço payout-eligible-banks.",
                            "brands": [
                                {
                                    "code": "PIX",
                                    "name": "Pix",
                                    "imgSource": "http://digital-ti.ccorp.local/merchant/offers/static/assets/img/brands/brand_PIX.png",
                                    "conditions": [
                                        {
                                            "mdr": 0.92
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "agreements": [
                        {
                            "code": "OPTIN-TERMOS-E-CONTRATOS",
                            "isMandatory": true,
                            "description": "Termos e Contratos - Cielo",
                            "status": "PENDING",
                            "terms": [
                                {
                                    "description": "Contrato de credenciamento Cielo",
                                    "version": "2.0",
                                    "url": "https://www.cielo.com.br/docs/Contrato_de_Credenciamento_Consolidado_novo.pdf"
                                },
                                {
                                    "description": "Termos e condições de uso do site Cielo",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/termos-condicoes-de-uso"
                                },
                                {
                                    "description": "Política de privacidade e uso de dados pessoais",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/privacidade"
                                }
                            ]
                        },
                        {
                            "code": "OPTIN-PIX",
                            "isMandatory": false,
                            "description": "Termos e Contratos - PIX",
                            "status": "PENDING",
                            "terms": [
                                {
                                    "description": "Termos e condições de uso da conta PIX",
                                    "version": "1.0",
                                    "url": "https://www.cielo.com.br/pix/termo-de-uso/"
                                }
                            ]
                        }
                    ]
                },
                "required": {
                    "individualFields": [
                         { 
                            "id": "registrationData.individual.email",
                            "label": "E-mail",
                            "format": "EMAIL",
                            "placeholder": "Digite seu e-mail"
                         }   
                    ]
                }
            }
        """.trimIndent()

        private val offerEmptyWithRequiredJson = """
            {
                "required": {
                    "individualFields": [
                         { 
                            "id": "registrationData.individual.email",
                            "label": "E-mail",
                            "format": "EMAIL",
                            "placeholder": "Digite seu e-mail"
                         }   
                    ]
                }
            }
        """.trimIndent()

        val offerResponse: OfferResponse = Gson().fromJson(offerJson, OfferResponse::class.java)
        val offerWithRequiredResponse: OfferResponse = Gson().fromJson(offerWithRequiredJson, OfferResponse::class.java)
        val offerEmptyWithRequiredResponse: OfferResponse = Gson().fromJson(offerEmptyWithRequiredJson, OfferResponse::class.java)

        val agreements = offerResponse.offer?.agreements!!
        val products = offerResponse.offer?.products!!
        val brandsTap = products.find {
            it.reference == PosVirtualConstants.REFERENCE_CODE_CIELO_TAP
        }!!.brands!!

        val required = offerWithRequiredResponse.required
    }

    val amount = 20.0.toBigDecimal()

    const val logicalNumber = "123456"

    val posVirtualCreateQRCodeRequest = PosVirtualCreateQRCodeRequest(
        logicalNumber = logicalNumber,
        amount = amount
    )

    val posVirtualCreateQRCodeResponse = PosVirtualCreateQRCodeResponse(
        id = "6f7262c8-6c12-30f0-951b-6cd60f199140",
        creationDate = "2023-04-27T18:06:20.593",
        nsuPix = "a8a94425",
        qrCodeString = "00020126580014br.gov.bcb.pix0136437cb071-2974-4c5f-a2a0-31835653b9ef520400005303986540520.005802BR5925MASSA DADOS AFIL. - 237-16009SAO PAULO6229052512345600000007872a8a944256304BD57",
        qrCodeBase64 = "",
        merchantName = "MASSA DADOS AFIL. - 237-1",
        merchantNumber = "2015833930",
        merchantDocument = "77795766000134",
        amount = 20.0
    )

    private val listBanksResponse = listOf(
        BankResponse(
            name = "BANCO ITAU S.A",
            code = "341",
            agency = "9326",
            accountNumber = "1672",
            accountDigit = "3"
        ),
        BankResponse(
            name = "BANCO DO BRASIL S.A.",
            code = "1",
            agency = "7964",
            agencyDigit = "2",
            accountNumber = "36784364",
            accountDigit = "1"
        )
    )

    private val posVirtualBrandsResponse = listOf(
        PosVirtualBrandsResponse(
            banks = listBanksResponse
        )
    )

    private val posVirtualBrandsResponseWithSolutionsNull = listOf(PosVirtualBrandsResponse())

    val solutions = MapperPosVirtualBrands.mapToBrands(posVirtualBrandsResponse)
    val solutionsNull =
        MapperPosVirtualBrands.mapToBrands(posVirtualBrandsResponseWithSolutionsNull)
    val itemsConfigurations = listOf("123456")

    val posVirtualCreateOrderRequest = OrdersRequest(
        type = RequiredDataFieldConstants.REQUIRED_DATA_FIELD_ORDER_TYPE,
        registrationData = null,
        order = Order(
            offerId = "123456",
            sessionId = "123456",
            payoutData = PayoutData(
                payoutMethod = POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_BANK_ACCOUNT,
                targetBankAccount = TargetBankAccount(
                    bankNumber = "1",
                    agency = "7964",
                    accountNumber = "36784364-1",
                    accountType = PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_CHECKING
                )
            ),
            agreements = OfferResponseFactory.offerResponse.offer?.agreements?.map {
                Agreement(
                    it.code.orEmpty(),
                    PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_AUTHORIZED
                )
            },
            itemsConfigurations = itemsConfigurations
        )

    )

    const val offerID = "123456"
    const val sessionID = "123456"

}