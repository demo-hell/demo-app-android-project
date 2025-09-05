package br.com.mobicare.cielo.accessManager.expired.adapter

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.expired.AccessManagerExpiredInvitationContract
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.commons.constants.CPF_MASK
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.databinding.CardAccessManagerUserExpiredInvitationItemBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL

class UsersWithExpiredInvitationGroupHolder(
    val view: CardAccessManagerUserExpiredInvitationItemBinding,
    val fragment: Fragment
) :
    RecyclerView.ViewHolder(view.root) {

    fun bind(
        user: Item,
        usersSelected: List<Item>,
        listener: AccessManagerExpiredInvitationContract.View
    ) {
        view.apply {
            selectedUser(user in usersSelected)
            tvUserName.text = user.email

            if (user.cpf.isNullOrEmpty()){
                tvUserCpf.visibility = View.INVISIBLE
            }else{
                tvUserCpf.visibility = View.VISIBLE

                tvUserCpf.text = fragment.getString(
                    R.string.access_manager_assign_role_cpf_number_x,
                    fragment.addMaskCPForCNPJ(user.cpf, CPF_MASK)
                )
            }
            tvUserType.text = getTextFromRole(
                user.profile?.id,
                if (user.profile?.name.isNullOrEmpty())
                    fragment.getString(R.string.custom_rb)
                else
                    user.profile?.name
            )

            clCardContainer.setOnClickListener {
                val isChecked = user !in usersSelected
                selectedUser(isChecked)
                listener.onUserSelected(user)
            }
        }
    }

    private fun selectedUser(
        isChecked: Boolean
    ) {
        if (isChecked)
            selectedUserLayout()
        else
            unselectedUserLayout()

        view.checkBoxUser.isChecked = isChecked
    }

    private fun selectedUserLayout() {
        setupLayout(R.color.brand_400, R.color.brand_400, R.drawable.background_border_blue)
    }

    private fun unselectedUserLayout() {
        setupLayout(R.color.display_400, R.color.display_400, R.drawable.background_gray_c5ced7)
    }

    private fun setupLayout(
        @ColorRes colorTitle: Int,
        @ColorRes color: Int,
        @DrawableRes background: Int
    ) {
        view.apply {
            val context = fragment.requireContext()
            tvUserName.setTextColor(
                ContextCompat.getColor(context, colorTitle)
            )
            tvUserCpf.setTextColor(
                ContextCompat.getColor(context, color)
            )
            tvUserType.setTextColor(
                ContextCompat.getColor(context, color)
            )
            ivUser.imageTintList = ContextCompat.getColorStateList(context, colorTitle)

            clCardContainer.background = ContextCompat.getDrawable(
                context,
                background
            )
        }
    }

    private fun getTextFromRole(role: String?, profileName: String?): String? {
        return if (role.isNullOrEmpty()) {
            fragment.getString(R.string.access_manager_resend_invite_user_reader)
        } else {
            when (role) {
                ADMIN -> fragment.getString(R.string.access_manager_resend_invite_user_admin)
                ANALYST -> fragment.getString(R.string.access_manager_resend_invite_user_analyst)
                TECHNICAL -> fragment.getString(R.string.access_manager_resend_invite_user_technical)
                READER -> fragment.getString(R.string.access_manager_resend_invite_user_reader)
                else -> fragment.getString(R.string.access_manager_resend_invite_user_custom, profileName.capitalizeWords())
            }
        }
    }
}