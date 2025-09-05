package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.ERRO
import br.com.mobicare.cielo.commons.analytics.ESTABELECIMENTO_OWNER
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
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.bottomText
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.scrollLayout
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tieEmail
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tieTelefone1
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tieTelefone2
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tieTelefone3
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tilEmail
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tilTelefone1
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tilTelefone2
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_proprietario.tilTelefone3
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ARG_PARAM_OWNER = "ARG_PARAM_OWNER"

class EditarDadosProprietarioFragment : BaseFragment(), EditarDadosProprietarioContract.View, OnGenericFragmentListener {

    private val presenter: EditarDadosProprietarioPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.mcn_fragment_editar_dados_proprietario, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentStatusListener?.onSetTitleToolbar(getString(R.string.title_owner_data))
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
            itArguments.getParcelable<Owner>(ARG_PARAM_OWNER)?.let { itOwner ->
                this.presenter.putOwnerData(itOwner)
            }
        }
    }

    private fun configureFields() {
        configureTelefoneFields()
        configureEmailField()
        configureBottomText()

    }

    private fun configureTelefoneFields() {
        EditTextHelper.phoneField(this.tilTelefone1, this.tieTelefone1)
        EditTextHelper.phoneField(this.tilTelefone2, this.tieTelefone2)
        EditTextHelper.phoneField(this.tilTelefone3, this.tieTelefone3)
    }

    private fun configureEmailField() {
        EditTextHelper.emailField(this.tilEmail, this.tieEmail)
    }

    private fun configureBottomText() {
        val text = SpannableStringBuilder()
        text.append("Os dados Nome, CPF e Data de nascimento poderão ser editados somente pela ".addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.Text14sp_500Sans_758e9d)))
        text.append("central de relacionamento".addSpannable(TextAppearanceSpan(requireActivity(),
                R.style.Text14sp_500Sans_000000)))
        this.bottomText.text = text
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(ESTABELECIMENTO_OWNER, Action.CALLBACK),
                label = listOf(ERRO, it.errorMessage, it.errorCode)
            )
        }

        this.fragmentStatusListener?.onError()
    }

    override fun logout() {
        this.fragmentStatusListener?.onExpiredSession()
    }

    override fun logout(msg: ErrorMessage?) {
        this.fragmentStatusListener?.onExpiredSession()
    }

    override fun onReload() {
        this.onSaveButtonClicked()
    }

    override fun hideLoading() {
        this.fragmentStatusListener?.onHideLoading()
    }

    override fun showLoading() {
        this.fragmentStatusListener?.onShowLoading()
    }

    override fun onSaveButtonClicked() {

        validationTokenWrapper.generateOtp(
            onResult = { otpCode ->
                this.presenter.save(
                    otpCode,
                    this.tieEmail.text.toString(),
                    this.tieTelefone1.text.toString(),
                    this.tieTelefone2.text.toString(),
                    this.tieTelefone3.text.toString()
                )
            }
        )
    }

    override fun showOwnerData(owner: Owner) {
        owner.let {
            it.email?.let { itEmail ->
                this.tieEmail.setText(itEmail)
            }
            if (!owner.phones.isNullOrEmpty()) {
                for (idx in 0 until owner.phones.size) {
                    val phone = owner.phones[idx]
                    val numberFormatted =
                        EditTextHelper.phoneMaskFormatter("${phone.areaCode ?: ""}${phone.number}").formattedText.string
                    when (idx) {
                        0 -> tieTelefone1.setText(numberFormatted)
                        1 -> tieTelefone2.setText(numberFormatted)
                        2 -> tieTelefone3.setText(numberFormatted)
                    }
                }
            }
        }
    }

    override fun showPhoneFillError(isShow: Boolean) {
        if (isShow)
            this.scrollLayout?.requestChildFocus(this.tilTelefone1, this.tieTelefone1)

        tilTelefone1.error = when (isShow) {
            true -> "Por favor, é preciso preencher um telefone"
            else -> null
        }
    }

    override fun showEmailFillError(isShow: Boolean) {
        if (isShow)
            this.scrollLayout?.requestChildFocus(tilEmail, tilEmail)

        tilEmail.error = when (isShow) {
            true -> "Por favor, é preciso preencher o email"
            else -> null
        }
    }

    override fun showSaveSuccessful() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_OWNER, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.meu_cadastro_dados_proprietario_alterado))
        )

        val bundle = Bundle()
        bundle.putString(SUCCESS_BUNDLE_TITLE, getString(R.string.meu_cadastro_dados_proprietario_alterado))
        bundle.putString(SUCCESS_BUNDLE_TITLE_SCREEN, getString(R.string.meu_cadastro_dados_proprietario_alterar))
        bundle.putString(SUCCESS_BUNDLE_BUTTON, "OK")

        val fragment =
                SuccessBottomDialogFragment.create(SUCCESS_DEFAULT, bundle) {
                    fragmentStatusListener?.onSuccess("")
                }

        fragment.show(childFragmentManager, "BottomSheetDialogFragment")
    }
}