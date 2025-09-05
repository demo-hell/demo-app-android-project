package br.com.mobicare.cielo.pix.ui.transfer.amount

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PixEnterTransferAmountPresenterTest {

    @Mock
    lateinit var view: PixEnterTransferAmountContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixEnterTransferAmountPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixEnterTransferAmountPresenter(
            view,
            userPreferences
        )
    }

   @Test
    fun `When onSaveShowBalanceValue is called and the value of isShowBalanceValue is true the return must be false`(){
       doReturn(true).whenever(userPreferences).isShowBalanceValue
       val captor = argumentCaptor<Boolean>()
       presenter.onSaveShowBalanceValue()
       verify(view).onBalanceView(captor.capture())

       assertEquals(false, captor.firstValue)
   }

    @Test
    fun `When onSaveShowBalanceValue is called and the value of isShowBalanceValue is false the return must be true`(){
       doReturn(false).whenever(userPreferences).isShowBalanceValue
       val captor = argumentCaptor<Boolean>()
       presenter.onSaveShowBalanceValue()
        verify(view).onBalanceView(captor.capture())

       assertEquals(true, captor.firstValue)
   }

    @Test
    fun `When isShowBalanceValue is called and the saved value is false, the return must be false`(){
       doReturn(false).whenever(userPreferences).isShowBalanceValue
       assertEquals(false, presenter.isShowBalanceValue())
   }

    @Test
    fun `When isShowBalanceValue is called and the saved value is true, the return must be true`(){
       doReturn(true).whenever(userPreferences).isShowBalanceValue
       assertEquals(true,presenter.isShowBalanceValue())
   }

}