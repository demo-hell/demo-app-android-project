package br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.cielo.libflue.util.extensions.gone
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.CallsRequestItemBinding
import br.com.mobicare.cielo.databinding.CallsRequestItemShimmerBinding
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.utils.CallRequestItem

class CallsRequestAdapter(
    private val requests: List<CallRequestItem>,
    private val textSpan: String = ""
): RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CallsRequestItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        val bindingShimmer = CallsRequestItemShimmerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            ZERO -> CallsRequestViewHolder(binding)
            else -> CallsRequestShimmerViewHolder(bindingShimmer)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        requests.getOrNull(position)?.let { callRequestItem ->
            when(holder){
                is CallsRequestViewHolder -> {
                    holder.bind(callRequestItem as CallRequest, textSpan, holder.itemView.context)
                }
                is CallsRequestShimmerViewHolder -> {
                    holder.bind()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (requests.getOrNull(position)) {
            is CallRequest -> ZERO
            else -> ONE
        }
    }

    override fun getItemCount() = requests.size

    inner class CallsRequestViewHolder(private val binding: CallsRequestItemBinding): ViewHolder(binding.root){

        fun bind(event: CallRequest, textQuery: String, context: Context) {
            binding.apply {
                tvCallTitle.text = event.description
                tvCallCodeValue.text = event.referCode
                tvCallSolicitationDateValue.text = event.createdDate
                tvCallDeadlineValue.text = if (event.solutionDeadline!! > ZERO) event.solutionDeadline.let {
                    context.resources.getQuantityString(R.plurals.my_calls_deadline,
                        it, it
                    )
                } else context.getString(R.string.text_until_one_working_day)

                val highlightColor = ContextCompat.getColor(context, R.color.color_E9ED8A)

                if (textQuery.isNotEmpty()) {
                    if (event.description.contains(textQuery, ignoreCase = true)) {
                        val descriptionStartIndex = event.description.indexOf(textQuery, ignoreCase = true)
                        val descriptionEndIndex = descriptionStartIndex + textQuery.length
                        val descriptionSpannable = SpannableString(event.description).apply {
                            setSpan(BackgroundColorSpan(highlightColor), descriptionStartIndex, descriptionEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        tvCallTitle.text = descriptionSpannable
                    }

                    if (event.referCode.contains(textQuery, ignoreCase = true)) {
                        val codeStartIndex = event.referCode.indexOf(textQuery, ignoreCase = true)
                        val codeEndIndex = codeStartIndex + textQuery.length
                        val codeSpannable = SpannableString(event.referCode).apply {
                            setSpan(BackgroundColorSpan(highlightColor), codeStartIndex, codeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        tvCallCodeValue.text = codeSpannable
                    }
                }

                event.eventRequestStatus?.let { updateCallStatusUI(binding, context, it) }
            }
        }
    }

    fun updateCallStatusUI(binding: CallsRequestItemBinding, context: Context, callStatus: EventRequestStatus) {
        binding.chipCallStatusTag.apply {
            chipIcon = AppCompatResources.getDrawable(context, callStatus.statusIcon)
            chipIconTint = context.getColorStateList(callStatus.statusIconTint)
            setTextColor(context.getColorStateList(callStatus.statusIconTint))
            chipBackgroundColor = context.getColorStateList(callStatus.statusBackgroundColor)
            text = context.getString(callStatus.statusText)
        }

        when (callStatus) {
            EventRequestStatus.UNREALIZED -> {
                binding.tvCallDeadlineLabel.gone()
                binding.tvCallDeadlineValue.gone()
            }

            EventRequestStatus.ATTENDED -> {
                binding.tvCallDeadlineLabel.text = context.getString(R.string.solution_deadline)
            }

            else -> {
                binding.tvCallDeadlineLabel.text = context.getString(R.string.solution_deadline)
            }
        }
    }

    inner class CallsRequestShimmerViewHolder(private val binding: CallsRequestItemShimmerBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            binding.shimmerCallsRequestItem.itemEventRequestShimmer.startShimmer()
        }
    }
}