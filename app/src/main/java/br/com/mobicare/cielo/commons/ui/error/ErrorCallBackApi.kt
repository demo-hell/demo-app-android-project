package br.com.mobicare.cielo.commons.ui.error

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment

/**
 * @author Enzo Teles
 * Friday, May 15, 2020
 * */
class ErrorCallBackApi constructor(fragment: BaseFragment) {
    private var fragment: BaseFragment
    private var code: Int? = null
    private var bottonSheet: BottomSheetGenericFragment?= null

    init {
        this.fragment  = fragment
    }

    fun code(code: Int?): ErrorCallBackApi {
        this.code = code
        return this
    }

    fun build(error: (BottomSheetGenericFragment) -> Unit) {

        when(this.code){
            420->{
                bottonSheet = fragment.bottomSheetGeneric(
                    fragment.getString(R.string.txt_aguardando_motoboy_unavailable),
                    R.drawable.ic_generic_error_service_unavailable,
                    fragment.getString(R.string.txt_title_motoboy_unavailable),
                    fragment.getString(R.string.txt_subtitle_motoboy_unavailable),
                    fragment.getString(R.string.text_close),
                    true,
                    false)

                bottonSheet?.show(fragment.requireActivity().supportFragmentManager, fragment.getString(R.string.bottom_sheet_generic))
            }
        }
        bottonSheet?.let { error(it) }
    }

}