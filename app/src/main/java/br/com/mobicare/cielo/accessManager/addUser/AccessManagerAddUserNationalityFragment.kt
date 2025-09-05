package br.com.mobicare.cielo.accessManager.addUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.addUser.model.Country
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserNationalityBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerAddUserNationalityFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAddUserNationalityContract.View {

    private var navigation: CieloNavigation? = null
    private var countryCode: String = EMPTY
    private var _binding: FragmentAccessManagerAddUserNationalityBinding? = null
    private val binding get() = _binding
    private val presenter: AccessManagerAddUserNationalityPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAccessManagerAddUserNationalityBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        showCountries()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
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
                activity?.onBackPressed()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(
                    AccessManagerAddUserNationalityFragmentDirections.
                    actionAccessManagerAddUserNationalityFragmentToAccessManagerAddUserTypeFragment(
                        true, countryCode
                    )
                )
            }
            btnEditValue.setOnClickListener {
                showCountries()
            }
        }
    }

    fun showCountries() {
        presenter.getCountries()
    }

    override fun showSuccess(result: MutableList<Country>) {
        doWhenResumed(
            action = {
                result.removeAt(BRASIL_POSITION)
                var selectedCountry: Country? = null

                CieloListBottomSheet
                    .create(
                        headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                            title = getString(R.string.access_manager_nationality_title_bottom_sheet)
                        ),
                        layoutItemRes = R.layout.item_access_manager_countries,
                        data = result,
                        onViewBound = { country, isSelected, itemView ->
                            itemView.findViewById<RadioButton>(R.id.rbCountry).apply {
                                isChecked = isSelected
                            }
                            itemView.findViewById<AppCompatTextView>(R.id.tvCountry).apply {
                                text = country.name
                            }
                        },
                        onItemClicked = { country, position, bottomSheet ->
                            selectedCountry = country
                            bottomSheet.updateSelectedPosition(position)
                            bottomSheet.removeSearchFocus()
                            bottomSheet.changeButtonStatus(true)
                        },
                        mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                            title = getString(R.string.confirmar),
                            startEnabled = false,
                            onTap = {
                                binding?.tvCountry?.text = selectedCountry?.name
                                countryCode = selectedCountry?.code.toString()
                                enableButton(true)
                                it.dismiss()
                            }
                        ),
                        searchConfigurator = CieloBottomSheet.SearchConfigurator(
                            isShowSearchBar = true,
                            isShowSearchIcon = true,
                            onSearch = { searchString, listBottomSheet ->
                                val bottomSheet = (listBottomSheet as CieloListBottomSheet<Country>)
                                bottomSheet.updateSelectedPosition(ONE_NEGATIVE)
                                selectedCountry = null
                                bottomSheet.changeButtonStatus(false)
                                if (searchString.isEmpty()){
                                    bottomSheet.hideSearchErrorMessage()
                                    bottomSheet.updateList(result)
                                } else {
                                    val filteredList = result.filter { it.name?.contains(searchString, true) == true } as MutableList<Country>
                                    bottomSheet.updateList(filteredList)
                                    if (filteredList.isEmpty()){
                                        bottomSheet.showSearchErrorMessage(getString(R.string.access_manager_nationality_filter_list_error, searchString))
                                    } else {
                                        bottomSheet.hideSearchErrorMessage()
                                    }
                                }
                            }
                        ),
                        disableExpandableMode = true
                    ).show(parentFragmentManager, null)
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showError() {
        doWhenResumed(
            action = {
                enableButton(false)

                val cieloDialog = CieloDialog.create(
                    title = getString(R.string.access_manager_nationality_title_error_dialog_title),
                    message = getString(R.string.access_manager_nationality_title_error_dialog_message)
                )
                    .setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                    .setMessageTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                cieloDialog.setImage(R.drawable.ic_8)
                    .setTitleColor(R.color.display_500)
                    .setSecondaryButton(getString(R.string.text_button_try_again))
                    .setOnSecondaryButtonClickListener {
                        cieloDialog.dismiss()
                    }.show(
                        childFragmentManager,
                        AccessManagerAddUserNationalityFragment::class.java.simpleName
                    )
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun enableButton(validate: Boolean) {
        binding?.btnNext?.apply {
            isClickable = validate
            isEnabled = validate
        }
    }

    companion object {
        const val BRASIL_POSITION = 30
    }
}