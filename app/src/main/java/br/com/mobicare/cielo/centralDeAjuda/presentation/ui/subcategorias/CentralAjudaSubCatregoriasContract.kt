package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.SubCategorie
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface CentralAjudaSubCatregoriasContract {

    interface Presenter {
        fun resubmit()
        fun onSubCategorySelected(subCategorie: SubCategorie)
        fun loadSubCategories(categoryId: String, categoryName: String)
    }

    interface View : BaseView, IAttached {
        fun showSubCategories(subcategories: List<SubCategorie>)
        fun goToQuestionSelect(faqId: String, subCategorieId: String, subCategorieName: String)
    }

}