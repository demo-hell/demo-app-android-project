package br.com.mobicare.cielo.accessManager.assignedUsers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser.Status.ATIVO
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser.Status.BLOQUEADO
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser.Status.EM_CRIACAO
import br.com.mobicare.cielo.commons.constants.CPF_MASK
import br.com.mobicare.cielo.databinding.CardAccessManagerUserItemBinding
import br.com.mobicare.cielo.extensions.addMaskCPForCNPJ
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.visible

class AccessManagerAssignedUserAdapter(
    private val usersList: MutableList<AccessManagerUser>,
    private val usersSelected: MutableList<AccessManagerUser>,
    val onUserSelected: (AccessManagerUser) -> Unit
) : RecyclerView.Adapter<AccessManagerAssignedUserAdapter.AccessManagerAssignedUserViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccessManagerAssignedUserViewHolder {
        val binding = CardAccessManagerUserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccessManagerAssignedUserViewHolder(binding)
    }

    override fun getItemCount() = usersList.size

    fun updateList(users: List<AccessManagerUser>) {
        usersList.clear()
        usersList.addAll(users)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AccessManagerAssignedUserViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    inner class AccessManagerAssignedUserViewHolder(
        private val binding: CardAccessManagerUserItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(accessManagerUser: AccessManagerUser) {
            binding.apply {

                tvUserName.text = accessManagerUser.name.capitalizeWords()

                val statusLabel = itemView.context.getString(
                    when (accessManagerUser.status) {
                        ATIVO.name -> R.string.access_manager_assigned_users_active
                        EM_CRIACAO.name -> R.string.access_manager_assigned_users_pending
                        BLOQUEADO.name -> R.string.access_manager_assigned_users_blocked
                        else -> R.string.access_manager_assigned_users_blocked
                    }
                )

                if (accessManagerUser.cpf.isNullOrEmpty()) {
                    tvUserCpf.text = statusLabel
                } else {
                    tvUserCpf.text = if (accessManagerUser.representative) {
                        itemView.context.getString(
                            R.string.access_manager_assigned_users_attorney_cpf_number_and_status_x,
                            addMaskCPForCNPJ(accessManagerUser.cpf, CPF_MASK),
                            statusLabel
                        )
                    } else {
                        itemView.context.getString(
                            R.string.access_manager_assigned_users_cpf_number_and_status_x,
                            addMaskCPForCNPJ(accessManagerUser.cpf, CPF_MASK),
                            statusLabel
                        )
                    }
                }

                dotStatusColor.visible()
                dotStatusColor.backgroundTintList = ContextCompat.getColorStateList(
                    itemView.context,
                    when (accessManagerUser.status) {
                        ATIVO.name -> R.color.success_400
                        EM_CRIACAO.name -> R.color.alert_400
                        BLOQUEADO.name -> R.color.danger_400
                        else -> R.color.danger_400
                    }
                )

                binding.checkBox.isChecked = accessManagerUser in usersSelected

                itemView.setOnClickListener {
                    binding.checkBox.isChecked = accessManagerUser !in usersSelected
                    onUserSelected(accessManagerUser)
                }
            }
        }
    }
}