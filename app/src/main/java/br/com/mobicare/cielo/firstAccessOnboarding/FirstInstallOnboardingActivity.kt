package br.com.mobicare.cielo.firstAccessOnboarding

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.ActivityFirstInstallOnboardingBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.newLogin.NewLoginActivity

class FirstInstallOnboardingActivity: BaseActivity() {

    private lateinit var binding: ActivityFirstInstallOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstInstallOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPagerView()
        listenerCallAction()
        UserPreferences.getInstance().saveFirstUse(false)
    }

    private fun setupPagerView() {
        val items = listOf(
            FirstInstallOnboardingItem(
                title = getString(R.string.first_install_onboarding_sell_title),
                subtitle = getString(R.string.first_install_onboarding_sell_subtitle),
                image = R.drawable.img_01_light
            ),
            FirstInstallOnboardingItem(
                title = getString(R.string.first_install_onboarding_follow_title),
                subtitle = getString(R.string.first_install_onboarding_follow_subtitle),
                image = R.drawable.img_51_light
            ),
            FirstInstallOnboardingItem(
                title = getString(R.string.first_install_onboarding_security_title),
                subtitle = getString(R.string.first_install_onboarding_security_subtitle),
                image = R.drawable.img_40_light
            ),
            FirstInstallOnboardingItem(
                title = getString(R.string.first_install_onboarding_ready_title),
                subtitle = getString(R.string.first_install_onboarding_ready_subtitle),
                image = R.drawable.img_47_light
            )
        )

        binding.apply {
            viewPagerOnboarding.adapter = FirstInstallOnboardingAdapter(items)
            indicatorOnboarding.setViewPager(viewPagerOnboarding)
        }
        setupListener(items)
    }

    private fun setupListener(listItems: List<FirstInstallOnboardingItem>) {
        binding.viewPagerOnboarding.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerVisibleBottom(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerVisibleBottom(visible: Boolean) {
        binding.apply {
            btnNextOnboarding.visible(visible.not())
            btnStartOnboarding.visible(visible)
        }
    }

    private fun listenerCallAction() {
        binding.apply {
            btnNextOnboarding.setOnClickListener {
                viewPagerOnboarding.apply {
                    currentItem += ONE
                }
            }
            btnStartOnboarding.setOnClickListener {
                startActivity(Intent(this@FirstInstallOnboardingActivity, NewLoginActivity::class.java))
                finish()
            }
        }
    }

}