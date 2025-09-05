package br.com.mobicare.cielo.accessManager.assignRole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentAccessManagerSelectRoleBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL

class AccessManagerSelectRoleFragment: BaseFragment(), CieloNavigationListener {
    private var navigation: CieloNavigation? = null
    val args: AccessManagerSelectRoleFragmentArgs by navArgs()
    private var binding: FragmentAccessManagerSelectRoleBinding? = null
    private val featureToggle = FeatureTogglePreference.instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAccessManagerSelectRoleBinding
            .inflate(inflater, container, false)
            .also { binding = it }
            .root

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
        binding?.apply {
            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            cardAdmin.setOnClickListener {
                findNavController().navigate(
                    AccessManagerSelectRoleFragmentDirections
                        .actionAccessManagerSelectRoleFragmentToAccessManagerAssignRoleFragment(
                            args.usersList,
                            ADMIN
                        )
                )
            }

            cardReader.setOnClickListener {
                findNavController().navigate(
                    AccessManagerSelectRoleFragmentDirections
                        .actionAccessManagerSelectRoleFragmentToAccessManagerAssignRoleFragment(
                            args.usersList,
                            READER
                        )
                )
            }

            cardAnalyst.setOnClickListener {
                findNavController().navigate(
                    AccessManagerSelectRoleFragmentDirections
                        .actionAccessManagerSelectRoleFragmentToAccessManagerAssignRoleFragment(
                            args.usersList,
                            ANALYST
                        )
                )
            }

            cardTechnical.visible(featureToggle.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO))

            cardTechnical.setOnClickListener {
                findNavController().navigate(
                    AccessManagerSelectRoleFragmentDirections
                        .actionAccessManagerSelectRoleFragmentToAccessManagerAssignRoleFragment(
                            args.usersList,
                            TECHNICAL
                        )
                )
            }
        }
    }
}