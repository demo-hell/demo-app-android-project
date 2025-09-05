package br.com.mobicare.cielo.suporteTecnico.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.suporteTecnico.domain.entities.Problem


class TechnicalSupportProblemsAdapter(private val technicalItems: TechnicalProblemItems,
                                      private val clickListener: OnClickListener) :
        androidx.recyclerview.widget.RecyclerView.Adapter<TechnicalSupportProblemsAdapter.TechnicalSupportProblemViewHolder>() {


    interface OnClickListener {
        fun onClick(problem: Problem)
    }

    override fun onBindViewHolder(holder: TechnicalSupportProblemsAdapter
                                          .TechnicalSupportProblemViewHolder,
                                  position: Int) {
        holder.bind(technicalItems.problems[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TechnicalSupportProblemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_technical_support, parent, false))

    override fun getItemCount(): Int = technicalItems.problems.size


    class TechnicalSupportProblemViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        fun bind(problem: Problem, clickListener: OnClickListener) {

            val textItemDesc = view.findViewById<TextView>(R.id.textTechnicalSupportItemDescription)

            val relativeLayout = view.findViewById<RelativeLayout>(R.id.relativeTechnicalSupportItemContent)

            Utils.addFontMuseoSans500(view.context, textItemDesc)
            textItemDesc.text = SpannableStringBuilder.valueOf(problem.name)


            relativeLayout.setOnClickListener {
                clickListener.onClick(problem)
            }
        }

    }

}