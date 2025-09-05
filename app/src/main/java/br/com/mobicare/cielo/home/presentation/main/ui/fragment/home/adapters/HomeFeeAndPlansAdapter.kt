package br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.databinding.BrandsTaxHomeItemBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand

class HomeFeeAndPlansAdapter() : RecyclerView.Adapter<HomeFeeAndPlansViewHolder>() {

    private var mContext: Context? = null
    private var mBrands = arrayListOf<Brand>()
    private var mShouldShowLoading = false
    private var mIsAnError = false
    private var mCallToAction: (() -> Unit)? = null

    constructor(
        context: Context,
        brands: ArrayList<Brand> = arrayListOf(),
        shouldShowLoading: Boolean = false,
        isError: Boolean = false,
        action: (() -> Unit)? = null
    ) : this() {
        mContext = context
        mBrands = brands
        mShouldShowLoading = shouldShowLoading
        mIsAnError = isError
        mCallToAction = action
    }

    internal fun updateInfo(
        brands: ArrayList<Brand>,
        isError: Boolean,
        isFirstTimeOpening: Boolean,
        action: (() -> Unit)
    ) {
        mBrands.clear()
        mBrands.addAll(brands)
        mIsAnError = isError
        mShouldShowLoading = isFirstTimeOpening
        mCallToAction = action

        notifyDataSetChanged()
    }

    internal fun reload() {
        mShouldShowLoading = true

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeeAndPlansViewHolder {
        val binding =
            BrandsTaxHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeFeeAndPlansViewHolder(binding)
    }

    override fun getItemCount() = when {
        mShouldShowLoading -> THREE
        mIsAnError -> ONE
        else -> mBrands.size
    }

    override fun onBindViewHolder(holder: HomeFeeAndPlansViewHolder, position: Int) {
        holder.bind(
            if (mShouldShowLoading || mIsAnError) null else mBrands[position],
            mShouldShowLoading,
            mIsAnError,
            mCallToAction,
            mContext
        )
    }
}