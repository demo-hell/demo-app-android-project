package br.com.mobicare.cielo.changeEc.domain

import com.google.gson.annotations.SerializedName

class HierachyResponse (
    @SerializedName("items")
    val hierarchies: Array<Hierarchy>? = null,
    val pagination: Pagination? = null)

class Pagination(var pageNumber: Int,
                 var pageSize: Int,
                 var totalElements: Int,
                 var firstPage: Boolean,
                 var lastPage: Boolean,
                 var numPages: Int)