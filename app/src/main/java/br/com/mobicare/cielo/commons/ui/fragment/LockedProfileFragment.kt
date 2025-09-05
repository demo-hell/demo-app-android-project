package br.com.mobicare.cielo.commons.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.URL_WEBSITE_CIELO
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentLockedProfileBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.CUSTOM
import br.com.mobicare.cielo.pix.constants.EMPTY

class LockedProfileFragment() : BaseFragment() {

    val binding: FragmentLockedProfileBinding by viewBinding()

    @StringRes
    var title: Int? = null

    var mainRole: String? = null

    companion object {
        fun newInstance(@StringRes title: Int, mainRole: String) = LockedProfileFragment().apply {
            this.title = title
            this.mainRole = mainRole
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mainRole == CUSTOM) {
            binding.includeLockedProfile.profileInfoTitle.fromHtml(R.string.txt_lock_for_custom_profile)
            binding.includeLockedProfile.lockedProfileImage.setImageDrawable(
                AppCompatResources.getDrawable(view.context, R.drawable.locked_profile_info_content)
            )
            binding.includeLockedProfile.btnAccessWebsite.gone()
        }

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun setupToolbar() {
        val titleAux = title?.let { getString(it) } ?: EMPTY
        configureToolbarActionListener?.changeTo(title = titleAux)
    }

    private fun setupListeners() {
        binding.includeLockedProfile.btnAccessWebsite.setOnClickListener {
            Utils.openBrowser(requireActivity(), URL_WEBSITE_CIELO)
        }
    }

}