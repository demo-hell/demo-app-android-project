package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository

import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.model.SummaryItemsPostings
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary

data class PostingsResponse(val summary: List<Summary>, val items: List<SummaryItemsPostings>)