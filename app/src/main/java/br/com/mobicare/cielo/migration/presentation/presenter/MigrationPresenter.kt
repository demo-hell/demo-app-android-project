package br.com.mobicare.cielo.migration.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.migration.MigrationRepository
import br.com.mobicare.cielo.migration.domain.MigrationRequest
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain

class MigrationPresenter(private val repository: MigrationRepository) : MigrationContract.Presenter {

    private lateinit var mView: MigrationContract.View

    override fun migrationUser(migrationDomain: MigrationDomain) {

        migrationDomain.passwordConfirmation = migrationDomain.password
        migrationDomain.merchantId = UserPreferences.getInstance().numeroEC
        val cpfWithPoint = migrationDomain.cpf?.replace(".", "")?.replace("-", "")?.trim()
        migrationDomain.cpf = cpfWithPoint


        val migrationRequest = getMigrationRequest(migrationDomain)

        var accessToken = UserPreferences.getInstance().token
        val authorization = Utils.authorization()

        repository.migrationUser(accessToken, authorization, migrationRequest,
                object : APICallbackDefault<MultichannelUserTokenResponse, String> {
            override fun onStart() {
                mView.showLoading()
            }

            override fun onError(error: ErrorMessage) {
                if (error.httpStatus == 420) {
                    if (!error.code.isNullOrEmpty() && error.code == "business_error") {
                        mView.showError(error)
                        return
                    }
                }
                mView.showErrorApi(error.httpStatus)
            }

            override fun onFinish() {
                mView.hideLoading()
            }

            override fun onSuccess(response: MultichannelUserTokenResponse) {
                mView.hideLoading()
                mView.onMigrationUser(response)
            }

        })

    }

    private fun getMigrationRequest(migrationDomain: MigrationDomain): MigrationRequest {
        migrationDomain.let {
            return MigrationRequest(it.fullName, it.cpf, it.email, it.currentPassword,
                    it.password, it.passwordConfirmation, it.merchantId)
        }
    }

    override fun setView(view: MigrationContract.View) {
        mView = view
    }

}