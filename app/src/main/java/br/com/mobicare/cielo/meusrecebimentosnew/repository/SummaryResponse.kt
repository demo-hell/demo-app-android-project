package br.com.mobicare.cielo.meusrecebimentosnew.repository

import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import br.com.mobicare.cielo.meusrecebimentosnew.models.SummaryItems
import com.google.gson.annotations.SerializedName

class SummaryResponse(val summary: Summary,
                      @SerializedName("items")
                      val summaryItems: List<SummaryItems>)