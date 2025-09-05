package br.com.mobicare.cielo.meusRecebimentos.domains.entities

/**
 * Created by silvia.miranda on 31/08/2017.
 */
class GroupedPostings {
    var date: String? = null
    var paymentDate: String? = null
    var postings: ArrayList<PostingDetailObject> = ArrayList()
}