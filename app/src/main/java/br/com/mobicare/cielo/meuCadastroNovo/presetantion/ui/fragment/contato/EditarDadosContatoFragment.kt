package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.ERRO
import br.com.mobicare.cielo.commons.analytics.ESTABELECIMENTO_CONTATO
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO
import br.com.mobicare.cielo.commons.analytics.SUCESSO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.listener.OnCommonActivityFragmentStatusListener
import br.com.mobicare.cielo.commons.listener.OnGenericFragmentListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_BUTTON
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE_SCREEN
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_DEFAULT
import br.com.mobicare.cielo.commons.ui.success.SuccessBottomDialogFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tieEmail
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tieNome
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tieTelefone1
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tieTelefone2
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tieTelefone3
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tilEmail
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tilNome
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tilTelefone1
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tilTelefone2
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_contato.tilTelefone3
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ARG_PARAM_CONTACT = "ARG_PARAM_CONTACT"


class EditarDadosContatoFragment : BaseFragment(), EditarDadosContatoContract.View, OnGenericFragmentListener {

    private val presenter: EditarDadosContatoPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    val analytics: MeuCadastroAnalytics by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.mcn_fragment_editar_dados_contato, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentStatusListener?.onSetTitleToolbar("Editar dados de contato")
        configureFields()
        loadArguments()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCommonActivityFragmentStatusListener) {
            this.fragmentStatusListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.fragmentStatusListener = null
    }

    private fun loadArguments() {
        this.arguments?.also { itArguments ->
            itArguments.getParcelable<Contact>(ARG_PARAM_CONTACT)?.let { itContact ->
                this.presenter.putContactData(itContact)
            }
        }
    }

    override fun showError(error: ErrorMessage?) {

        error?.let {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(ESTABELECIMENTO_CONTATO, Action.CALLBACK),
                label = listOf(ERRO, it.errorMessage, it.errorCode)
            )

            if (it.httpStatus == 500) {
                this.fragmentStatusListener?.onError()
            } else {
                AlertDialogCustom.Builder(this.context, getString(R.string.ga_login))
                        .setTitle("Editar Contatos")
                        .setMessage(it.statusText)
                        .setBtnRight(getString(R.string.ok))
                        .show()
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        this.fragmentStatusListener?.onExpiredSession()
    }

    override fun showContactData(contact: Contact) {
        contact.let {
            tieNome.setText(it.name)

            it.email?.let { itEmail ->
                this.tieEmail.setText(itEmail)
            }

            this.tilTelefone1.visibility = View.GONE
            this.tilTelefone2.visibility = View.GONE
            this.tilTelefone3.visibility = View.GONE

            if (!contact.phones.isNullOrEmpty()) {
                for (idx in 0 until contact.phones.size) {
                    val phone = contact.phones[idx]
                    val numberFormatted =
                        EditTextHelper.phoneMaskFormatter("${phone.areaCode ?: ""}${phone.number}").formattedText.string
                    when (idx) {
                        0 -> setTextAndVisible(tilTelefone1, numberFormatted, true)
                        1 -> setTextAndVisible(tilTelefone2, numberFormatted, true)
                        2 -> setTextAndVisible(tilTelefone3, numberFormatted, true)
                    }
                }
            }
        }
    }

    private fun setTextAndVisible(til: TextInputLayout, text: String, isVisible: Boolean) {
        if (isVisible) {
            til.editText?.setText(text)
            til.visibility = View.VISIBLE
        } else {
            til.visibility = View.GONE
        }
    }

    private fun configureFields() {
        configureTelefoneFields()
        configureEmailField()
    }


    private fun configureTelefoneFields() {
        EditTextHelper.phoneField(this.tilTelefone1, this.tieTelefone1)
        EditTextHelper.phoneField(this.tilTelefone2, this.tieTelefone2)
        EditTextHelper.phoneField(this.tilTelefone3, this.tieTelefone3)
    }

    private fun configureEmailField() {
        EditTextHelper.emailField(this.tilEmail, this.tieEmail)
    }

    override fun onReload() {
        this.onSaveButtonClicked()
    }

    override fun onSaveButtonClicked() {

        validationTokenWrapper.generateOtp(
            onResult = { otpCode ->
                this.presenter.save(
                    this.tieNome.text.toString(),
                    this.tieEmail.text.toString(),
                    this.tieTelefone1.text.toString(),
                    this.tieTelefone2.text.toString(),
                    this.tieTelefone3.text.toString(),
                    otpCode,
                )
            }
        )
    }

    override fun logout() {
        this.fragmentStatusListener?.onExpiredSession()
    }

    override fun showEmailFillError(isShow: Boolean) {
        tilEmail.error = when (isShow) {
            true -> "Por favor, é preciso preencher o email"
            else -> null
        }
    }

    override fun showPhoneFillError(idx: Int, @StringRes stringResId: Int?) {
        var message: String? = null
        stringResId?.let {
            message = this.context?.getString(it)
        }
        when (idx) {
            0 -> tilTelefone1.error = message
            1 -> tilTelefone2.error = message
            2 -> tilTelefone3.error = message
        }
    }

    override fun showNameFillError(isShow: Boolean) {
        tilNome.error = when (isShow) {
            true -> "Por favor, é preciso preencher o nome"
            else -> null
        }
    }

    override fun showInvalidEmail(isShow: Boolean) {
        tilEmail.error = when (isShow) {
            true -> "Por favor, digite um email válido"
            else -> null
        }
    }

    override fun showLoading() {
        this.fragmentStatusListener?.onShowLoading()
    }

    override fun hideLoading() {
        this.fragmentStatusListener?.onHideLoading()
    }

    override fun showSaveSuccessful() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_CONTATO, Action.CALLBACK),
            label = listOf(SUCESSO, "Dados de contato alterados com sucesso!")
        )

        val bundle = Bundle()
        bundle.putString(SUCCESS_BUNDLE_TITLE, "Dados de contato alterados com sucesso!")
        bundle.putString(SUCCESS_BUNDLE_TITLE_SCREEN, "Alterar dados de contato")
        bundle.putString(SUCCESS_BUNDLE_BUTTON, "OK")

        val fragment =
                SuccessBottomDialogFragment.create(SUCCESS_DEFAULT, bundle) {
                    fragmentStatusListener?.onSuccess("")
                }

        fragment.show(childFragmentManager, "BottomSheetDialogFragment")
    }
}