package br.com.mobicare.cielo.onboarding.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.ReaderJson
import br.com.mobicare.cielo.newLogin.NewLoginActivity
import br.com.mobicare.cielo.onboarding.domains.entities.OnboardingObj
import br.com.mobicare.cielo.onboarding.presentation.ui.OnBoardingAdapter
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.activity_onboarding.view.*


class OnboardingActivity : AppCompatActivity() {
    lateinit var onboarding: OnboardingObj

    companion object {
        private const val STEP = "/Step0"
        private const val EXIT_ONBOARDING = "Sair Do Onboarding"
        private const val NEXT = "Próximo"
        private const val FOLLOWING = "Seguinte"
        private const val PREVIOUS = "Anterior"
        private const val ACESSAR_O_APP = "Acessar o App"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        onboarding = ReaderJson.getOnBoarding(this)

        clickNextPage()
        configViewPager()
        jumpOnBoarding()
        clickAccessApp()
        UserPreferences.getInstance().saveFirstUse(false)
    }

    override fun onResume() {
        super.onResume()
        Analytics.trackScreenView(
            screenName = formatedOnboardinScreenName(),
            screenClass = this.javaClass
        )
    }

    var listSteps = mutableListOf<String>()
    var isSwipe = true

    private fun formatedOnboardinScreenName(): String {
        return "OnBoarding${listSteps.joinToString("")}"
    }

    private fun configViewPager() {
        view_pager_onboarding.adapter = OnBoardingAdapter(supportFragmentManager, onboarding)
        indicator.setViewPager(view_pager_onboarding)

        if (view_pager_onboarding.currentItem == 0) {
            listSteps.add("${STEP}${view_pager_onboarding.currentItem + 1}")
        }


        view_pager_onboarding.addOnPageChangeListener(object : OnPageChangeListener {
            var positionAtual = 0

            override fun onPageScrollStateChanged(position: Int) {

            }

            override fun onPageScrolled(position: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageSelected(position: Int) {

                if (positionAtual <= position) {
                    listSteps.add("${STEP}${view_pager_onboarding.currentItem + 1}")

                    if (isSwipe) {
                        Analytics.trackEvent(
                            category = listOf(Category.APP_CIELO, Category.SWIPE_SCREEN),
                            action = listOf(formatedOnboardinScreenName()),
                            label = listOf(String.format(Label.SWIPE_PAGE_X, FOLLOWING))
                        )

                        sendGaSwipe(FOLLOWING)

                    }
                } else if (positionAtual >= position) {
                    listSteps.removeAt(positionAtual)

                    if (isSwipe) {
                        Analytics.trackEvent(
                            category = listOf(Category.APP_CIELO, Category.SWIPE_SCREEN),
                            action = listOf(formatedOnboardinScreenName()),
                            label = listOf(String.format(Label.SWIPE_PAGE_X, PREVIOUS))
                        )

                        sendGaSwipe(PREVIOUS)
                    }
                }

                positionAtual = position
                isSwipe = true

                Analytics.trackScreenView(
                    screenName = formatedOnboardinScreenName(),
                    screenClass = this.javaClass
                )

                if (position == onboarding.pages?.size!! - 1) {
                    accessAppVisible(true)
                } else {
                    accessAppVisible(false)
                }
            }
        })
    }

    private fun clickNextPage() {
        layout_next.setOnClickListener {
            isSwipe = false

            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAP_LABEL),
                action = listOf(formatedOnboardinScreenName()),
                label = listOf(NEXT)
            )

            sendGaEventButton(NEXT)

            if (view_pager_onboarding.currentItem + 1 == onboarding.pages?.size!!) {
                accessAppVisible(true)
            } else {
                accessAppVisible(false)
                view_pager_onboarding.setCurrentItem(view_pager_onboarding.currentItem + 1)
            }
        }
    }

    private fun accessAppVisible(visible: Boolean) {
        if (visible) {
            relativeJumpNext.visibility = View.INVISIBLE
            relativeAccessApp.visibility = View.VISIBLE
        } else {
            relativeJumpNext.visibility = View.VISIBLE
            relativeAccessApp.visibility = View.INVISIBLE
        }
    }

    private fun clickAccessApp() {
        relativeAccessApp.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAP_LABEL),
                action = listOf(formatedOnboardinScreenName()),
                label = listOf(Label.ACESSAR_APP)
            )

            sendGaEventButton(ACESSAR_O_APP)

            changeActivity(NewLoginActivity::class.java)
        }
    }

    private fun jumpOnBoarding() {
        layout_jump.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAP_LABEL),
                action = listOf(formatedOnboardinScreenName()),
                label = listOf(layout_jump.btnJump.text.toString(), EXIT_ONBOARDING)
            )

            sendGaEventButton(EXIT_ONBOARDING)

            changeActivity(NewLoginActivity::class.java)
        }
    }

    fun changeActivity(activity: Class<*>) {
        startActivity(Intent(this, activity))
        this.finish()
    }


    //region Send Events GA

    private fun sendGaEventButton(eventLabel: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, formatedOnboardinScreenName()),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.SWIPE, eventLabel)
        )
    }

    private fun sendGaSwipe(eventLabel: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, formatedOnboardinScreenName()),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.BOTAO, eventLabel)
        )
    }
    //endregion
}
