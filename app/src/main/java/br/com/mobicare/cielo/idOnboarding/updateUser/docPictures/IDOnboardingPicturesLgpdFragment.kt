package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingPicturesLgpdBinding

class IDOnboardingPicturesLgpdFragment: BaseFragment(), CieloNavigationListener {

    private lateinit var binding: FragmentIdOnboardingPicturesLgpdBinding
    private var navigation: CieloNavigation? = null
    private var checked = false

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? =
//        inflater.inflate(R.layout.fragment_id_onboarding_pictures_lgpd, container, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdOnboardingPicturesLgpdBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

            ibCheck.setOnClickListener {
                checked = checked.not()
                ibCheck.setImageResource(
                    if (checked) R.drawable.ic_check_circle_green_selected
                    else R.drawable.ic_check_circle_white_unselected
                )
                btNext.isEnabled = checked
            }

            btLgpdLink.setOnClickListener {}

            btNext.setOnClickListener {}
        }
    }
}