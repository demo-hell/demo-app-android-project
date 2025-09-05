package br.com.mobicare.cielo.suporteTecnico.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.suporteTecnico.domain.entities.ProblemSolution


class TechnicalSupportSolutionsAdapter(private val technicalSolutionItems: TechnicalSolutionItems,
                                       private val clickListener: OnClickListener) : androidx.recyclerview.widget.RecyclerView
          .Adapter<TechnicalSupportSolutionsAdapter.TechnicalSupportSolutionsViewHolder>() {

    interface OnClickListener {
        fun onClick(solution: ProblemSolution)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TechnicalSupportSolutionsViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_technical_support, parent, false))

    override fun onBindViewHolder(holder: TechnicalSupportSolutionsViewHolder, position: Int) {
        holder.bind(technicalSolutionItems.solutions[position], clickListener)
    }

    override fun getItemCount(): Int = technicalSolutionItems.solutions.size

    class TechnicalSupportSolutionsViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        fun bind(solution: ProblemSolution, clickListener: OnClickListener) {

            val textItemDesc = view.findViewById<TextView>(R.id.textTechnicalSupportItemDescription)


            val relativeLayout = view.findViewById<RelativeLayout>(R.id.relativeTechnicalSupportItemContent)

            Utils.addFontMuseoSans500(view.context, textItemDesc)
            textItemDesc.text = SpannableStringBuilder.valueOf(solution.name)

            relativeLayout.setOnClickListener {
                clickListener.onClick(solution)
            }
        }

    }

}