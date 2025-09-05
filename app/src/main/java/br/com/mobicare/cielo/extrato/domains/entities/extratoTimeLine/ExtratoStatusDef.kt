package br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine

/**
 * Created by benhur.souza on 05/06/2017.
 */

object ExtratoStatusDef {

    /**
    1 - Aprovada
    2 - Negada
    4 - Desfeita
    5 - Erro
    8 - Cancelada
    9 - Pendente/Atualizar
     * */



    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    @androidx.annotation.IntDef(APROVADA, CANCELADA, DESFEITA, NEGADA)
    annotation class Status

    const val APROVADA =  1
    const val NEGADA = 2
    const val DESFEITA = 4
    const val ERRO = 5
    const val CANCELADA = 8
    const val ATUALIZAR = 9

}
