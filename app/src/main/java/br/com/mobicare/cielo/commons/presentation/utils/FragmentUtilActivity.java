package br.com.mobicare.cielo.commons.presentation.utils;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import br.com.mobicare.cielo.R;

/**
 * Created by benhur.souza on 24/04/2017.
 */

public class FragmentUtilActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_utils);


    }

}