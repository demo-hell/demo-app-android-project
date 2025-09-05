package br.com.mobicare.cielo.pix.ui.qrCode.charge.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixQrCodeChangeKeyBinding
import br.com.mobicare.cielo.pix.constants.PIX_SELECTED_KEY_ARGS
import br.com.mobicare.cielo.pix.domain.MyKey

class PixQRCodeChangeKeyFragment : BaseFragment(), CieloNavigationListener,
    PixQRCodeMyKeysAdapterListener {

    private var newKey: MyKey? = null

    private var _binding: FragmentPixQrCodeChangeKeyBinding? = null

    private val binding get() = _binding

    private var navigation: CieloNavigation? = null

    private val selectedKey: MyKey? by lazy {
        arguments?.getParcelable(PIX_SELECTED_KEY_ARGS)
    }
    private var keys: List<MyKey>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixQrCodeChangeKeyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        keys = getMyKeys()
        newKey = selectedKey
        setupView()
    }

    private fun setupView() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding?.rvMyKeys?.adapter = context?.let { context ->
            keys?.let {
                val adapter = PixQRCodeMyKeysAdapter(context, it, this)
                adapter.setInitialSelectedKey(selectedKey)
                adapter
            }
        }
    }

    private fun getMyKeys(): List<MyKey>? = navigation?.getData() as? List<MyKey>

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_text_toolbar_generate_qr_code))
            navigation?.setTextButton(getString(R.string.screen_text_qr_code_change_key_save))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.enableButton(isEnabled = true)
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    override fun onButtonClicked(labelButton: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PIX_SELECTED_KEY_ARGS,
            newKey as MyKey
        )
        findNavController().popBackStack()
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }

    override fun handleClick(key: MyKey) {
        newKey = key
    }
}