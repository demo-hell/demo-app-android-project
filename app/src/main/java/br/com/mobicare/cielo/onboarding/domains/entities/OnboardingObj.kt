package br.com.mobicare.cielo.onboarding.domains.entities

/**
 * Created by gustavon on 23/10/17.
 */
class OnboardingObj {

    var pages: List<Page>? = null

    class Page {
        var title: String = ""
        var subtitle: String = ""
    }
}