package br.com.mobicare.cielo.commons.utils.codebar

import java.util.*

data class CodeBarData (val codebarLine: String,
                        val codebarValue: String,
                        val cadebarDate: Calendar?,
                        val codebarBank: String?,
                        val codebarAgreement: String?,
                        val codevarValid: Boolean)