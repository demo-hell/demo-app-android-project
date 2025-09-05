package br.com.mobicare.cielo.interactBannersOffersNew.presentation

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.FIFTY
import br.com.mobicare.cielo.commons.constants.FORTY_SIX
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.databinding.FragmentInteractBannersOffersNewBinding
import br.com.mobicare.cielo.interactBannersOffersNew.analytics.InteractBannersNewAnalytics
import br.com.mobicare.cielo.interactBannersOffersNew.presentation.adapter.InteractBannersOffersNewAdapter
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnError
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnFeatureDisabled
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnHideLoading
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnShowBannerByControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnShowLoading
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnSuccess
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.interactbannersoffers.router.InteractBannerRouter
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InteractBannersOffersNewFragment : BaseFragment() {
    private val viewModel: InteractBannersViewModel by viewModel()
    private var actionCallback: ((InteractBannersUiState) -> Unit)? = null
    private val analytics: InteractBannersNewAnalytics by inject()
    private var bannerType: InteractBannerTypes? = null
    private var bannerControl: BannerControl? = null
    private var mShouldGetOffersFromApi = true
    private var binding: FragmentInteractBannersOffersNewBinding? = null

    companion object {
        private const val SHOULD_GET_OFFERS_FROM_API = "SHOULD_GET_OFFERS_FROM_API"
        private const val BANNER_TYPE = "BANNER_TYPE"
        private const val BANNER_CONTROL = "BANNER_CONTROL"

        fun newInstance(
            bannerType: InteractBannerTypes,
            shouldGetOffersFromApi: Boolean = true,
            bannerControl: BannerControl? = BannerControl.LeaderboardHome,
            actionCallback: ((InteractBannersUiState) -> Unit)?
        ) = InteractBannersOffersNewFragment().apply {
            this.actionCallback = actionCallback

            arguments = Bundle().apply {
                putSerializable(BANNER_CONTROL, bannerControl)
                putBoolean(SHOULD_GET_OFFERS_FROM_API, shouldGetOffersFromApi)
                putSerializable(BANNER_TYPE, bannerType)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInteractBannersOffersNewBinding.inflate(
            inflater,
            container,
            false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getParameters()
        setupBannerLoadingSize()
        observe()
        getOffers()
    }

    private fun getOffers() {
        bannerControl?.let { itControl ->
            viewModel.getHiringOffers(mShouldGetOffersFromApi, itControl)
        }
    }

    private fun getParameters() {
        arguments?.let {
            bannerType = it.getSerializable(BANNER_TYPE) as InteractBannerTypes
            mShouldGetOffersFromApi = it.getBoolean(SHOULD_GET_OFFERS_FROM_API)
            bannerControl = it.getSerializable(BANNER_CONTROL) as BannerControl
        }
    }

    private fun setupBannerLoadingSize() {
        binding?.apply {
            val dpValue = bannerType?.height ?: ZERO
            val pxValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue.toFloat(),
                resources.displayMetrics
            ).toInt()

            val bannerParams =
                FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pxValue)
            bannerParams.setMargins(FORTY_SIX, ONE, FORTY_SIX, FIFTY)
            loadingShimmer.layoutParams = bannerParams
        }
    }

    private fun observe() {
        viewModel.interactBannersStateMutableLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is OnShowLoading -> showLoading()
                is OnHideLoading -> hideLoading()
                is OnFeatureDisabled -> finishFlow()
                is OnError -> showError(uiState.error)
                is OnShowBannerByControl -> showBannerByControl(uiState.offers, uiState.bannerControl)
            }
        }
    }

    private fun hideLoading() {
        binding?.loadingShimmer.gone()
    }

    private fun showLoading() {
        binding?.loadingShimmer.visible()
    }

    private fun finishFlow() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    private fun showError(error: NewErrorMessage? = null) {
        actionCallback?.invoke(OnError(error))
        finishFlow()
    }

    private fun showBannerByControl(offers: List<HiringOffers>, bannerControl: BannerControl) {
        bannerType?.let { bannerType ->
            binding?.rvBanners?.apply {
                LinearSnapHelper().attachToRecyclerView(this)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val interactBannersAdapter = InteractBannersOffersNewAdapter(
                    offers.toMutableList(),
                    bannerType,
                    this,
                    bannerControl,
                    analytics
                ) { selectedOffer, position ->
                    logEventControl(selectedOffer, bannerControl, position)
                    goToOffer(selectedOffer)
                }

                adapter = interactBannersAdapter

                viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        this@apply.let { recyclerView ->
                            recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            if (bannerControl.numberOfBanners > ONE && offers.size > ONE) {
                                if (recyclerView.height > ZERO && recyclerView.getChildAt(ZERO) != null && recyclerView.getChildAt(ZERO).height > ZERO) {
                                    recyclerView.addItemDecoration(CircleIndicatorItemDecoration(context))
                                }
                            }
                        }
                    }
                })
            }
        }

        actionCallback?.invoke(OnSuccess)
    }

    private fun goToOffer(hiringOffer: HiringOffers?) {
        hiringOffer?.let { offer ->
            InteractBannerRouter.goTo(
                requireActivity(),
                requireContext(),
                offer
            )
        }
    }

    private fun logEventControl(
        hiringOffers: HiringOffers,
        bannerControl: BannerControl,
        position: Int,
        action: String = Label.CLIQUE
    ) {
        analytics.logScreenActionsByControl(
            action = action,
            bannerTypeName = bannerType?.name ?: EMPTY,
            bannerName = hiringOffers.name ?: EMPTY,
            bannerControl = bannerControl,
            position = position
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}