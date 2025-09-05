package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.ombudsman.personaldata

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata.OmbudsmanPersonalDataContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata.OmbudsmanPersonalDataPresenter
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val EC_MOCK = "1234"

class OmbudsmanPersonalDataPresenterTest {

    private val meJsonExample = "{\n" +
            "    \"id\":\"58b\",\n" +
            "    \"advertisingId\":\"40f\",\n" +
            "    \"username\":\"Conta Digital 1\",\n" +
            "    \"login\":\"60224548751\",\n" +
            "    \"email\":\"teste@teste.net\",\n" +
            "    \"alternateEmail\":\"teste@teste.net\",\n" +
            "    \"birthDate\":\"1993-08-05\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"602\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 9999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"201\",\n" +
            "        \"name\":\"MASSA DADOS AFIL. - 001-11440\",\n" +
            "        \"tradingName\":\"Teste\",\n" +
            "        \"cnpj\":{\n" +
            "            \"rootNumber\":\"092\",\n" +
            "            \"number\":\"09.205\"\n" +
            "        },\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"201\",\n" +
            "        \"name\":\"MASSA DADOS AFIL. - 001-11440\",\n" +
            "        \"tradingName\":\"Teste\",\n" +
            "        \"cnpj\":{\n" +
            "            \"rootNumber\":\"092\",\n" +
            "            \"number\":\"09.205\"\n" +
            "        },\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-01-04\",\n" +
            "    \"isMigrationRequired\":false\n" +
            "}"

    @Mock
    lateinit var view: OmbudsmanPersonalDataContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var menuPreference: MenuPreference

    private lateinit var presenter: OmbudsmanPersonalDataPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = spy(OmbudsmanPersonalDataPresenter(view, userPreferences, menuPreference))
    }

    @Test
    fun `check values and behavior when userInformation is not null`() {
        val me = Gson().fromJson(meJsonExample, MeResponse::class.java)
        doReturn(me).whenever(userPreferences).userInformation
        doReturn(EC_MOCK).whenever(menuPreference).getEC()

        presenter.onLoadPersonalData()

        val captor = argumentCaptor<OmbudsmanRequest>()
        verify(view).showLoading()
        verify(presenter).onCreateObject(any(), any(), any(), any())
        verify(view).hideLoading()
        verify(view).onShowPersonalData(captor.capture())

        assertEquals("Conta Digital 1", captor.firstValue.contactPerson)
        assertEquals("1199999999", captor.firstValue.phone)
        assertEquals("teste@teste.net", captor.firstValue.email)
        assertEquals("1234", captor.firstValue.merchant)
        assertEquals("S", captor.firstValue.previousContact)
        assertEquals(null, captor.firstValue.protocol)
        assertEquals(null, captor.firstValue.message)
        assertEquals(null, captor.firstValue.subject)
    }

    @Test
    fun `check values and behavior when userInformation is null`() {
        doReturn(null).whenever(userPreferences).userInformation

        presenter.onLoadPersonalData()

        val captor = argumentCaptor<OmbudsmanRequest>()
        verify(view).showLoading()
        verify(presenter, never()).onCreateObject(any(), any(), any(), any())
        verify(view).hideLoading()
        verify(view).onShowPersonalData(captor.capture())

        assertEquals(listOf(null), captor.allValues)
    }
}