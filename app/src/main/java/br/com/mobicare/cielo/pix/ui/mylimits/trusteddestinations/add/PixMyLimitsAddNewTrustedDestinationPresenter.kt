package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.add

import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.formattedDocument
import br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations.PixTrustedDestinationsRepositoryContract
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.domain.PixAddNewTrustedDestinationRequest
import br.com.mobicare.cielo.pix.domain.TrustedDestinationLimit
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum
import br.com.mobicare.cielo.pix.enums.PixLimitTypeEnum
import br.com.mobicare.cielo.pix.enums.PixOwnerTypeEnum
import br.com.mobicare.cielo.pix.enums.PixServicesGroupEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection

class PixMyLimitsAddNewTrustedDestinationPresenter(
    private val view: PixMyLimitsAddNewTrustedDestinationContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixTrustedDestinationsRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : PixMyLimitsAddNewTrustedDestinationContract.Presenter {

    private var disposable = CompositeDisposable()

    private var _name: String? = null
    private var _document: String? = null
    private var _documentType: String? = null
    private var _bank: String? = null
    private var _branch: String? = null
    private var _account: String? = null
    private var _accountType: String? = null
    private var _ispb: Int? = null
    private var _key: String? = null
    private var _keyType: String? = null

    private var isKey = false

    override fun getUsername(): String = userPreferences.userName

    override fun onGetTrustedDestinationInformation(
        isKey: Boolean,
        keyInformation: ValidateKeyResponse?,
        manualTransferPayee: ManualPayee?
    ) {
        processData(isKey, keyInformation, manualTransferPayee)
        view.onSetTrustedDestinationInformation(
            name = _name,
            document = formattedDocument(),
            documentType = _documentType,
            bank = _bank,
            branch = _branch,
            account = _account
        )
    }

    private fun processData(
        isKey: Boolean,
        keyInformation: ValidateKeyResponse?,
        manualTransferPayee: ManualPayee?
    ) {
        this.isKey = isKey

        _name = if (isKey) keyInformation?.ownerName else manualTransferPayee?.name
        _document =
            if (isKey) keyInformation?.ownerDocument else manualTransferPayee?.documentNumber
        _documentType =
            if (isKey) keyInformation?.ownerType else manualTransferPayee?.beneficiaryType
        _bank = if (isKey) keyInformation?.participantName else manualTransferPayee?.bankName
        _branch = if (isKey) keyInformation?.branch else manualTransferPayee?.bankBranchNumber
        _account =
            if (isKey) keyInformation?.accountNumber else manualTransferPayee?.bankAccountNumber
        _accountType =
            if (isKey) keyInformation?.accountType else manualTransferPayee?.bankAccountType
        _ispb = if (isKey) keyInformation?.participant?.toInt() else manualTransferPayee?.ispb
        _key = keyInformation?.key
        _keyType = keyInformation?.keyType

        _documentType =
            if (_documentType == PixOwnerTypeEnum.LEGAL_PERSON.name || _documentType == BeneficiaryTypeEnum.CNPJ.key)
                PixOwnerTypeEnum.LEGAL_PERSON.owner
            else
                PixOwnerTypeEnum.NATURAL_PERSON.owner
    }

    private fun formattedDocument(): String? {
        return if (isKey.not())
            _document?.let { formattedDocument(it, _documentType) }
        else _document
    }

    override fun onAddNewTrustedDestination(
        otp: String?,
        limit: Double?,
        fingerprint: String
    ) {
        disposable.add(
            repository.addNewTrustedDestination(otp, request(limit, fingerprint))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onSuccessAddNewTrustedDestination()
                }, {
                    val error = APIUtils.convertToErro(it)
                    view.onErrorAddNewTrustedDestination(onGenericError = {
                        view.showError(error)
                    }, onOTPError = {
                        if (error.code != HttpURLConnection.HTTP_FORBIDDEN.toString() ||
                            error.errorCode.contains(OTP)
                        )
                            view.onErrorAddNewTrustedDestinationOTP()
                    })
                })
        )
    }

    private fun request(limit: Double?, fingerprint: String): PixAddNewTrustedDestinationRequest {
        val limits = listOf(
            TrustedDestinationLimit(
                type = PixLimitTypeEnum.DAYTIME_TRANSACTION_LIMIT.name,
                value = limit
            ),
            TrustedDestinationLimit(
                type = PixLimitTypeEnum.TOTAL_DAYTIME_TRANSACTION_LIMIT.name,
                value = limit
            ),
            TrustedDestinationLimit(
                type = PixLimitTypeEnum.TOTAL_MONTH_TRANSACTION_LIMIT.name,
                value = limit
            ),
            TrustedDestinationLimit(
                type = PixLimitTypeEnum.TOTAL_NIGHTTIME_TRANSACTION_LIMIT.name,
                value = limit
            ),
            TrustedDestinationLimit(
                type = PixLimitTypeEnum.NIGHTTIME_TRANSACTION_LIMIT.name,
                value = limit
            )
        )
        return PixAddNewTrustedDestinationRequest(
            bankAccountNumber = _account,
            bankBranchNumber = _branch,
            documentNumber = if (isKey) null else _document,
            institutionName = _bank,
            ispb = _ispb,
            key = _key,
            keyType = _keyType,
            limits = limits,
            name = _name,
            serviceGroup = PixServicesGroupEnum.PIX.name,
            fingerprint = fingerprint
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}