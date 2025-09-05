package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato

import br.com.mobicare.cielo.R

enum class TrocaDomicilioStatus(val status: String, val code:Int) {

    PENDING("Em andamento", 1) {
        override fun getColor() = R.color.color_f98f25
    },
    REJECT("Rejeitado", 2) {
        override fun getColor() = R.color.red_DC392A
    },

    AWAITING("Aguardando", 3) {
        override fun getColor() = R.color.red_DC392A
    },

    CHECKING("Em análise", 4) {
        override fun getColor() = R.color.red_DC392A
    },

    CONCLUDED("Concluído", 5) {
        override fun getColor() = R.color.red_DC392A
    },

    ERROR("Erro", 7) {
        override fun getColor() = R.color.color_f98f25
    },

    CANCEL("Cancelado",8) {
        override fun getColor() = R.color.color_f98f25
    },
    ALL("Todos",9) {
        override fun getColor() = R.color.color_f98f25
    };

    abstract fun getColor(): Int
}