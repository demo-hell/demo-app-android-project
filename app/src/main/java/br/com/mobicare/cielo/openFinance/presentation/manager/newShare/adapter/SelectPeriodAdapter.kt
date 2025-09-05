package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.viewbinding.ViewBinding
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.OpenFinancePeriodConsentActiveBinding
import br.com.mobicare.cielo.databinding.OpenFinancePeriodConsentInactiveBinding
import br.com.mobicare.cielo.databinding.OpenFinancePeriodConsentOpenBinding
import br.com.mobicare.cielo.openFinance.domain.model.DeadLine
import br.com.mobicare.cielo.openFinance.presentation.utils.CheckTypePeriod.checkTypePeriod

class SelectPeriodAdapter(
    context: Context,
    private val deadlineList: List<DeadLine>
) : ArrayAdapter<DeadLine>(context, ZERO, deadlineList) {

    var spinnerOpen: Boolean = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (spinnerOpen) return viewInactive(position, convertView, parent)
        else return viewActive(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return viewOpen(position, convertView, parent)
    }

    private fun viewInactive(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { OpenFinancePeriodConsentInactiveBinding.bind(it) }
            ?: OpenFinancePeriodConsentInactiveBinding.inflate(LayoutInflater.from(context), parent, false)
        return createView(binding, position)
    }

    private fun viewActive(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { OpenFinancePeriodConsentActiveBinding.bind(it) }
            ?: OpenFinancePeriodConsentActiveBinding.inflate(LayoutInflater.from(context), parent, false)
        return createView(binding, position)
    }

    private fun viewOpen(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { OpenFinancePeriodConsentOpenBinding.bind(it) }
            ?: OpenFinancePeriodConsentOpenBinding.inflate(LayoutInflater.from(context), parent, false)
        return createView(binding, position)
    }

    private fun createView(binding: ViewBinding, position: Int): View {
        val currentItem: DeadLine? = getItem(position)
        currentItem?.let {
            val total = it.total.toString()
            val type = it.type?.let { checkTypePeriod(context, it) }
            when (binding) {
                is OpenFinancePeriodConsentInactiveBinding -> binding.tvSpinnerValue.text = "$total $type"
                is OpenFinancePeriodConsentActiveBinding -> binding.tvSpinnerValue.text = "$total $type"
                is OpenFinancePeriodConsentOpenBinding -> binding.tvSpinnerValue.text = "$total $type"
            }
        }
        return binding.root
    }
}