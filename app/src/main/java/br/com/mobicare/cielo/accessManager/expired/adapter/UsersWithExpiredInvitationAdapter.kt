package br.com.mobicare.cielo.accessManager.expired.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.accessManager.expired.AccessManagerExpiredInvitationContract
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.databinding.CardAccessManagerUserExpiredInvitationItemBinding

class UsersWithExpiredInvitationAdapter(
    private val users: List<Item>,
    private val usersSelected: List<Item>,
    val listener: AccessManagerExpiredInvitationContract.View,
    val fragment: Fragment
) : RecyclerView.Adapter<UsersWithExpiredInvitationGroupHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersWithExpiredInvitationGroupHolder {
        val binding = CardAccessManagerUserExpiredInvitationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return UsersWithExpiredInvitationGroupHolder(binding, fragment)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(
        holder: UsersWithExpiredInvitationGroupHolder,
        position: Int
    ) {
        holder.bind(users[position], usersSelected, listener)
    }
}