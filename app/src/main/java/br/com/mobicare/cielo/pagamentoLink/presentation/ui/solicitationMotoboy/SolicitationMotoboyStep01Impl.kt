package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.error.ErrorCallBackApi
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import kotlinx.android.synthetic.main.fragment_solicitation_motoboy_step01.*
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author Enzo Teles
 * date 07/05/2020
 * */
class SolicitationMotoboyStep01Impl : BaseFragment(),
    CieloNavigationListener, SolicitationMotoboyView {

    lateinit var orderId: String
    private var cieloNavigation: CieloNavigation? = null
    var btResponseMotoboy: BottomSheetGenericFragment? = null
    private val presenter: SolicitationMotoboyPresenterImpl by inject {
        parametersOf(this)
    }

    /**
     * método onCreateView
     * */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_solicitation_motoboy_step01, container, false)


    /**
     * método onViewCreated
     * @param view
     * @param savedInstanceState
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadParams()
        presenter.initView()
    }

    /**
     * método para pegar o id da ordem via arguments
     * */
    override fun loadParams() {
        this.arguments?.let {
            orderId = SolicitationMotoboyStep01ImplArgs.fromBundle(it).orderId
        }

    }

    /**
     * método para carregar componentes da view
     * */
    override fun initView() {
        configureNavigation()
        retryButton.setText(getString(R.string.btn_solicitation_motoboy))
        retryButton.setOnClickListener {
            presenter.callBottonSheetGeneric(false)
        }
        errorLayout.configureActionClickListener(View.OnClickListener {
            presenter.callBottonSheetGeneric(false)
        })
    }

    /**
     * método para fazer a chamada do motoboy via api
     * @param isShowModalMotoboy
     * */
    override fun callBottonSheetGeneric(isShowModalMotoboy: Boolean) {
        displayedChild(1)
        if(isShowModalMotoboy){
            presenter.callLoadMotoboy()
        }
        orderId?.let {
            presenter.callMotoboy(orderId)
        }
    }

    /**
     * método que retorna a msg de sucesso da api do motoboy
     * @param responseMotoboy
     * */
    override fun responseMotoboy(
        responseMotoboy: ResponseMotoboy,
        isResendMotoboy: Boolean
    ) {
        if (isAttached())
            presenter.statusCodeMotoboy(responseMotoboy, isResendMotoboy)
    }

    /**
     * método configurar o estilos do fragmento via navigation listener
     * */
    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.title_tela_solicitar_motoboy))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    /**
     * método sessão expirada
     * */
    override fun expiredSession() {
        if (isAttached()){
            presenter.closeDialog()
        }
    }

    /**
     * método de error 500
     * */
    override fun serverError() {
        if (isAttached()) {
            displayedChild(2)
            presenter.closeDialog()
        }
    }

    /**
     * método de error 420
     * */
    override fun enhance() {
        if (isAttached()) {
            displayedChild(0)
            ErrorCallBackApi(this)
                .code(HTTP_ENHANCE)
                .build() {
                    it.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnClose(dialog: Dialog) {
                            presenter.delayCloseLocatesScreen(dialog)
                        }
                    }
                }
            presenter.closeDialog()
        }
    }

    /**
     * método que mostra o retorno 200 da chamada do motoboy na api
     * */
    override fun responseCallMotoboy(motoboy: ResponseMotoboy) {
        if(isAttached()){
            presenter.statusCodeMotoboy(motoboy, false)
        }
    }

    /**
     * método que fecha do modal de time da tela do motoboy
     * */
    override fun closeDialog() {
        btResponseMotoboy?.let {
            it.closeDialog()
        }
    }

    /**
     * método para tratar o error 404
     * */
    override fun notFound() {
        if(isAttached()){
            displayedChild(0)
            bottomSheetGeneric(
                getString(R.string.txt_topbar_name_error_404),
                R.drawable.ic_generic_error_image,
                getString(R.string.txt_title_error_404),
                getString(R.string.txt_subtitle_error_404),
                getString(R.string.btn_name_motoboy),
                true,
                false
            ).apply {
                this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnClose(dialog: Dialog) {
                        presenter.delayCloseLocatesScreen(dialog)
                    }
                }
            }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
            presenter.closeDialog()
        }
    }

    /**
     * método para mostrar quando o motoboy é localizado
     * o status para mostrar essa tela são
     * ALLOCATED_DELIVERER, STARTED, FINESHED, FINESHED_WITH_ERROR
     * */
    override fun screenLocated(motoboy: ResponseMotoboy) {
        if(isAttached()){
            displayedChild(0)
            bottomSheetGeneric(
               getString(R.string.txt_aguardando_motoboy),
               R.drawable.ic_success_user_loan,
               getString(R.string.txt_title_motoboy),
               getString(R.string.txt_subtitle_motoboy),
               getString(R.string.btn_name_motoboy),
               true,
               true
           ).apply {
               this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                   override fun onBtnClose(dialog: Dialog) {
                       presenter.delayCloseLocatesScreen(dialog)
                   }

                   override fun onBtnOk(dialog: Dialog) {

                       presenter.delayCloseLocatesScreen(dialog)
                       presenter.openBrowser(motoboy.trackingUrl)


                   }
               }
           }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
            presenter.closeDialog()
        }

    }

    /**
     * método para apresentar quando a coleta foi cancelada
     * o status para mostrar essa tela são
     * ERROR_CANCELLATION_PENDING, CANCELLED
     * */
    override fun collectionCanceled() {
        if(isAttached()){
            bottomSheetGeneric(
                getString(R.string.txt_topbar_name_collection_canceled),
                R.drawable.ic_generic_error_image,
                getString(R.string.txt_title_collection_canceled),
                getString(R.string.txt_subtitle_collection_canceled),
                getString(R.string.btn_name_motoboy),
                true,
                false
            ).apply {
                this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnClose(dialog: Dialog) {
                        presenter.delayCloseLocatesScreen(dialog)
                    }
                }
            }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
            presenter.closeDialog()
        }
    }

    /**
     * método para mostrar a tela com o cronometro e por trás fica tentando localizar um motoboy
     *
     * */
    override fun callLoadMotoboy(isResendMotoboy: Boolean) {
        if(!isResendMotoboy){
            btResponseMotoboy = bottomSheetGeneric(
                getString(R.string.txt_topbar_name_chronometer),
                R.drawable.ic_generic_relogio,
                getString(R.string.txt_title_chronometer),
                getString(R.string.txt_subtitle_chronometer),
                getString(R.string.btn_retornar_painel),
                true,
                false,
                true,
                TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                TxtTitleStyle.TXT_TITLE_BLUE,
                TxtSubTitleStyle.TXT_SUBTITLE_GREEN,
                ButtonBottomStyle.BNT_BOTTOM_WHITE
            ).apply {
                this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnClose(dialog: Dialog) {
                        presenter.delayCloseLocatesScreen(dialog)
                    }
                }
            }
            btResponseMotoboy?.let {
                it.show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
        orderId?.let {
            presenter.resendCallMotoboy(orderId)
        }
    }

    /**
     * método que controla o status da tela pelo view fliper
     * */
    override fun displayedChild(value: Int){
        vf_motoboy.displayedChild = value
    }

    /**
     * método que abre o link no browser via device
     * @param trackingUrl
     * */
    override fun openTrackingUrl(trackingUrl: String) {
        requireActivity().browse(trackingUrl)
    }

    /**
     * método para dar um backpress e um dimiss no dialog
     * */
    override fun delayCloseLocatesScreen(dialog: Dialog) {
        findNavController().navigateUp()
        dialog.dismiss()
    }

    /**
     * método para voltar para tela de detalhes do pedido
     * */
    override fun navigateUp() {
        findNavController().navigateUp()
        closeDialog()
    }

}