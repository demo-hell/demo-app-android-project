package br.com.mobicare.cielo.accessManager.foreignUsers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.accessManager.foreignUsers.AccessManagerForeignListContract
import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.databinding.CardAccessManagerForeignersUserPendingItemBinding

class ForeignUsersPendingAdapter(
    private val users: List<ForeignUsersItem>,
    val listener: AccessManagerForeignListContract.View,
    val fragment: Fragment
) : RecyclerView.Adapter<ForeignUsersPendingGroupHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForeignUsersPendingGroupHolder {
        val binding = CardAccessManagerForeignersUserPendingItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ForeignUsersPendingGroupHolder(binding, fragment)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(
        holder: ForeignUsersPendingGroupHolder,
        position: Int
    ) {
        holder.bind(users[position], listener)
    }
}