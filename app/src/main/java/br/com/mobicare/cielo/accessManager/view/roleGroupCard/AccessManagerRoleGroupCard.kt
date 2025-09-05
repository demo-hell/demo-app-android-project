package br.com.mobicare.cielo.accessManager.view.roleGroupCard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.CardAccessManagerRoleGroupBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class AccessManagerRoleGroupCard(
    context: Context, val attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var binding: CardAccessManagerRoleGroupBinding? = null

    var count: Int = 1
        set(value) {
            field = value
            showCount()
        }

    var type: AccessManagerRoleGroupCardTypeEnum = AccessManagerRoleGroupCardTypeEnum.NONE
        set(value) {
            field = value

            if (isInEditMode) {
                setupLayout()
            } else {
                doOnLayout {
                    setupLayout()
                }
            }
            requestLayout()
        }

    private fun showCount() {
        doOnLayout {
            binding?.tvDetailCounter?.text = if (type == AccessManagerRoleGroupCardTypeEnum.EXPIRED)
                resources.getQuantityString(
                    R.plurals.access_manager_role_expired_group_card_detail_count, count, count
                )
            else
                resources.getQuantityString(
                    R.plurals.access_manager_role_group_card_detail_count, count, count
                )
        }
    }

    private fun setupLayout() {
        binding?.apply {
            tvTitle.text = context.getString(type.title).parseAsHtml()
            tvSubtitle.text = context.getString(type.subtitle).parseAsHtml()
            tvSubtitle.invalidate()

            val titleColor = ContextCompat.getColor(context, type.titleColor)
            tvTitle.setTextColor(titleColor)

            if (type.hasDetail) {
                tvTitle.setCompoundDrawables(null, null, null, null)
                ivDetail.setImageResource(type.detailImage)
                if (type.showDetailName) {
                    tvDetailTitle.visible()
                    tvDetailTitle.text = context.getString(type.detailName).parseAsHtml()
                } else
                    tvDetailTitle.gone()

                showCount()
                tvDetailCounter.setTextColor(titleColor)
                clDetailContainer.visible()

                if (type == AccessManagerRoleGroupCardTypeEnum.FOREIGN && count == ZERO) {
                    tvDetailTitle.text =
                        resources.getString(R.string.access_manager_role_group_card_foreign_empty_list)
                    tvDetailCounter.gone()
                }

            } else {
                clDetailContainer.gone()

                tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, type.detailImage),
                    null, null, null
                )
            }

            containerUsersInformation.requestLayout()
        }
    }

    init {
        binding = CardAccessManagerRoleGroupBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.AccessManagerRoleGroupCard) {
            type = AccessManagerRoleGroupCardTypeEnum.fromId(
                getInt(R.styleable.AccessManagerRoleGroupCard_cardType, ZERO)
            )
        }
    }
}