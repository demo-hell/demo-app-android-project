package br.com.mobicare.cielo.elopat

import android.os.Bundle
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.router.MENU_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.databinding.FragmentEloPatBinding
import br.com.mobicare.cielo.main.domain.Menu

class EloPatFragment : BaseFragment() {
    private var _binding: FragmentEloPatBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEloPatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        extractMenuFromArguments()
        setupView()
        setupButtons()
    }

    private fun extractMenuFromArguments() {
        arguments?.let {
            this.menu = it.getParcelable(MENU_ROUTER_FRAGMENT) as Menu?
        }
    }

    private fun setupView() {
        binding.customHandlerView.apply {
            title = (getString(R.string.elo_pat_unavailable_title))
            message = (getString(R.string.elo_pat_unavailable_text))
            isShowIconButtonEndHeader = false
            cardInformationData = null
        }
    }

    private fun setupButtons() {
        binding.customHandlerView.apply {
            labelPrimaryButton = getString(R.string.elo_pat_go_to_site_button)
            setOnPrimaryButtonClickListener {
                Utils.openBrowser(requireActivity(), menu?.menuTarget?.url)
            }

            labelSecondaryButton = getString(R.string.elo_pat_back_button)
            setOnSecondaryButtonClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
