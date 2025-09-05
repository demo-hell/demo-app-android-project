package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.takeScreenshot
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType

abstract class PixReceiptViewBuilder(
    layoutInflater: LayoutInflater,
) : PixReceiptBaseViewBuilder(layoutInflater) {
    abstract val headerInformation: HeaderInformation
    abstract val fields: List<Field>

    open val originalTransactionFields: List<Field>? = null
    open val destinationFields: List<Field>? = null
    open val originFields: List<Field>? = null
    open val recurrenceFields: List<Field>? = null

    override fun build(): View {
        binding.apply {
            tvPixType.text = headerInformation.pixType
            tvDate.apply {
                text = headerInformation.date
                visible(headerInformation.date != null)
            }
            tvAboutSettlement.apply {
                text = headerInformation.aboutSettlement
                visible(headerInformation.aboutSettlement != null)
            }
            btnShare.setOnClickListener(::onShareTap)
        }

        addFieldsToView(fields)

        destinationFields?.let {
            addView(buildDividerView())
            addView(buildTitleView(getString(R.string.pix_extract_detail_title_value_destination)))
            addFieldsToView(it)
        }

        originFields?.let {
            addView(buildDividerView())
            addView(buildTitleView(getString(R.string.pix_extract_detail_title_value_origin)))
            addFieldsToView(it)
        }

        originalTransactionFields?.let {
            addView(buildDividerView())
            addView(buildTitleView(getString(R.string.pix_extract_detail_title_original_transaction_data)))
            addFieldsToView(it)
        }

        recurrenceFields?.let {
            addView(buildDividerView())
            addView(buildTitleView(getString(R.string.pix_extract_detail_title_recurrence_data)))
            addFieldsToView(it)
        }

        return super.build()
    }

    protected fun getPixTypeTextOrNull(type: PixType?) =
        type?.run {
            PixExtractReceiptType.parsePixExtractReceiptType(name)?.let { getString(it.title) }
        }

    private fun addFieldsToView(fields: List<Field>) {
        fields.forEach { field ->
            if (field.value.isNullOrBlank()) return@forEach
            addView(
                buildItemView(
                    getString(field.label),
                    field.value,
                    field.caption,
                    field.onFieldTap,
                ),
            )
        }
    }

    private fun onShareTap(v: View) {
        binding.apply {
            btnShare.gone()
            val result = root.takeScreenshot()
            btnShare.visible()

            if (result != null) {
                val intent =
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = context.contentResolver.getType(result)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        putExtra(Intent.EXTRA_STREAM, result)
                    }
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.resources.getText(R.string.pix_extract_detail_share_title),
                    ),
                )
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.pix_extract_detail_share_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    data class HeaderInformation(
        val pixType: String?,
        val date: String?,
        val aboutSettlement: String? = null,
    )

    open class Field(
        @StringRes val label: Int,
        val value: String?,
        val caption: String? = null,
        val onFieldTap: (() -> Unit)? = null,
    )
}
