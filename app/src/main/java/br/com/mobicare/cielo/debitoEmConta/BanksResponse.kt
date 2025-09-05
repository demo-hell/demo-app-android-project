package br.com.mobicare.cielo.debitoEmConta

import android.app.Activity

data class BanksResponse(
    val banks: List<BankResponse>
)

data class BankResponse(
    val bankCode: String,
    val bankName: String
)

fun Activity.getBankName(code: String): String? {
    when(code){

        "001"->"Banco do Brasil S.A."
        "237"->"Banco Bradesco S.A."
        "104"->"Caixa Econômica Federal"
        "341"->"Itaú Unibanco  S.A."
        "033"->"Banco Santander (Brasil) S. A."

        "745"->"Banco Citibank S.A."
        "269"->"HSBC Brasil S.A. Banco de Investimento"
        "422"->"Banco Safra S.A."
        "070"->"Banco de Brasília S.A."
        "136"->"Confederação Nacional das Cooperativas Centrais Unicred Ltda – Unicred do Brasil"

        "077"->"Banco Inter S.A."
        "741"->"Banco Ribeirão Preto S.A."
        "739"->"Banco Cetelem S.A."
        "743"->"Banco Semear S.A."
        "100"->"Planner Corretora de Valores S.A."

        "096"->"Banco B3 S.A."
        "747"->"Banco Rabobank International Brasil S.A."
        "748"->"Banco Cooperativo Sicredi S. A."
        "752"->"Banco BNP Paribas Brasil S.A."
        "091"->"Central de Cooperativas de Economia e Crédito Mútuo do Est RS - Unicred"

        "399"->"Kirton Bank S.A. - Banco Múltiplo"
        "108"->"PortoCred S.A. Crédito, Financiamento e Investimento"
        "756"->"Banco Cooperativo do Brasil S/A - Bancoob"
        "757"->"Banco Keb Hana do Brasil S. A."
        "102"->"XP Investimentos Corretora de Câmbio Títulos e Valores Mobiliários S.A."

        "084"->"Uniprime Norte do Paraná - Cooperativa de Crédito Ltda."
        "180"->"CM Capital Markets Corretora de Câmbio, Títulos e Valores Mobiliários Ltda."
        "066"->"Banco Morgan Stanley S. A."
        "015"->"UBS Brasil Corretora de Câmbio, Títulos e Valores Mobiliários S.A."
        "143"->"Treviso Corretora de Câmbio S.A."




    }
    return "BANCO BANCO"
}