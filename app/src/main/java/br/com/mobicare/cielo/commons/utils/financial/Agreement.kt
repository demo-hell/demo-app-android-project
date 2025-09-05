package br.com.mobicare.cielo.commons.utils.financial

class Agreement {
    companion object {
        fun nameFrom(code: String): String {
            var nameAgreement: String?

            when (code) {
                "1" -> nameAgreement = "Prefeituras"
                "2" -> nameAgreement = "Saneamento"
                "3" -> nameAgreement = "Energia Elétrica e Gás"
                "4" -> nameAgreement = "Telecomunicações"
                "5" -> nameAgreement = "Órgãos Governamentais"
                "6" -> nameAgreement = "Carnes e Assemelhados ou demais Empresas / Órgãos que serão identificadas através do CNPJ."
                "7" -> nameAgreement = "Multas de trânsito"
                "9" -> nameAgreement = "Uso exclusivo do banco"
                else -> nameAgreement = "Convênio Desconhecido"
            }
            return nameAgreement
        }
    }
}