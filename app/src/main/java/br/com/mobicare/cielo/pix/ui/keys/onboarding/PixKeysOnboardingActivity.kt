package br.com.mobicare.cielo.pix.ui.keys.onboarding

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.enums.Onboarding
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.adapter.OnboardingAdapter
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.ui.keys.myKeys.PixMyKeysNavigationFlowActivity
import kotlinx.android.synthetic.main.activity_pix_keys_onboarding.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixKeysOnboardingActivity : BaseLoggedActivity(), PixKeysOnboardingContract.View {

    private val presenter: PixKeysOnboardingPresenter by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pix_keys_onboarding)

        setupToolbar(toolbar as Toolbar, getString(R.string.text_title_screen_my_keys_pix))
        setupPagerView()
        listenerCallAction()
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.text_pix_what_is_pix_onboarding),
                subtitle = getString(R.string.text_pix_what_is_pix_answer_onboarding),
                image = R.drawable.ic_pix_chaves
            ),
            OnboardingItem(
                title = getString(R.string.text_pix_more_practical_onboarding),
                subtitle = getString(R.string.text_pix_more_practical_answer_onboarding),
                image = R.drawable.ic_pix_01
            )
        )

        view_pager_pix_keys_onboarding?.adapter = OnboardingAdapter(
            items = items,
            layout = R.layout.layout_pix_keys_onboarding_item,
            id = Onboarding.DEFAULT.id
        )
        indicator_pix_keys_onboarding?.setViewPager(view_pager_pix_keys_onboarding)

        setupListener(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        view_pager_pix_keys_onboarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerVisibleBottom(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerVisibleBottom(visible: Boolean) {
        if (visible) {
            btn_next_keys_pix_onboarding?.gone()
            btn_show_keys_pix_onboarding?.visible()
        } else {
            btn_next_keys_pix_onboarding?.visible()
            btn_show_keys_pix_onboarding?.gone()
        }
    }

    private fun listenerCallAction() {
        btn_next_keys_pix_onboarding?.setOnClickListener {
            view_pager_pix_keys_onboarding?.currentItem =
                view_pager_pix_keys_onboarding.currentItem + ONE
        }
        btn_show_keys_pix_onboarding?.setOnClickListener {
            presenter.onSaveOnboardingWasViewed()
        }
    }

    override fun onShowMyPixKeys() {
        startActivity<PixMyKeysNavigationFlowActivity>()
        finish()
    }
}