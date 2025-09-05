package br.com.mobicare.cielo.accessManager.assignRole

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.constants.CPF_MASK
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAssignRoleBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class AccessManagerAssignRoleFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAssignRoleContract.View,
    DefaultViewListAdapter.OnItemClickListener<AccessManagerUser> {

    private lateinit var binding: FragmentAccessManagerAssignRoleBinding

    private val presenter: AccessManagerAssignRolePresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    val args: AccessManagerAssignRoleFragmentArgs by navArgs()

    private var selectedUsersIds: MutableList<String> = mutableListOf()
    private var usersAdapter: DefaultViewListAdapter<AccessManagerUser>? = null

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccessManagerAssignRoleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupText()
        setupListeners()
        setupRecyclerView()
    }

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    private fun setupText() {
        binding.apply {
            when (args.role) {
                ADMIN -> {
                    tvTitle.text = getString(R.string.access_manager_assign_role_admin_title)
                    tvSubtitle.text = getString(R.string.access_manager_assign_role_admin_subtitle)
                    btSelectAll.gone()
                }

                ANALYST -> {
                    tvTitle.text = getString(R.string.access_manager_assign_role_analyst_title)
                    tvSubtitle.text =
                        getString(R.string.access_manager_assign_role_analyst_subtitle)
                    btSelectAll.visible()
                }

                TECHNICAL -> {
                    tvTitle.text = getString(R.string.access_manager_assign_role_technical_title)
                    tvSubtitle.text =
                        getString(R.string.access_manager_assign_role_technical_subtitle)
                    btSelectAll.visible()
                }

                else -> {
                    tvTitle.text = getString(R.string.access_manager_assign_role_reader_title)
                    tvSubtitle.text =
                        getString(R.string.access_manager_assign_role_reader_subtitle)
                    btSelectAll.visible()
                }
            }
            updateSelectedCount()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                activity?.onBackPressed()
            }

            btSelectAll.setOnClickListener {
                val wasNotFull = selectedUsersIds.size != args.usersList.size
                selectedUsersIds.clear()
                if (wasNotFull) {
                    selectedUsersIds.addAll(args.usersList.mapNotNull { it.id })
                }

                updateSelectedCount()
                usersAdapter?.notifyDataSetChanged()
            }

            btNext.setOnClickListener {
                validationTokenWrapper.generateOtp(showAnimation = false,
                    onResult = { otpCode ->
                        presenter.assignRole(selectedUsersIds, args.role, otpCode)
                    }
                )
            }
        }
    }

    private fun updateSelectedCount() {
        binding.apply {
            tvUsersCount.text = getString(
                R.string.x_dash_y,
                selectedUsersIds.size,
                args.usersList.size
            )

            btNext.isEnabled = selectedUsersIds.isNotEmpty()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun roleAssigned(userCount: Int, role: String) {
        doWhenResumed {
            val roleName = when (role) {
                ADMIN -> getString(R.string.access_manager_admins)
                ANALYST -> getString(R.string.access_manager_analyst)
                TECHNICAL -> getString(R.string.access_manager_technical)
                else -> getString(R.string.access_manager_readers)
            }

            val titleId = when (role) {
                ANALYST, TECHNICAL -> R.plurals.access_manager_assign_role_assign_success_title_plurals_analyst
                else -> R.plurals.access_manager_assign_role_assign_success_title_plurals
            }

            val messageId = when (role) {
                ANALYST, TECHNICAL -> R.plurals.access_manager_assign_role_assign_success_message_plurals_analyst
                else -> R.plurals.access_manager_assign_role_assign_success_message_plurals
            }

            navigation?.showCustomBottomSheet(
                image = R.drawable.img_conta_criada,
                title = resources.getQuantityString(
                    titleId,
                    userCount,
                    userCount, roleName
                ),
                message = resources.getQuantityString(
                    messageId,
                    userCount,
                    userCount, roleName
                ),
                bt2Title = getString(R.string.access_manager_assign_role_assign_success_button),
                bt2Callback = {
                    findNavController().navigate(
                        AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                    )
                    false
                },
                closeCallback = {
                    findNavController().navigate(
                        AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                    )
                }
            ) ?: activity?.moveToHome() ?: baseLogout()
        }
    }

    override fun showLoading(@StringRes loadingMessage: Int?, vararg messageArgs: String) {
        doWhenResumed {
            navigation?.showLoading(true, loadingMessage, *messageArgs)
        }
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        doWhenResumed(
            action = {
                navigation?.showContent(true, successMessage, loadingSuccessCallback, *messageArgs)
                    ?: loadingSuccessCallback?.invoke()
            },
            errorCallback = { loadingSuccessCallback?.invoke() }
        )
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            doWhenResumed(
                action = {
                    navigation?.showCustomBottomSheet(
                        image = R.drawable.ic_generic_error_image,
                        title = getString(R.string.generic_error_title),
                        message = messageError(error, requireActivity()),
                        bt1Title = if (retryCallback != null)
                            getString(R.string.text_try_again_label)
                        else
                            null,
                        bt1Callback = {
                            retryCallback?.invoke()
                            false
                        },
                        bt2Title = getString(R.string.entendi),
                        bt2Callback = {
                            false
                        },
                    ) ?: baseLogout()
                },
                errorCallback = { baseLogout() }
            )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(item: AccessManagerUser) {
        if (item.id == null) return

        if (selectedUsersIds.remove(item.id).not()) {
            selectedUsersIds.add(item.id)
        }

        updateSelectedCount()
        usersAdapter?.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        usersAdapter = DefaultViewListAdapter(
            args.usersList.toList(),
            R.layout.card_access_manager_user_item
        ).apply {
            onItemClickListener = this@AccessManagerAssignRoleFragment

            setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<AccessManagerUser> {

                override fun onBind(
                    item: AccessManagerUser,
                    holder: DefaultViewHolderKotlin
                ) {
                    val tvUserName = holder.itemView
                        .findViewById<AppCompatTextView>(R.id.tvUserName)
                    tvUserName?.text = item.name.capitalizeWords()

                    val tvUserCpf = holder.itemView
                        .findViewById<AppCompatTextView>(R.id.tvUserCpf)

                    if (item.cpf.isNullOrEmpty()) {
                        tvUserCpf.visibility = View.INVISIBLE
                    } else {
                        tvUserCpf.visibility = View.VISIBLE

                        tvUserCpf?.text = getString(
                            R.string.access_manager_assign_role_cpf_number_x,
                            addMaskCPForCNPJ(item.cpf, CPF_MASK)
                        )
                    }

                    val dotStatusColor = holder.itemView
                        .findViewById<View>(R.id.dotStatusColor)
                    dotStatusColor?.gone()

                    val isChecked = item.id in selectedUsersIds
                    val checkBox = holder.itemView
                        .findViewById<AppCompatCheckBox>(R.id.checkBox)
                    checkBox?.isChecked = isChecked

                    val ivUser = holder.itemView
                        .findViewById<AppCompatImageView>(R.id.ivUser)

                    val clCardContainer = holder.itemView
                        .findViewById<ConstraintLayout>(R.id.clCardContainer)

                    context?.let { itContext ->
                        val color =
                            if (isChecked) R.color.brand_400 else R.color.display_400

                        tvUserName?.setTextColor(
                            ContextCompat.getColor(itContext, color)
                        )

                        ivUser?.imageTintList = ContextCompat.getColorStateList(itContext, color)

                        tvUserCpf?.setTextColor(
                            ContextCompat.getColor(
                                itContext,
                                if (isChecked) R.color.brand_400 else R.color.display_300
                            )
                        )

                        clCardContainer?.background = ContextCompat.getDrawable(
                            itContext,
                            if (isChecked)
                                R.drawable.background_border_blue
                            else
                                R.drawable.background_gray_c5ced7
                        )
                    }

                    holder.itemView.setOnClickListener {
                        onItemClickListener?.onItemClick(item)
                    }
                }
            })
        }
        binding.rvUsers.apply {
           layoutManager = LinearLayoutManager(context)
           adapter = usersAdapter
        }
    }
}