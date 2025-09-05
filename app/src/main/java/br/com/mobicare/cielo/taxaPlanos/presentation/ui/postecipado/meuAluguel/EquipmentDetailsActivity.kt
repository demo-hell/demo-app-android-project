package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Postecipate.ARG_PARAM_EQUIPMENT_DETAILS_TERMINAL
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.ActivityPostecipadoEquipmentDetailsBinding
import kotlinx.android.synthetic.main.activity_add_ec.*

class EquipmentDetailsActivity : BaseActivity() {

    private var terminals: List<Terminal>? = null
    private var _binding: ActivityPostecipadoEquipmentDetailsBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityPostecipadoEquipmentDetailsBinding.inflate(layoutInflater);
        setContentView(binding?.root)
        setupToolbar(toolbarInclude as Toolbar, getString(R.string.equipment_negotiated))

        retrieveEquipmentList()
        setupView()
    }

    private fun retrieveEquipmentList() {
        intent?.extras?.let { bundle ->
            terminals = bundle.getParcelableArrayList(ARG_PARAM_EQUIPMENT_DETAILS_TERMINAL)
        }
    }

    private fun setupView() {
        binding?.rvEquipmentDetails?.apply {
            terminals?.let { itTerminals ->
                layoutManager = LinearLayoutManager(this@EquipmentDetailsActivity, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                adapter = PostecipadoEquipmentsAdapter(itTerminals, this@EquipmentDetailsActivity)
            }
        }
    }
}