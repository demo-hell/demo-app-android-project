package br.com.mobicare.cielo.me

import com.google.gson.annotations.SerializedName

data class AnticipationEligibilityResponse(@SerializedName("looseAntecipationStatus")
                                           var looseAnticipationStatus: String?,
                                           val scheduledAnticipationStatus: String?)