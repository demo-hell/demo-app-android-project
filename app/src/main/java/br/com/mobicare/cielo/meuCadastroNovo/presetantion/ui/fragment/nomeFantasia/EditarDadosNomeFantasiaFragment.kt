package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia

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
import br.com.mobicare.cielo.commons.analytics.ESTABELECIMENTO_NOME
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO_NOME_FANTASIA
import br.com.mobicare.cielo.commons.analytics.SUCESSO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.OnCommonActivityFragmentStatusListener
import br.com.mobicare.cielo.commons.listener.OnGenericFragmentListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_BUTTON
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_BUNDLE_TITLE_SCREEN
import br.com.mobicare.cielo.commons.ui.success.SUCCESS_DEFAULT
import br.com.mobicare.cielo.commons.ui.success.SuccessBottomDialogFragment
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_nome_fantasia.text_view_description
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_nome_fantasia.text_view_name_1
import kotlinx.android.synthetic.main.mcn_fragment_editar_dados_nome_fantasia.text_view_name_2
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ARG_PARAM_FANTASY_NAME = "ARG_PARAM_FANTASY_NAME"

class EditarDadosNomeFantasiaFragment : BaseFragment(), EditarDadosNomeFantasiaContract.View, OnGenericFragmentListener {

    private val analytics: MeuCadastroAnalytics by inject()

    val presenterNomeFantasia : EditarDadosNomeFantasiaPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.mcn_fragment_editar_dados_nome_fantasia, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doWhenResumed {
            analytics.logUpdateFantasyNameScreenView()
        }
        fragmentStatusListener?.onSetTitleToolbar("Alterar Nome Fantasia")

        this.arguments?.also { itArguments ->
            itArguments.getParcelable<MCMerchantResponse>(ARG_PARAM_FANTASY_NAME)?.let {
                text_view_name_1.text = it.tradingName
            }
        }

        text_view_description.text = configureSubtitle()




        presenterNomeFantasia.loadReceitaFederal()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCommonActivityFragmentStatusListener) {
            this.fragmentStatusListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        presenterNomeFantasia.onCleared()
        this.fragmentStatusListener = null
    }


    private fun configureSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append("- Para alterar o Nome Fantasia cadastrado na Cielo ligue em nossa"
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Text12sp_500Sans_516c7b)))
        text.append(" ")

        text.append("Central de Relacionamento"
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Text12sp_900Sans_516c7b)))

        return text
    }


    //region EditarDadosNomeFatasiaContract.View

    override fun showReceitaFederal(name: String) {
        text_view_name_2.text = name
    }

    override fun showUpdateError() {
        fragmentStatusListener?.onError()
    }

    override fun showUpdateSuccess() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_NOME, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.meu_cadastro_nome_fantasia_alterado))
        )

        this.presenterNomeFantasia.onCleared()
        val bundle = Bundle()
        bundle.putString(SUCCESS_BUNDLE_TITLE , getString(R.string.meu_cadastro_nome_fantasia_alterado) )
        bundle.putString(SUCCESS_BUNDLE_TITLE_SCREEN , getString(R.string.meu_cadastro_nome_fantasia_alterar) )
        bundle.putString(SUCCESS_BUNDLE_BUTTON , "OK" )

        val fragment =
                SuccessBottomDialogFragment.create(SUCCESS_DEFAULT, bundle ) {
                    fragmentStatusListener?.onSuccess("")
                }

        fragment.show(childFragmentManager, "BottomSheetDialogFragment")
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(ESTABELECIMENTO_NOME, Action.CALLBACK),
            label = listOf(ERRO, it.errorMessage, it.errorCode)
        )

        if (it.httpStatus == 420) {
            it.errorMessage.let {message ->
                it.message = message
            }
            fragmentStatusListener?.onErrorAndClose(it)
        }
        else {
            fragmentStatusListener?.onErrorHandlerRetryWithMessage(it)
        }
    }

    }

    override fun logout(msg: ErrorMessage?) {
       fragmentStatusListener?.onExpiredSession()
    }

    override fun hideLoading() {
        super.hideLoading()
        fragmentStatusListener?.onHideLoading()
    }

    override fun showLoading() {
        super.showLoading()
        fragmentStatusListener?.onShowLoading()
    }

    //endregion
    
    //region OnGenericFragmentListener
    override fun onReload() {
        presenterNomeFantasia.resubmit()
    }

    override fun onSaveButtonClicked() {
        presenterNomeFantasia.saveNameReceitaFederal()
        analytics.logUpdateFantasyNameClick()
    }

    //ga
    private fun gaSendInteracao() {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(MEUS_CADASTRO_NOME_FANTASIA),
                label = listOf(Label.BOTAO, "salvar")
            )
        }
    }
}