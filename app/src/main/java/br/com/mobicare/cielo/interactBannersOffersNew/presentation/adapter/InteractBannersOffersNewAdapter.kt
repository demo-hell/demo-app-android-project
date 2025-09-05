package br.com.mobicare.cielo.interactBannersOffersNew.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action.EXIBICAO
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.databinding.ItemBannerListBinding
import br.com.mobicare.cielo.extensions.isVisibleOnScreen
import br.com.mobicare.cielo.interactBannersOffersNew.analytics.InteractBannersNewAnalytics
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes.LEADERBOARD
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes.RECTANGLE
import br.com.mobicare.cielo.interactBannersOffersNew.utils.enums.InteractBannerEnum
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

class InteractBannersOffersNewAdapter(
    val offers: MutableList<HiringOffers>,
    val bannerType: InteractBannerTypes,
    val recyclerView: RecyclerView,
    val bannerControl: BannerControl,
    val analytics: InteractBannersNewAnalytics,
    val action: ((HiringOffers, Int) -> Unit)
) : RecyclerView.Adapter<InteractBannersOffersNewAdapter.InteractBannersOffersViewHolder>() {

    private val handleTrackedBanners = hashMapOf<Int, String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): InteractBannersOffersViewHolder {
        val binding =
            ItemBannerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InteractBannersOffersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InteractBannersOffersViewHolder, position: Int) {
        holder.bind(offers[position], action, bannerType)
    }

    override fun getItemCount() = offers.size

    inner class InteractBannersOffersViewHolder(private val binding: ItemBannerListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            offer: HiringOffers,
            action: ((HiringOffers, Int) -> Unit),
            bannerType: InteractBannerTypes,
        ) {
            binding.apply {
                try {
                    offer.name?.let { offerName ->
                        val newOfferName = offerName.replace('-', '_')
                        InteractBannerEnum.valueOf(newOfferName).also { bannerInfo ->
                            ivBanner.apply {
                                setImageResource(
                                    when (bannerType) {
                                        LEADERBOARD -> bannerInfo.leaderboardResId
                                        RECTANGLE -> bannerInfo.rectangleResId
                                    }
                                )

                                scaleType = ImageView.ScaleType.FIT_XY
                                contentDescription =
                                    getAccessibilityText(context, offers.last() == offer)

                                binding.root.viewTreeObserver?.addOnScrollChangedListener {
                                    if (binding.ivBanner.isVisibleOnScreen()) {
                                        trackBannerVisibleOnScreen(newOfferName)
                                    }
                                }
                                setOnClickListener {
                                    action(offer, absoluteAdapterPosition + ONE)
                                }
                            }
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    removeItem()
                }
            }
        }

        private fun trackBannerVisibleOnScreen(bannerName: String) {
            if (!handleTrackedBanners.containsKey(absoluteAdapterPosition)) {
                analytics.logScreenActionsByControl(
                    action = EXIBICAO,
                    bannerTypeName = bannerType.name,
                    bannerName = bannerName,
                    bannerControl = bannerControl,
                    absoluteAdapterPosition + ONE
                )
                handleTrackedBanners[absoluteAdapterPosition] = bannerName
            }
        }

        private fun getAccessibilityText(context: Context, isTheLastItem: Boolean): String {
            return context.getString(
                if (isTheLastItem) R.string.interact_banner_content_description_last_item
                else R.string.interact_banner_content_description_item
            )
        }

        private fun removeItem() {
            offers.removeAt(absoluteAdapterPosition)
            recyclerView.post { notifyItemRemoved(absoluteAdapterPosition) }
        }
    }
}


