package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits

interface IDOnboardingBenefitsInfoP1PolicyContract {
    interface Presenter {
        fun fetchTradeName(): String
        fun fetchCnpj(): String
        fun saveNewCpfToShowOnLogin(newCpf: String?)
    }
}