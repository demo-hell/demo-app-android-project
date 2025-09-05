package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.recycler.MarginItemDecoration
import br.com.mobicare.cielo.databinding.McnFragmentDadosContatoBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.ContatosAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ESTABLISHMENT_ANALYTICS
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MY_REGISTER
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_0f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_90f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosProprietarioFragment.Companion.PADDING_HORIZONTAL_32
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosProprietarioFragment.Companion.PADDING_VERTICAL_0

class DadosContatoFragment : BaseFragment(), ShowLayoutListener {

    private lateinit var contacts: ArrayList<Contact>
    private var isEditBlocked = false

    private lateinit var _binding: McnFragmentDadosContatoBinding
    val binding: McnFragmentDadosContatoBinding get() = _binding

    companion object {
        const val CONTACTS = "contacts"
        const val EDIT_BLOCK = "edit_block"
        fun create(contacts: ArrayList<Contact>, isEditBlocked: Boolean) = DadosContatoFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(CONTACTS, contacts)
                putBoolean(EDIT_BLOCK, isEditBlocked)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = McnFragmentDadosContatoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { it ->
            it.getParcelableArrayList<Contact>(CONTACTS)?.let { itContacts ->
                contacts = itContacts
            }
            isEditBlocked = it.getBoolean(EDIT_BLOCK)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contatosAdapter = ContatosAdapter(requireContext(), contacts).also {
            it.setAction {
                showInformationDialog()
            }
        }

        binding.apply {
            rvContatos.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = contatosAdapter

                setPadding(PADDING_HORIZONTAL_32, PADDING_VERTICAL_0, PADDING_HORIZONTAL_32, PADDING_VERTICAL_0)

                if (onFlingListener == null) PagerSnapHelper().attachToRecyclerView(this)

                addItemDecoration(
                    MarginItemDecoration(
                        resources.getDimensionPixelSize(R.dimen.dimen_16dp),
                        DividerItemDecoration.HORIZONTAL
                    )
                )
            }
            if (contacts.isEmpty().not()) {
                contentContatosMsgDc.gone()
                rvContatos.visible()
            } else {
                contentContatosMsgDc.visible()
                rvContatos.gone()
            }
            constraintViewTitleCon.setOnClickListener {
                if (constraintViewDetailsContact.visibility == View.VISIBLE) {
                    gaSendInteraction(textTitle.text.toString())

                    imageViewContactArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
                    constraintViewDetailsContact.gone()
                } else {
                    imageViewContactArrow.animate()?.rotation(VALUE_ROTATION_90f)?.start()
                    constraintViewDetailsContact.visible()
                }
            }
        }
    }

    override fun closeContainer() {
        binding.apply {
            constraintViewDetailsContact.gone()
            imageViewContactArrow.animate()?.rotation(VALUE_ROTATION_0f)?.start()
        }
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

    private fun showInformationDialog() {
        val dialog = CieloDialog.create(
            title = getString(R.string.title_contact_update_dialog),
            message = getString(R.string.text_update_dialog))
            .setTitleTextAppearance(R.style.bold_montserrat_16)
            .setMessageTextAppearance(R.style.regular_montserrat_14_cloud_500)
            .setPrimaryButton(getString(R.string.entendi))
        activity?.supportFragmentManager?.let {
            dialog.show(it, null)
        }
    }
}