package br.com.mobicare.cielo.splash.data.managers;

import android.content.Context;

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault;
import br.com.mobicare.cielo.commons.utils.ReaderMock;
import br.com.mobicare.cielo.splash.domain.entities.Configuration;

/**
 * Created by benhur.souza on 31/03/2017.
 */

public class SplashRepository {
    private Context mContext;

    public static SplashRepository getInstance(Context context){
        return new SplashRepository(context);
    }

    public SplashRepository(Context context){
        this.mContext = context;
    }

    public void getConfig(final APICallbackDefault<Configuration, String> callback) {
        callback.onStart();
        callback.onSuccess(ReaderMock.Companion.getConfiguration(mContext));
        callback.onFinish();
    }
}
