package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.categories.CentralAjudaCategoriesFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.CentralAjudaContatosFragment
import br.com.mobicare.cielo.centralDeAjuda.search.HelpCenterSearchActivity
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.contactCielo.domain.ContactCieloViewModel
import br.com.mobicare.cielo.databinding.CentralAjudaLogadoMainFragmentBinding
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

@Keep
class CentralAjudaLogadoFragment : BaseFragment() {

    private val binding: CentralAjudaLogadoMainFragmentBinding by viewBinding()

    private var logoutListener: LogoutListener? = null

    private val contactCieloViewModel: ContactCieloViewModel by viewModel()

    companion object {
        fun create(logoutListener: LogoutListener): CentralAjudaLogadoFragment {
            val fragment = CentralAjudaLogadoFragment()
            fragment.logoutListener = logoutListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactCieloViewModel.retrieveContactSourceInfo()
        binding.questionSearch.setOnClickListener {
            requireActivity().startActivity<HelpCenterSearchActivity>()
        }

        CentralAjudaCategoriesFragment.create(logoutListener)
            .addInFrame(childFragmentManager, R.id.fragment_categories)

        MostFrequentQuestionsFragment.create().apply {
            this.onLogoutListener = logoutListener
        }.addInFrame(
            childFragmentManager,
            R.id.frameMostFrequentQuestions
        )

        NewTechnicalSupportFragment.create(logoutListener, false).addInFrame(
            childFragmentManager,
            R.id.frameTechnicalSupportV2
        )

        CentralAjudaContatosFragment.create(logoutListener)
            .addInFrame(childFragmentManager, R.id.fragment_contacts)

        showChat()

        this.configureToolbarActionListener?.changeTo(
            title =
            getString(R.string.menu_central_ajuda)
        )
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
    }

    private fun trackScreenView() {
        if (isAttached()) {
            GA4.logScreenView(TechnicalSupportAnalytics.ScreenView.HELP_CENTER)
        }
    }

    private fun showChat() {
        val isToShowChat: Boolean =
            FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.CHAT)


        val frameLayouts = ArrayList<Int>()
        frameLayouts.add(R.id.fragment_chat)
        frameLayouts.add(R.id.fragment_whatsApp)
        frameLayouts.add(R.id.fragment_gerente_virtual)

        var idxLayout = ZERO

        if (isToShowChat) {
            binding.textViewCentralAtendimento.visible()
            binding.fragmentChat.visible()
            CentralAjudaLogadoChat().addInFrame(childFragmentManager, frameLayouts[idxLayout++])
        } else {
            binding.fragmentChat.gone()
        }
        contactCieloViewModel.contactInfoSource.observe(viewLifecycleOwner) { contactCieloWhatsappList ->
            if (contactCieloWhatsappList.isNullOrEmpty()) {
                binding.textViewCentralAtendimento.gone()
            } else {
                binding.textViewCentralAtendimento.visible()
                contactCieloWhatsappList.forEach { contactCieloWhatsapp ->
                    when (contactCieloWhatsapp.baseFragment) {
                        is CentralAjudaLogadoDuvidasGeraisFragment -> {
                            binding.fragmentWhatsApp.visible()
                            contactCieloWhatsapp.baseFragment
                                .addInFrame(childFragmentManager, R.id.fragment_whatsApp)
                        }

                        is CentralAjudaLogadoVirtualManagerFragment -> {
                            binding.fragmentGerenteVirtual.visible()
                            contactCieloWhatsapp.baseFragment
                                .addInFrame(childFragmentManager, R.id.fragment_gerente_virtual)
                        }
                    }
                }
            }
        }
    }
}
