package br.com.mobicare.cielo.migration.presentation.presenter

import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain

interface MigrationContract {
    interface View : IAttached {
        fun onMigrationUser(response: MultichannelUserTokenResponse)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String, title: String)
        fun showError(error: ErrorMessage)
        fun showErrorApi(error: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun migrationUser(migrationDomain: MigrationDomain)
    }
}