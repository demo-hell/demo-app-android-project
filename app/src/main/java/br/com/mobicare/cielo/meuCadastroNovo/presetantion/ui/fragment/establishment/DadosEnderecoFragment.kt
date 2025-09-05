package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.commons.CommonActivityWithFragment
import br.com.mobicare.cielo.commons.EXTRA_PARAM_FRAGMENT
import br.com.mobicare.cielo.commons.EXTRA_PARAM_OBJECT
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.collapse
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.commons.utils.expand
import br.com.mobicare.cielo.databinding.McnFragmentDadosEnderecoBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.MeuCadastroNovoAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.UserAddressesAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ESTABLISHMENT_ANALYTICS
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MY_REGISTER
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_0f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_90f

class DadosEnderecoFragment : BaseFragment(), ShowLayoutListener {

    private lateinit var addresses: ArrayList<Address>
    private var isEditBlocked = false

    private lateinit var _binding: McnFragmentDadosEnderecoBinding
    val binding: McnFragmentDadosEnderecoBinding get() = _binding

    var dataUpdateListener: MeuCadastroNovoAdapter.DataUpdateListener? = null

    private val updatedOkBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dataUpdateListener?.onDataUpdated()
        }
    }

    companion object {
        const val ADDRESS = "address"
        const val EDIT_BLOCK = "edit_block"
        fun create(addresses: ArrayList<Address>, isEditBlocked: Boolean) = DadosEnderecoFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(ADDRESS, addresses)
                putBoolean(EDIT_BLOCK, isEditBlocked)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)

        arguments?.let {
            it.getParcelableArrayList<Address>(ADDRESS)?.let { itAddresses ->
                addresses = itAddresses
            }
            isEditBlocked = it.getBoolean(EDIT_BLOCK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = McnFragmentDadosEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerUserAddresses.setHasFixedSize(true)
            recyclerUserAddresses.layoutManager = LinearLayoutManager(requireContext())
            recyclerUserAddresses.adapter = UserAddressesAdapter(addresses, isEditBlocked).apply {
                onAddressSelectedListener = object : UserAddressesAdapter
                .OnAddressSelectedListener {

                    override fun onAddressSelected(currentAddress: Address) {
                        startActivity(Intent(
                            requireContext(),
                            CommonActivityWithFragment::class.java
                        ).apply {
                            putExtra(
                                EXTRA_PARAM_FRAGMENT, UserAddressEditFragment::class.java
                                    .name
                            )
                            putExtra(EXTRA_PARAM_OBJECT, Bundle().apply {
                                putParcelable(
                                    UserAddressEditFragment.EXTRA_ADDRESS_TO_EDIT,
                                    currentAddress
                                )
                            })
                        })
                    }
                }
            }
            constraintViewDetailsAddress.gone()
            constraintViewTitleEnd.setOnClickListener {
                if (constraintViewDetailsAddress.visibility == View.VISIBLE) {
                    gaSendInteraction(textTitle.text.toString())

                    imageViewAddressArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
                    constraintViewDetailsAddress.collapse()
                } else {
                    constraintViewDetailsAddress.expand()
                    imageViewAddressArrow.animate()?.rotation(VALUE_ROTATION_90f)?.start()
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                updatedOkBroadcastReceiver,
                IntentFilter(CommonActivityWithFragment.FINISH_OK)
            )
    }

    private fun gaSendInteraction(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MY_REGISTER),
                action = listOf(ESTABLISHMENT_ANALYTICS),
                label = listOf(Label.INTERACAO, labelButton)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(updatedOkBroadcastReceiver)
    }

    override fun closeContainer() {
        binding.apply {
            constraintViewDetailsAddress.gone()
            imageViewAddressArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
        }
    }
}