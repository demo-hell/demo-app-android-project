package br.com.mobicare.cielo

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import br.com.mobicare.cielo.accessManager.di.accessManagerModule
import br.com.mobicare.cielo.antifraud.di.antiFraudModuleList
import br.com.mobicare.cielo.arv.di.arvModulesList
import br.com.mobicare.cielo.biometricToken.di.biometricTokenModulesList
import br.com.mobicare.cielo.chargeback.di.chargebackModulesList
import br.com.mobicare.cielo.cieloFarol.di.cieloFarolModulesList
import br.com.mobicare.cielo.commons.analytics.DatadogService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.di.accessTokenModulesList
import br.com.mobicare.cielo.commons.di.configurationModulesList
import br.com.mobicare.cielo.commons.di.featureTogglePreferenceModulesList
import br.com.mobicare.cielo.commons.di.getUserObjModulesList
import br.com.mobicare.cielo.commons.di.menuModulesList
import br.com.mobicare.cielo.commons.di.safeApiCallerModulesList
import br.com.mobicare.cielo.commons.di.userPreferencesModulesList
import br.com.mobicare.cielo.commons.utils.ActivityDetector
import br.com.mobicare.cielo.commons.utils.token.di.tokenModulesList
import br.com.mobicare.cielo.component.impersonate.di.impersonatingModulesList
import br.com.mobicare.cielo.component.onboarding.di.baseOnboardingModuleList
import br.com.mobicare.cielo.component.requiredDataField.di.requiredDataFieldModulesList
import br.com.mobicare.cielo.contactCielo.di.contactCieloModulesList
import br.com.mobicare.cielo.eventTracking.di.eventTrackingModulesList
import br.com.mobicare.cielo.forgotMyPassword.di.forgotMyPasswordModulesList
import br.com.mobicare.cielo.home.presentation.postecipado.di.postecipadoModulesList
import br.com.mobicare.cielo.interactBannersOffersNew.di.interactBannersModulesList
import br.com.mobicare.cielo.login.firstAccess.di.firstAccessModulesList
import br.com.mobicare.cielo.mdr.di.mdrModulesList
import br.com.mobicare.cielo.meuCadastroNovo.di.myAccountModule
import br.com.mobicare.cielo.mySales.di.MySalesModuleList
import br.com.mobicare.cielo.newRecebaRapido.di.receiveAutomaticModulesList
import br.com.mobicare.cielo.biometricNotification.di.biometricNotificationList
import br.com.mobicare.cielo.cancelSale.di.cancelSaleModuleList
import br.com.mobicare.cielo.openFinance.di.holderModuleList
import br.com.mobicare.cielo.p2m.di.p2mModulesList
import br.com.mobicare.cielo.pixMVVM.di.pixModulesList
import br.com.mobicare.cielo.posVirtual.di.posVirtualModuleList
import br.com.mobicare.cielo.review.di.googlePlayReviewModulesList
import br.com.mobicare.cielo.selfieChallange.di.selfieChallengeModule
import br.com.mobicare.cielo.simulator.simulation.di.simulatorModulesList
import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity
import br.com.mobicare.cielo.superlink.di.superLinkModulesList
import br.com.mobicare.cielo.suporteTecnico.di.requestTicketSupportModulesList
import br.com.mobicare.cielo.technicalSupport.di.technicalSupportModulesList
import br.com.mobicare.cielo.transparentLogin.di.transparentLoginModulesList
import br.com.mobicare.cielo.turboRegistration.di.registrationUpdateModulesList
import br.com.mobicare.cielo.webView.di.webViewContainerDiModulesList
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.stetho.Stetho
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.salesforce.marketingcloud.MCLogListener
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions
import com.salesforce.marketingcloud.notifications.NotificationManager
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig
import com.salesforce.marketingcloud.sfmcsdk.components.logging.LogLevel
import com.salesforce.marketingcloud.sfmcsdk.components.logging.LogListener
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.log.EmptyLogger
import timber.log.Timber
import java.util.Random

