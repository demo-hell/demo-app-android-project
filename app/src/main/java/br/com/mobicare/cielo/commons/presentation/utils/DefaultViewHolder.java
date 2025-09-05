package br.com.mobicare.cielo.commons.presentation.utils;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by benhur.souza on 19/04/2017.
 */
public class DefaultViewHolder<H extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private final H binding;

    public DefaultViewHolder(H viewDataBinding) {
        super(viewDataBinding.getRoot());

        binding = viewDataBinding;
        binding.executePendingBindings();
    }

    public H getViewDataBinding() {
        return binding;
    }
}

