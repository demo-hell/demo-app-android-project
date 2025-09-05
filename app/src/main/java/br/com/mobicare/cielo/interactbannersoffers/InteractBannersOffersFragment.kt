package br.com.mobicare.cielo.interactbannersoffers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.interactbannersoffers.analytics.InteractBannersAnalytics
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerMapper
import br.com.mobicare.cielo.interactbannersoffers.router.InteractBannerRouter
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.model.TermoAceiteObj
import br.com.mobicare.cielo.interactbannersoffers.view.InteractBannerType
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.synthetic.main.fragment_interact_banners_offers.*
import org.jetbrains.annotations.NotNull
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InteractBannersOffersFragment : BaseFragment(), InteractBannersView {

    private val presenter: InteractBannersPresenterImpl by inject {
        parametersOf(this)
    }
    private var listener: InteractBannersListener? = null
    private val analytics: InteractBannersAnalytics by inject()
    private lateinit var bannerType: InteractBannerType
    private var termoAceiteObj: TermoAceiteObj? = null
    private var mIsLoadingFromHome = false

    companion object {
        private const val PRIORITY_BANNER = "PRIORITY_BANNER"
        private const val IS_LOADING_FROM_HOME = "IS_LOADING_FROM_HOME"
        private const val BANNER_TYPE = "BANNER_TYPE"

        @JvmStatic
        fun newInstance(
            @NotNull priority: Int,
            @NotNull bannerType: InteractBannerType,
            isLoadingFromHome: Boolean = false,
            listener: InteractBannersListener? = null
        ) =
            InteractBannersOffersFragment().apply {
                this.listener = listener

                arguments = Bundle().apply {
                    putInt(PRIORITY_BANNER, priority)
                    putBoolean(IS_LOADING_FROM_HOME, isLoadingFromHome)
                    putSerializable(BANNER_TYPE, bannerType)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interact_banners_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            this.bannerType = it.getSerializable(BANNER_TYPE) as InteractBannerType
            this.mIsLoadingFromHome = it.getBoolean(IS_LOADING_FROM_HOME)

            presenter.onCreate(
                it.getInt(PRIORITY_BANNER),
                mIsLoadingFromHome
            )
        }

        imageViewLeaderboardBanner.setOnClickListener {
            presenter.goTo()
        }
    }

    override fun onPause() {
        presenter.onDispose()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun showBannerByPriority(offer: HiringOffers?) {
        frameLayoutContent.visible()
        listener?.onSuccess()

        offer?.let {
            InteractBannerMapper.getBanner(it.name, bannerType, imageViewLeaderboardBanner)

            it.name?.let { itName ->
                it.id?.let { itId ->
                    termoAceiteObj = InteractBannerMapper.getTermoAceite(requireActivity(), itId, itName)
                }
            }
        }
    }

    override fun onError(error: ErrorMessage) {
        listener?.onError(error)
    }

    override fun goTo(hiringOffers: HiringOffers?, screenName: String) {
        hiringOffers?.let {offer ->
            logEvent(offer, screenName)
            InteractBannerRouter.goTo(
                activity,
                requireContext(),
                offer
            )
        }
    }

    override fun showLoading() {
        if (isAttached()) progressBar?.visible()
    }

    override fun hideLoading() {
        if (isAttached()) progressBar?.gone()
    }

    private fun logEvent(hiringOffers: HiringOffers, screenName: String, action: String = Label.CLIQUE) {
        analytics.logScreenActions(
                screenName = screenName,
                action = action,
                bannerSize = getBannerSize(),
                bannerName = hiringOffers.name ?: EMPTY,
                isFromHome = mIsLoadingFromHome
        )
    }

    private fun getBannerSize() =
        when (this.bannerType) {
            InteractBannerType.LEADERBOARD -> Label.LEADERBOARD
            else -> Label.RECTANGLE
        }
}

interface InteractBannersListener {
    fun onSuccess()
    fun onError(error: ErrorMessage) {}
}