class CieloApplication : Application() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        configureDebugLogs()
        configureFireBaseCloudMessage()
        configureCrashlytics()
        configureAppsFlyer()
        configureMarketingCloud()
        Hawk.init(this).build()
        UserPreferences.init(this)
        DatadogService(this)

        this.registerActivityLifecycleCallbacks(ActivityDetector.getActivityDetector())

        context = this

        startKoin(
            context = this,
            modules = mutableListOf<Module>().apply {
                add(AppModule.getLoginModule())
                add(AppModule.getFeatureToggleModule())
                add(appBankAccountTransfer)
                add(presenterModule)
                add(appSelfRegistration)
                add(appMigration)
                add(appChangeEc)
                add(appAutoAtendimento)
                add(appCoil)
                add(appPagamentoPorLink)
                add(appPgLinkDetail)
                add(appFaq)
                add(appFaqSubCategories)
                add(appFaqQuestions)
                add(appMeuEstabelecimento)
                add(appMeuUser)
                add(appFaqContacts)
                add(appEditarDadosProprietario)
                add(appContaDigital)
                add(appTaxaPlanos)
                add(appEditarDadosContato)
                add(appMinhaConta)
                add(appAddAccount)
                add(appDeepLink)
                add(appMinhasVendas)
                add(appNewLogin)
                add(allowMe)
                add(appCancel)
                add(appLinkOrders)
                add(appMeusRecebimentos)
                add(appMfa)
                add(appMachinesTracking)
                add(appPedidosModule)
                add(appRecebaRapido)
                add(appLgpdModule)
                add(appPixModule)
                add(appDirfModule)
                add(appBalcaoRecebiveisExtratoModule)
                add(appAddEc)
                add(appWarningModule)
                add(appSecurityMenu)
                add(appHelpCenterModule)
                add(appIDOnboarding)
                add(appRouter)
                add(appSolesp)
                add(appTapOnPhone)
                add(debitAccountGA4)
                addAll(MySalesModuleList)
                addAll(antiFraudModuleList)
                addAll(chargebackModulesList)
                addAll(superLinkModulesList)
                addAll(forgotMyPasswordModulesList)
                addAll(getUserObjModulesList)
                addAll(menuModulesList)
                addAll(interactBannersModulesList)
                addAll(featureTogglePreferenceModulesList)
                addAll(configurationModulesList)
                addAll(safeApiCallerModulesList)
                addAll(tokenModulesList)
                addAll(accessTokenModulesList)
                addAll(p2mModulesList)
                addAll(arvModulesList)
                addAll(cieloFarolModulesList)
                addAll(userPreferencesModulesList)
                addAll(postecipadoModulesList)
                addAll(biometricTokenModulesList)
                addAll(requiredDataFieldModulesList)
                addAll(impersonatingModulesList)
                addAll(receiveAutomaticModulesList)
                addAll(posVirtualModuleList)
                addAll(contactCieloModulesList)
                addAll(selfieChallengeModule)
                addAll(eventTrackingModulesList)
                addAll(myAccountModule)
                addAll(technicalSupportModulesList)
                addAll(baseOnboardingModuleList)
                addAll(holderModuleList)
                addAll(pixModulesList)
                addAll(googlePlayReviewModulesList)
                addAll(mdrModulesList)
                addAll(transparentLoginModulesList)
                addAll(registrationUpdateModulesList)
                addAll(webViewContainerDiModulesList)
                addAll(requestTicketSupportModulesList)
                addAll(simulatorModulesList)
                addAll(biometricNotificationList)
                addAll(firstAccessModulesList)
                addAll(accessManagerModule)
                addAll(cancelSaleModuleList)
            },
            logger = EmptyLogger()
        )

        UserPreferences.getInstance().isStepTwo(false)
        Stetho.initializeWithDefaults(this)
    }

    private fun configureDebugLogs() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            SFMCSdk.setLogging(LogLevel.DEBUG, LogListener.AndroidLogger())
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
        }
    }

    private fun configureCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    private fun configureAppsFlyer() {
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
            }

            override fun onConversionDataFail(error: String?) {
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
            }

            override fun onAttributionFailure(error: String?) {
            }
        }
        AppsFlyerLib.getInstance().init(BuildConfig.APPSFLYER_KEY, conversionDataListener, this)
        AppsFlyerLib.getInstance().start(this)
        if (BuildConfig.DEBUG) {
            AppsFlyerLib.getInstance().setDebugLog(true)
        }
    }

    private fun configureMarketingCloud() {
        SFMCSdk.configure(applicationContext, SFMCSdkModuleConfig.build {
            pushModuleConfig = MarketingCloudConfig.builder().apply {
                setApplicationId(BuildConfig.MOBILE_PUSH_APP_ID)
                setAccessToken(BuildConfig.MOBILE_PUSH_ACCESS_TOKEN)
                setSenderId(BuildConfig.MOBILE_PUSH_SENDER_ID)
                setMarketingCloudServerUrl(BuildConfig.MOBILE_PUSH_CLOUD_SERVER_URL)
                setMid(BuildConfig.MOBILE_PUSH_MID)
                setNotificationCustomizationOptions(
                    NotificationCustomizationOptions.create { context, notificationMessage ->
                        val builder = NotificationManager.getDefaultNotificationBuilder(
                            context,
                            notificationMessage,
                            NotificationManager.createDefaultNotificationChannel(context),
                            R.drawable.ic_cielo_notification
                        )
                        val intent = Intent(context, SplashActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            action = Intent.ACTION_VIEW
                        }
                        notificationMessage.url?.let {
                            intent.data = Uri.parse(it)
                        }

                        builder.setAutoCancel(true)
                        builder.setSmallIcon(R.drawable.ic_cielo_notification)
                        builder.setContentIntent(
                            NotificationManager.redirectIntentForAnalytics(
                                applicationContext,
                                PendingIntent.getActivity(
                                    context,
                                    Random().nextInt(),
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                ),
                                notificationMessage,
                                true
                            ))
                    }
                )
                setDelayRegistrationUntilContactKeyIsSet(true)
                setAnalyticsEnabled(true)
                setPiAnalyticsEnabled(true)
            }.build(applicationContext)
        })
    }

    private fun configureFireBaseCloudMessage() {
        try {
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (BuildConfig.DEBUG) {
                    if (it.isSuccessful) {
                        Timber.d("~!!CieloApplication:configureFireBaseCloudMessage:addOnCompleteListener:${it.result}")
                    } else {
                        Timber.d(it.exception)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        UserPreferences.getInstance().isStepTwo(false)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        lateinit var context: Context
            private set
    }
}