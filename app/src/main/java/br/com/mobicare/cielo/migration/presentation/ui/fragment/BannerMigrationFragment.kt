package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActionListener
import kotlinx.android.synthetic.main.layout_modal_migration.*


class BannerMigrationFragment: BaseFragment() {

    private lateinit var migrationActionListener: MigrationActionListener
    private var migrationDomain : MigrationDomain? = null

    companion object {
        fun create(actionListener: MigrationActionListener, migrationDomain: MigrationDomain) : BannerMigrationFragment {
            val fragment = BannerMigrationFragment().apply {
                migrationActionListener = actionListener
               this.migrationDomain = migrationDomain
            }

            return fragment
        }

        const val NUM_PAGES = 3
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.layout_modal_migration, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_banner_migration.setOnClickListener {
            if(Utils.isNetworkAvailable(requireActivity())){
                migrationActionListener.onNextStep(false)
            }else{
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }
        }

        btn_rm_close.setOnClickListener {
            migrationActionListener.bannerDimmiss()
        }

        val pagerAdapter = fragmentManager?.let { ScreenSlidePagerAdapter(it) }
        vp_banner_migration.adapter = pagerAdapter

        indicator_banner_migration.setViewPager(vp_banner_migration)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES


        @SuppressLint("NewApi")
        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> {
                    ItemFragmentVP(createItemBannerMigration(R.drawable.relogio_migration,
                            R.string.vp_banner_1, 1))
                }
                1 -> {
                    ItemFragmentVP(createItemBannerMigration(R.drawable.share_migration,
                            R.string.vp_banner_2, 2))
                }
                2 -> {
                    ItemFragmentVP(createItemBannerMigration(R.drawable.home_migration,
                            R.string.vp_banner_3, 3))
                } else -> ItemFragmentVP(null)
            }

        }

    }

    private fun createItemBannerMigration(@DrawableRes drawableId: Int,
                                          @StringRes itemLabel: Int, id: Int):
            ItemBannerMigration {

        return ItemBannerMigration(getString(itemLabel),
                ContextCompat.getDrawable(requireContext(), drawableId), id)
    }

}
