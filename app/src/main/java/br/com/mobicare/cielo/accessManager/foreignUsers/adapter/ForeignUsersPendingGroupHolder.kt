package br.com.mobicare.cielo.accessManager.foreignUsers.adapter

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.foreignUsers.AccessManagerForeignListContract
import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.databinding.CardAccessManagerForeignersUserPendingItemBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL

class ForeignUsersPendingGroupHolder(
    val view: CardAccessManagerForeignersUserPendingItemBinding,
    val fragment: Fragment
) :
    RecyclerView.ViewHolder(view.root) {

    fun bind(
        user: ForeignUsersItem,
        listener: AccessManagerForeignListContract.View
    ) {
        view.apply {
            tvUserName.text = user.name.capitalizeWords()

            tvUserPermission.text = fragment.getString(
                R.string.access_manager_foreign_list_item_subtitle,
                getTextFromRole(user.profile?.id,
                    if (user.profile?.name.isNullOrEmpty()) {
                        fragment.getString(R.string.custom_rb)
                    }else
                        user.profile?.name
                )
            ).fromHtml()

            clCardContainer.setOnClickListener {
                listener.onForeignUserClicked(user)
            }
        }
    }

    private fun getTextFromRole(role: String?, profileName: String?): String? {
        return if (role.isNullOrEmpty()) {
            fragment.getString(R.string.reader_rb)
        } else {
            when (role) {
                ADMIN -> fragment.getString(R.string.admin_rb)
                ANALYST -> fragment.getString(R.string.analyst_rb)
                TECHNICAL -> fragment.getString(R.string.technical_rb)
                READER -> fragment.getString(R.string.reader_rb)
                else -> profileName.capitalizeWords()
            }
        }
    }
}