package br.com.mobicare.cielo.home.presentation.main.ui.fragment

import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand

interface FeeAndPlansHomeContract {
    interface View {
        fun showFeePerBrand(brands: ArrayList<Brand>, isError: Boolean = false)
        fun hideFeesAndPlans()
    }

    interface Presenter: CommonPresenter {
        fun getBrands()
    }
}