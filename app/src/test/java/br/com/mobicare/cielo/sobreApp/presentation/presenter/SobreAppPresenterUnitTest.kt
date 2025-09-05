package br.com.mobicare.cielo.sobreApp.presentation.presenter

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.sobreApp.presentation.ui.SobreAppContract
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Benhur on 14/11/2017.
 */
class SobreAppPresenterUnitTest {

    private lateinit var mView: SobreAppContract.View
    private lateinit var presenter: SobreAppPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mView = Mockito.mock(SobreAppContract.View::class.java)
        presenter = SobreAppPresenter(mView)
    }
    @Test
    fun retrieveAppVersionTest() {
        presenter.retrieveAppVersion()
        Mockito.verify(mView).fillAppVersion(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

}