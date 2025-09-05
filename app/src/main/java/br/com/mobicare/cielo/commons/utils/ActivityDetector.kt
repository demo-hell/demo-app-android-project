package br.com.mobicare.cielo.commons.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import br.com.mobicare.cielo.firstAccessOnboarding.FirstInstallOnboardingActivity
import br.com.mobicare.cielo.onboarding.presentation.ui.activity.OnboardingActivity
import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity


class ActivityDetector private constructor() : Application.ActivityLifecycleCallbacks {

    companion object {
        val openActivities: LinkedHashSet<String> = LinkedHashSet()

        private var instance: ActivityDetector? = null

        fun getActivityDetector(): ActivityDetector {

            if (instance == null) {
                instance = ActivityDetector()
            }

            return instance as ActivityDetector

        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        activity.apply {
            if (this !is SplashActivity &&
                this !is OnboardingActivity &&
                this !is FirstInstallOnboardingActivity) {
                openActivities.add(this::class.java.simpleName)
            }
        }

        screenCurrentPath()
    }

    override fun onActivityDestroyed(activity: Activity) {

        activity.apply {
            openActivities.remove(this::class.java.simpleName)
        }

        screenCurrentPath()

    }

    fun screenCurrentPath(): String {

        return openActivities.map {
            it.replace("Activity", "")
                    .replace("HomeF", "")
                    .replace("Main", "Inicio")
                    .replace("MinhasVendasHome", "MinhasVendas")
                    .replace("DetalhesDaVenda", "MinhasVendasDetalhes")
                    .replace("ExtratoTimeline", "MinhasVendasDia")
                    .replace("ExtratoDetalhe", "MinhasVendasDetalhes")
                    .replace("AndEstabelecimento", "Estabelecimento")
                    .replace("CentralAjudaActvity", "")
        }.joinToString(separator = "/")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

}

