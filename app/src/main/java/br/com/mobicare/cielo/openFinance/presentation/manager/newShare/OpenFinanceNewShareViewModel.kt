package br.com.mobicare.cielo.openFinance.presentation.manager.newShare

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.DataPermission
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.model.DeadLine
import br.com.mobicare.cielo.openFinance.domain.model.InfoDetailsShare
import br.com.mobicare.cielo.openFinance.domain.model.ResourceGroup
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.domain.usecase.CreateShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.TermsOfUseUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.UpdateShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFile
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.ACCOUNT_TYPE
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.CUSTOMER_TYPE
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson
import kotlinx.coroutines.launch

class OpenFinanceNewShareViewModel(
    private val createShareUseCase: CreateShareUseCase,
    private val updateShareUseCase: UpdateShareUseCase,
    private val termsOfUseUseCase: TermsOfUseUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _createShareLiveData =
        MutableLiveData<UIStateConsentDetail<CreateShare>>()
    val createShareLiveData get() = _createShareLiveData

    private val _updateShareLiveData =
        MutableLiveData<UIStateConsentDetail<UpdateShare>>()
    val updateShareLiveData get() = _updateShareLiveData

    private val _termsOfUseLiveData =
        MutableLiveData<UIStateFile<String>>()
    val termsOfUseLiveData get() = _termsOfUseLiveData

    private val listRegisterData: MutableList<ResourceGroup> = mutableListOf()
    private val listComplementaryData: MutableList<ResourceGroup> = mutableListOf()
    private var createShareItem: CreateShare? = null
    private var deadLineFromDetails: DeadLine? = null

    fun createShare(authorizationServerId: String?, organizationId: String?) {
        if (authorizationServerId == null || organizationId == null) return
        val request = CreateShareRequest(authorizationServerId, organizationId, null)
        viewModelScope.launch {
            _createShareLiveData.postValue(UIStateConsentDetail.Loading)
            createShareUseCase.invoke(request)
                .onSuccess { createShare ->
                    _createShareLiveData.postValue(UIStateConsentDetail.Success(createShare))
                    filterResourceGroup(createShare.resourceGroups)
                    createShareItem = createShare
                    deadLineFromDetails = checkIfChangedOrNewShare()
                }
                .onError { error ->
                    _createShareLiveData.postValue(UIStateConsentDetail.Error(error.apiException.message))
                }
        }
    }

    private fun filterResourceGroup(resourceGroupList: List<ResourceGroup>) {
        resourceGroupList.forEach {
            if (it.type == ACCOUNT_TYPE || it.type == CUSTOMER_TYPE) {
                listRegisterData.add(it)
            } else {
                listComplementaryData.add(it)
            }
        }
    }

    fun getRegisterData(): List<ResourceGroup> {
        return listRegisterData
    }

    fun getComplementaryData(): List<ResourceGroup> {
        return listComplementaryData
    }

    fun updateShare(deadLine: DeadLine, typeShare: Int) {
        val newDeadline: DeadLine? = if (userPreferences.infoDetailsShare.isNullOrEmpty().not() && typeShare == ONE) {
            deadLineFromDetails
        } else {
            deadLine
        }
        val request = UpdateShareRequest(
            dataPermissions = createListPermissions(),
            deadLine = newDeadline,
            redirectUri = BuildConfig.URL_OPF_CALLBACK
        )
        viewModelScope.launch {
            _updateShareLiveData.postValue(UIStateConsentDetail.Loading)
            updateShareUseCase.invoke(createShareItem?.shareId ?: EMPTY, request).onSuccess {
                _updateShareLiveData.postValue(UIStateConsentDetail.Success(it))
            }.onError {
                _updateShareLiveData.postValue(UIStateConsentDetail.Error(it.apiException.message))
            }

        }
    }

    private fun createListPermissions(): List<DataPermission> {
        val listPermissions = mutableListOf<DataPermission>()
        createShareItem?.resourceGroups?.forEach { resourceGroup ->
            resourceGroup.permission.forEach { permission ->
                listPermissions.add(
                    DataPermission(
                        permission.permissionCode,
                        permission.displayName,
                        permission.detail
                    )
                )
            }
        }
        return listPermissions
    }

    fun getTermsOfUse() {
        viewModelScope.launch {
            _termsOfUseLiveData.postValue(UIStateFile.LoadingDocument)
            termsOfUseUseCase.invoke().onSuccess {
                _termsOfUseLiveData.postValue(UIStateFile.SuccessDocument(it.string))
            }.onError {
                _termsOfUseLiveData.postValue(UIStateFile.ErrorDocument)
            }
        }
    }

    fun saveInformationToConclusion() {
        userPreferences.saveShareIdOPF(createShareItem?.shareId?: EMPTY)
    }

    fun getDeadlineFromDetails(): DeadLine?{
        return deadLineFromDetails
    }

    private fun checkIfChangedOrNewShare() : DeadLine? {
        if (userPreferences.infoDetailsShare?.isEmpty()?.not() == true){
            return Gson().fromJson(userPreferences.infoDetailsShare, InfoDetailsShare::class.java).deadLine
        }else return null
    }
}