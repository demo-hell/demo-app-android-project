package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.constants.SEQUENCE_LENGTH
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAdress
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.instalacao_maquina_new_address_fragment.*
import kotlinx.android.synthetic.main.layout_modal_receba_mais.btn_rm_ok
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InstalacaoMaquinaChooseAddressBottomSheetFragment : BaseActivity(), InstalacaoMaquinaChooseAddressNewContract.View {

    private var actionEventListener: BaseView? = null
    private var enableCepRequest: Boolean = false

    private val presenter: InstalacaoMaquinaChooseaddressNewPresenter by inject {
        parametersOf(this)
    }

    private val compositeDisposableHandler = CompositeDisposableHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instalacao_maquina_new_address_fragment)

        btn_rm_close.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_from_up_to_bottom)
        }

        btn_rm_ok.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_from_up_to_bottom)
        }

        edittext_view_zipcode.addTextChangedListener(edittext_view_zipcode
                .getMask("#####-###", edittext_view_zipcode))


        btn_rm_ok.setOnClickListener {
            errorFieldCancel(text_view_zipcode_title)
            errorFieldCancel(text_view_state_title)
            errorFieldCancel(text_view_reference_point_title)
            errorFieldCancel(text_view_neighborhood_title)
            errorFieldCancel(text_view_city_title)
            errorFieldCancel(text_view_address_title)

            /*
              var streetAddress: String,
        var numberAddress: String,
        var zipcode: String,
        var referencePoint: String,
        var city: String,
        var neighborhood: String,
        var state: String
             */

            val addressObj = MachineInstallAddressObj(
                    edittext_view_address.text.toString(),
                    edittext_view_number.text.toString(),
                    "",
                    edittext_view_zipcode.text.toString(),
                    edittext_view_reference_point.text.toString(),
                    edittext_view_city.text.toString(),
                    edittext_view_neighborhood.text.toString(),
                    edittext_view_state.text.toString())

            presenter.nextStep(addressObj)
        }

        configureFocusChangeDetection()
        configureTextInputChange()

        configZipcode()
        configNumber()
        configAddress()
        configReferencePoint()
        configNeighborhood()
        configCity()
        configState()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_from_up_to_bottom)
    }

    //region Local Functions

    private fun configureFocusChangeDetection() {
        compositeDisposableHandler.compositeDisposable.add(
            RxView
                .focusChanges(edittext_view_zipcode)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ isFocusOn ->
                    enableCepRequest = isFocusOn
                    valueCEP = ""
                }, {

                })
        )
    }

    var valueCEP = ""
    private fun configureTextInputChange() {
        compositeDisposableHandler.compositeDisposable
            .add(RxTextView.textChanges(edittext_view_zipcode)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { sequence ->
                    (!TextUtils.isEmpty(sequence.toString().trim()) &&
                            sequence.length > SEQUENCE_LENGTH)
                }
                .subscribe({ cepSequence ->
                    if (enableCepRequest && valueCEP != cepSequence.toString().removeNonNumbers()) {
                        valueCEP = cepSequence.toString().removeNonNumbers()
                        presenter.fetchAddressByCep(valueCEP)
                    }
                }, {

                })
            )
    }


    private fun configAddress() {
        edittext_view_address.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_address_title)
        }
    }

    private fun configState() {
        edittext_view_state.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_state_title)
        }
    }

    private fun configCity() {
        edittext_view_city.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_city_title)
        }
    }

    private fun configNeighborhood() {
        edittext_view_neighborhood.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_neighborhood_title)
        }
    }

    private fun configReferencePoint() {
        edittext_view_reference_point.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_reference_point_title)
        }
    }

    private fun configNumber() {
        edittext_view_number.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_address_title)
        }
    }

    private fun configZipcode() {
        edittext_view_zipcode.afterTextChangesNotEmptySubscribe {
            errorFieldCancel(text_view_zipcode_title)
        }
    }
    //endregion

    //region InstalacaoMaquinaChooseAddressNewContract.View

    private fun fillField(field: TypefaceEditTextView, text: String) {
        if (isAttached()) {
            if (text.isNotEmpty()) {
                field.text = SpannableStringBuilder.valueOf(text)
                field.isEnabled = false
            }
        }
    }

    private fun clearField(field: TypefaceEditTextView) {
        if (isAttached()) {
            field.text?.clear()
            field.isEnabled = true
        }
    }

    override fun showAddress(addresses: List<CepAdress>?) {
        if (!addresses.isNullOrEmpty()) {
            val address = addresses.first()
            fillField(edittext_view_address, address.address)
            fillField(edittext_view_neighborhood, address.neighborhood)
            fillField(edittext_view_city, address.city)
            fillField(edittext_view_state, address.state)
        }
    }

    override fun clearAddressFields() {
        clearField(edittext_view_address)
        clearField(edittext_view_neighborhood)
        clearField(edittext_view_city)
        clearField(edittext_view_state)
        clearField(edittext_view_number)
        clearField(edittext_view_reference_point)
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached())
            hideLoading()
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached())
            actionEventListener?.logout(msg)
    }

    override fun showErrorZipcode() {
        if (isAttached())
            errorFieldShow(text_view_zipcode_title, getString(R.string.ins_tr_error_zipcode_empty_not_allowed))
    }

    override fun showErrorState() {
        if (isAttached())
            errorFieldShow(text_view_state_title, getString(R.string.ins_tr_error_state_empty_not_allowed))
    }

    override fun showErrorReferencePoint() {
        if (isAttached())
            errorFieldShow(text_view_reference_point_title, getString(R.string.ins_tr_error_reference_point_empty_not_allowed))
    }

    override fun showErrorNeighborhood() {
        if (isAttached())
            errorFieldShow(text_view_neighborhood_title, getString(R.string.ins_tr_error_neighborhood_empty_not_allowed))
    }

    override fun showErrorCity() {
        if (isAttached())
            errorFieldShow(text_view_city_title, getString(R.string.ins_tr_error_city_empty_not_allowed))
    }

    override fun showErrorAddress() {
        if (isAttached())
            errorFieldShow(text_view_address_title, getString(R.string.ins_tr_error_address_empty_not_allowed))
    }

    override fun showErrorNumberAddress() {
        if (isAttached())
            errorFieldShow(text_view_number_title, getString(R.string.ins_tr_error_number_empty_not_allowed))
    }

    override fun nextStep(addressObj: MachineInstallAddressObj) {

        val bundle = Bundle()
        bundle.putParcelable("result", addressObj)
        val it = Intent()
        it.putExtras(bundle)
        setResult(Activity.RESULT_OK, it)
        finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_from_up_to_bottom)
    }


    override fun showLoading() {
        nested_scroll_view.visibility = View.GONE
        progressLoadingLayout.visibility = View.VISIBLE
        edittext_view_address.requestFocus()
    }

    override fun hideLoading() {
        nested_scroll_view.visibility = View.VISIBLE
        progressLoadingLayout.visibility = View.GONE
    }
    //endregion


    private fun errorFieldShow(view: TextInputLayout, errorString: String) {
        if (isAttached()) {
            view.error = errorString
            view.isErrorEnabled = true
        }
    }

    private fun errorFieldCancel(view: TextInputLayout) {
        if (isAttached()) {
            view.error = null
            view.isErrorEnabled = false
        }
    }
}