package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.SubCategorie
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class CentralAjudaSubCategoriasPresenter(
    private val view: CentralAjudaSubCatregoriasContract.View,
    private val repository: CentralAjudaLogadoRepository,
    private val userPreferences: UserPreferences
) : CentralAjudaSubCatregoriasContract.Presenter {

    private var categoryId: String = EMPTY
    private var categoryName: String = EMPTY

    override fun loadSubCategories(categoryId: String, categoryName: String) {
        this.categoryId = categoryId
        this.categoryName = categoryName
        repository.faqSubCategories(
            userPreferences.token,
            categoryId,
            object : APICallbackDefault<List<SubCategorie>, String> {
                override fun onStart() {
                    view.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    if (error.logout)
                        view.logout(error)
                    else
                        view.showError(error)
                    view.hideLoading()
                }

                override fun onSuccess(response: List<SubCategorie>) {
                    view.showSubCategories(response)
                    view.hideLoading()
                }

            })
    }

    override fun resubmit() {
        loadSubCategories(categoryId, categoryName)
    }

    override fun onSubCategorySelected(subCategorie: SubCategorie) {
        view.goToQuestionSelect(categoryId, subCategorie.id, subCategorie.name)
    }
}