package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.EIGHT
import br.com.cielo.libflue.util.FOUR
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.ERRO
import br.com.mobicare.cielo.commons.analytics.ESTABELECIMENTO_ENDERECO
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO
import br.com.mobicare.cielo.commons.analytics.SUCESSO
import br.com.mobicare.cielo.commons.constants.BR
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.OnGenericFragmentListener
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_BUTTON
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE_SCREEN
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_DEFAULT
import br.com.mobicare.cielo.commons.ui.success.SuccessBottomDialogFragment
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.anyErrorFields
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.utils.validateEmptyField
import br.com.mobicare.cielo.databinding.FragmentEditAddressUserBinding
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.domain.AddressUpdateRequest
import br.com.mobicare.cielo.meuCadastroNovo.domain.PurposeAddress
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.UserAddressContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.UserEditAddressPresenterImpl
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.HTTP_410
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class UserAddressEditFragment : BaseFragment(), OnGenericFragmentListener,
    UserAddressContract.UserEditAddressView {

    private var binding: FragmentEditAddressUserBinding? = null

    private val addressPresenter: UserEditAddressPresenterImpl by inject {
        parametersOf(this)
    }

    private val currentAddressToEdit: Address? by lazy {
        this.arguments?.getParcelable<Address?>(EXTRA_ADDRESS_TO_EDIT)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val compositeDisposableHandler = CompositeDisposableHandler()
    private val analytics: MeuCadastroAnalytics by inject()


    companion object {
        const val EXTRA_ADDRESS_TO_EDIT = "br.com.cielo.meuCadastro.extraAddressToEdit"
        const val CEP_MASK_FORMAT = "#####-###"
        const val SALE = "SALE"
        const val SUPPLY = "SUPPLY"
        const val CONTRACT = "CONTRACT"
        const val MAILING = "MAILING"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onResume() {
        super.onResume()
        compositeDisposableHandler.start()
        analytics.logUpdateAddressScreenView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentEditAddressUserBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentStatusListener?.onSetTitleToolbar(getString(R.string.text_user_edit_address_title))
        binding?.apply {
            editTextCepAddress.addTextChangedListener(
                editTextCepAddress
                    .getMask(CEP_MASK_FORMAT, editTextCepAddress)
            )
        }
        loadOlderAddress()

        configureTextInputChange()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun configureTextInputChange() {
        binding?.editTextCepAddress?.let {
            RxTextView.textChanges(it)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { sequence ->
                    (!TextUtils.isEmpty(sequence.toString().trim()) &&
                            sequence.length > EIGHT)
                }
                .subscribe({ cepSequence ->
                    addressPresenter.fetchAddressByCep(
                        cepSequence.toString().removeNonNumbers()
                    )
                }, { error ->
                    FirebaseCrashlytics.getInstance().recordException(error)
                })
        }?.let {
            compositeDisposableHandler.compositeDisposable
                .add(
                    it
                )
        }
    }

    private fun loadOlderAddress() {
        binding?.apply {
            currentAddressToEdit?.run {
                editTextCepAddress.text = SpannableStringBuilder.valueOf(this.zipCode ?: EMPTY)
                editTextAddresses.text = SpannableStringBuilder.valueOf(this.streetAddress ?: EMPTY)
                editTextAddressState.text = SpannableStringBuilder.valueOf(this.state ?: EMPTY)
                editTextAddressNumber.text = SpannableStringBuilder
                    .valueOf(this.number ?: EMPTY)
                editTextAddressCity.text = SpannableStringBuilder.valueOf(this.city ?: EMPTY)
                editTextAddressNeighborhood.text = SpannableStringBuilder
                    .valueOf(this.neighborhood ?: EMPTY)
                editTextAddressComplement.text = SpannableStringBuilder
                    .valueOf(this.complementAddress ?: EMPTY)

                fillTypeAddress(this.types)
            }
        }
    }

    override fun onReload() {

    }

    override fun onSaveButtonClicked() {
        binding?.apply {
            editTextCepAddress.validateEmptyField(
                textInputLayoutCepAddress,
                getString(R.string.text_error_cep_empty_not_allowed)
            )

            editTextAddressCity.validateEmptyField(
                textInputLayoutAddressCity,
                getString(R.string.text_error_city_empty_not_allowed)
            )

            editTextAddresses.validateEmptyField(
                textInputLayoutAddresses,
                getString(R.string.text_error_address_addresses_empty_not_allowed)
            )

            editTextAddressNeighborhood.validateEmptyField(
                textInputLayoutAddressNeighborhood,
                getString(R.string.text_error_address_neighborhood_empty_not_allowed)
            )

            editTextAddressNumber.validateEmptyField(
                textInputLayoutAddressNumber,
                getString(R.string.text_error_address_number_empty_not_allowed)
            )

            editTextAddressState.validateEmptyField(
                textInputLayoutAddressState,
                getString(R.string.text_error_address_state_empty_not_allowed)
            )

            clTypeContainer?.background = context?.let {
                ContextCompat.getDrawable(
                    it,
                    if (emptyTypeOfAddress())
                        R.drawable.background_error_dc392a
                    else
                        R.drawable.background_gray_c5ced7
                )
            }

            if (emptyTypeOfAddress()) {
                tvTypeAddressError.visible()
            } else {
                tvTypeAddressError.gone()
            }

            UserPreferences.getInstance().userInformation?.run userLoggedData@{

                if (!requireActivity().anyErrorFields(
                        textInputLayoutCepAddress,
                        textInputLayoutAddressCity,
                        textInputLayoutAddressNeighborhood,
                        textInputLayoutAddressNumber,
                        textInputLayoutAddressState
                    ) && !emptyTypeOfAddress()
                ) {

                    analytics.logUpdateAddressClick()

                    currentAddressToEdit?.run address@{
                        val addressUpdateRequest = AddressUpdateRequest(
                            id = this@address.id ?: EMPTY,
                            country = BR,
                            state = editTextAddressState.text.toString().trim(),
                            description = EMPTY,
                            streetAddress2 = editTextAddressComplement.text.toString().trim(),
                            city = editTextAddressCity.text.toString().trim(),
                            neighborhood = editTextAddressNeighborhood.text.toString().trim(),
                            streetAddress = editTextAddresses.text.toString().trim(),
                            number = editTextAddressNumber.text.toString().trim(),
                            zipCode = editTextCepAddress.text.toString().trim().removeNonNumbers(),
                            purposeAddress = getTypesAddressChecked()
                        )

                        validationTokenWrapper.generateOtp(
                            onResult = { otpCode ->
                                addressPresenter.updateAddress(
                                    otpCode,
                                    addressUpdateRequest
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun getTypesAddressChecked(): ArrayList<PurposeAddress> {
        val listPurposeAddress = ArrayList<PurposeAddress>()
        binding?.apply {
            if (cbPointSale.isChecked)
                listPurposeAddress.add(PurposeAddress(type = ONE))

            if (cbCorrespondence.isChecked)
                listPurposeAddress.add(PurposeAddress(type = TWO))

            if (cbSupplies.isChecked)
                listPurposeAddress.add(PurposeAddress(type = THREE))

            if (cbContract.isChecked)
                listPurposeAddress.add(PurposeAddress(type = FOUR))
        }

        return listPurposeAddress
    }

    private fun emptyTypeOfAddress(): Boolean {
        var emptyTypes = true

        binding?.apply {
            if (cbPointSale.isChecked
                || cbCorrespondence.isChecked
                || cbSupplies.isChecked || cbContract.isChecked
            ) {
                emptyTypes = false
            }
        }
        return emptyTypes
    }

    override fun showSuccessAddress() {

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_ENDERECO, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.text_update_address_result_title))
        )

        val fragment =
            SuccessBottomDialogFragment.create(SUCCESS_DEFAULT, Bundle().apply {
                putString(
                    SUCCESS_BUNDLE_TITLE,
                    getString(R.string.text_update_address_result_title)
                )
                putString(
                    SUCCESS_BUNDLE_TITLE_SCREEN,
                    getString(R.string.text_update_address_result_screen_title)
                )
                putString(SUCCESS_BUNDLE_BUTTON, getString(R.string.ok))
            }) {
                fragmentStatusListener?.onSuccess(EMPTY)
            }

        fragment.show(
            childFragmentManager,
            SuccessBottomDialogFragment::class.java.simpleName
        )
    }

    override fun addressUpdateError(updateAddressError: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_ENDERECO, Action.CALLBACK),
            label = listOf(ERRO, updateAddressError.errorMessage, updateAddressError.errorCode)
        )
        handleErrorWithMessage(
            updateAddressError, getString(R.string.text_address_update_error),
            getString(R.string.text_error_update_address_title)
        )
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            this.fragmentStatusListener?.onError(it)
        }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun showLoading() {
        this.fragmentStatusListener?.onShowLoading()
    }

    override fun hideLoading() {
        this.fragmentStatusListener?.onHideLoading()
    }

    private fun fillField(field: TypefaceEditTextView, text: String, enabled: Boolean) {
        if (isAttached()) {
            if (text.isNotEmpty()) {
                field.text = SpannableStringBuilder.valueOf(text)
                field.isEnabled = enabled
            }
        }
    }

    override fun fillAddressFields(addressReturn: AddressResponse) {
        binding?.apply {
            fillField(editTextAddresses, addressReturn.streetAddress.toString(), false)
            fillField(editTextAddressNeighborhood, addressReturn.neighborhood.toString(), false)
            fillField(editTextAddressCity, addressReturn.city.toString(), false)
            fillField(editTextAddressState, addressReturn.state.toString(), false)
            fillField(
                editTextAddressNumber, if (currentAddressToEdit?.number.isNullOrEmpty())
                     EMPTY else currentAddressToEdit?.number.toString(), true
            )
            fillField(
                editTextAddressComplement,
                if (currentAddressToEdit?.complementAddress.isNullOrEmpty())
                    EMPTY else currentAddressToEdit?.complementAddress.toString(),
                true
            )

            addressReturn.types?.let { fillTypeAddress(it) }
        }
    }

    private fun clearField(field: TypefaceEditTextView) {
        if (isAttached()) {
            field.text?.clear()
            field.isEnabled = true
        }
    }

    override fun clearAddressFields() {
        binding?.apply {
            clearField(editTextAddresses)
            clearField(editTextAddressNeighborhood)
            clearField(editTextAddressCity)
            clearField(editTextAddressState)
            clearField(editTextAddressComplement)
            clearField(editTextAddressNumber)
        }
    }

    override fun showCepError(cepFetchError: ErrorMessage) {

        if (isAttached()) {
            handleErrorWithMessage(
                cepFetchError,
                getString(R.string.text_invalid_cep),
                getString(R.string.text_cep_error_title)
            )
        }
    }

    private fun handleErrorWithMessage(
        cepFetchError: ErrorMessage,
        errorMessage: String,
        errorTitle: String
    ) {
        when (cepFetchError.httpStatus) {
            in HttpURLConnection.HTTP_CLIENT_TIMEOUT..HTTP_410 -> {
                logout(cepFetchError)
            }
            else -> {
                requireActivity().showMessage(
                    errorMessage,
                    errorTitle
                )
            }
        }
    }

    private fun fillTypeAddress(types: List<String?>) {
        binding?.apply {
            types.forEach {
                when (it.toString()) {
                    SALE -> fillCheckBox(cbPointSale)
                    MAILING -> fillCheckBox(cbCorrespondence)
                    CONTRACT -> fillCheckBox(cbContract)
                    else -> {
                        fillCheckBox(cbSupplies)
                    }
                }
            }
        }
    }

    private fun fillCheckBox(field: AppCompatCheckBox) {
        if (isAttached()) {
            field.isChecked = true
            field.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposableHandler.destroy()
    }

    override fun genericError(){
        requireActivity().showMessage(
            getString(R.string.text_message_generic_error),
            getString(R.string.text_title_generic_error)
        )
    }
}