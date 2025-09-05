package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_call_central.*


class CallCentralBSfrag: BottomSheetDialogFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.bottomsheet_call_central, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txt_phone_capital.setOnClickListener { callPhoneCapital() }
        txt_phone_local.setOnClickListener { callPhoneLocal() }
        img_close.setOnClickListener { dismiss() }

    }

    companion object {
        fun newInstance(): CallCentralBSfrag {
            return CallCentralBSfrag()
        }
    }

    private fun callPhoneLocal(){
        Utils.callPhone(requireActivity(), getString(R.string.txt_call_central_local))
    }

    private fun callPhoneCapital(){
        Utils.callPhone(requireActivity(), getString(R.string.txt_call_central_capital))
    }

}