package br.com.mobicare.cielo.mdr.analytics

object ArvAnalyticsGA4Constants {
    const val MDR = "mdr"
    const val CONTRACTING = "contratacao"
    private const val CONTRACT = "contract"
    private const val ACCEPT = "accept"
    private const val REJECT = "reject"
    private const val SUCCESS = "success"
    private const val HOME = "home"

    const val MDR_OFFER_BASE = "t_venda_mdr."
    const val SCREEN_VIEW_MDR_HOME = "/$MDR/$HOME"
    const val SCREEN_VIEW_MDR_HOME_ACCEPT = "/$MDR/$HOME/$ACCEPT"
    const val SCREEN_VIEW_MDR_HOME_REJECT = "/$MDR/$HOME/$REJECT"
    const val SCREEN_VIEW_MDR_HOME_CONTRACTING = "/$MDR/$HOME/$CONTRACT"
    const val SCREEN_VIEW_MDR_HOME_CONTRACTING_SUCCESS = "/$MDR/$HOME/$CONTRACT/$SUCCESS"
}
