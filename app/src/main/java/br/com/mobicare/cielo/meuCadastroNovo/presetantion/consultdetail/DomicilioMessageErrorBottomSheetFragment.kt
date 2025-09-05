package br.com.mobicare.cielo.meuCadastroNovo.presetantion.consultdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.FragmentDomicilioMessageErrorBottomSheetBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.PaymentAccountsDomicileBrand
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val PAYMENT_ACCOUNT_DOMICILE_BRAND = "PaymentAccountsDomicileBrand"

class DomicilioMessageErrorBottomSheetFragment : BottomSheetDialogFragment() {

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var brand: PaymentAccountsDomicileBrand? = null

    private var binding: FragmentDomicilioMessageErrorBottomSheetBinding? = null

    companion object {
        @JvmStatic
        fun newInstance(brand: PaymentAccountsDomicileBrand, fragmentManager: FragmentManager) = DomicilioMessageErrorBottomSheetFragment()
                .apply {
                    arguments = Bundle().apply {
                        putParcelable(PAYMENT_ACCOUNT_DOMICILE_BRAND, brand)
                    }
                    this.show(fragmentManager, this::class.java.simpleName)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brand = it.getParcelable(PAYMENT_ACCOUNT_DOMICILE_BRAND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDomicilioMessageErrorBottomSheetBinding.inflate(layoutInflater)
        .also {
            binding = it
        }.root

    override fun onResume() {
        super.onResume()
        init()
    }

    fun init() {
        configureBottomSheet()
        brand?.let {
            binding?.apply {
                textViewTitle.text = when {
                    it.nameBrand.isNullOrBlank() -> {
                        it.status ?: ""
                    }

                    it.status.isNullOrBlank() -> {
                        it.nameBrand
                    }

                    else -> {
                        getString(R.string.x_dots_y, it.nameBrand, it.status)
                    }
                }

                val strBuilder =
                    StringBuilder(getString(R.string.detail_troca_domicilio_error_intro))
                it.messageReason?.forEach { message ->
                    if (message.isNotEmpty()) {
                        strBuilder.append("  • $message\n")
                    }
                }
                textViewSubtitle.text = strBuilder.toString()
            }
        }
    }


    private fun configureBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}