package br.com.mobicare.cielo.accessManager.foreignUsers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.accessManager.foreignUsers.adapter.ForeignUsersPendingAdapter
import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentAccessManagerForeignListBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerForeignListFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerForeignListContract.View {

    val args: AccessManagerForeignListFragmentArgs by navArgs()
    private var navigation: CieloNavigation? = null
    private var foreignAdapter: ForeignUsersPendingAdapter? = null
    private var binding: FragmentAccessManagerForeignListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAccessManagerForeignListBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupRecyclerView() {
        foreignAdapter = ForeignUsersPendingAdapter(args.usersList.toList(), this, this)

        binding?.rvForeingUsers?.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = foreignAdapter
        }
    }

    private fun setupClickListeners() {
        binding?.btBackArrow?.setOnClickListener {
           comeBack()
        }
    }

    override fun onBackButtonClicked(): Boolean {
        comeBack()
        return super.onBackButtonClicked()
    }

    override fun onForeignUserClicked(user: ForeignUsersItem) {
        user.id?.let {
            findNavController().navigate(
                AccessManagerForeignListFragmentDirections
                    .actionAccessManagerPendingForeignUsersFragmentToAccessManagerForeignUserDetailFragment(it)
            )
        }
    }

    private fun comeBack() {
        findNavController().navigateUp()
    }
}