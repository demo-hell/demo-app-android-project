package br.com.mobicare.cielo.research

import br.com.mobicare.cielo.research.domains.entities.ResearchRating
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse

interface ResearchContract {

    interface ResearchPresenter {
        fun getResearch(it: String)
        fun saveResearch(
            researchRating: ResearchRating?,
            ecNumber: String
        )
    }

    interface ResearchView {
        fun saveDataResearch(researchResponse: ResearchResponse?)
    }

}