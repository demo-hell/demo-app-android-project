package br.com.mobicare.cielo.commons.menu.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.repository.remote.MenuRepository
import br.com.mobicare.cielo.commons.domain.useCase.GetMenuUseCase
import br.com.mobicare.cielo.commons.menu.utils.MenuFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMenuUseCaseTest {

    private val repository = mockk<MenuRepository>()
    private val getMenuUseCase = GetMenuUseCase(repository)

    private val resultSuccessMenu = CieloDataResult.Success(MenuFactory.menuResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should return menu when get menu with FT white list true`() = runBlocking {
        coEvery { repository.getMenu(any(), any()) } returns resultSuccessMenu

        val result = getMenuUseCase(isLocal = false, ftTapOnPhoneWhiteList = true)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultToCieloDataResult = result as CieloDataResult.Success
        val actualData = resultToCieloDataResult.value
        val expectedData = resultSuccessMenu.value

        assertEquals(expectedData.createdAt, actualData.createdAt)

        val actualMenuOne = actualData.menu[ZERO]
        val expectedMenuOne = expectedData.menu[ZERO]

        assertEquals(expectedMenuOne.menuTarget, actualMenuOne.menuTarget)
        assertEquals(expectedMenuOne.code, actualMenuOne.code)
        assertEquals(expectedMenuOne.icon, actualMenuOne.icon)
        assertEquals(expectedMenuOne.name, actualMenuOne.name)
        assertEquals(expectedMenuOne.shortIcon, actualMenuOne.shortIcon)
        assertEquals(expectedMenuOne.show, actualMenuOne.show)
        assertEquals(expectedMenuOne.showIcons, actualMenuOne.showIcons)
        assertEquals(expectedMenuOne.showItems, actualMenuOne.showItems)

        val actualItemOne = actualMenuOne.items?.get(ZERO)
        val expectedItemOne = expectedMenuOne.items?.get(ZERO)

        assertEquals(expectedItemOne?.menuTarget, actualItemOne?.menuTarget)
        assertEquals(expectedItemOne?.code, actualItemOne?.code)
        assertEquals(expectedItemOne?.icon, actualItemOne?.icon)
        assertEquals(expectedItemOne?.name, actualItemOne?.name)
        assertEquals(expectedItemOne?.shortIcon, actualItemOne?.shortIcon)
        assertEquals(expectedItemOne?.show, actualItemOne?.show)
        assertEquals(expectedItemOne?.showIcons, actualItemOne?.showIcons)
        assertEquals(expectedItemOne?.showItems, actualItemOne?.showItems)

        assertEquals(expectedMenuOne.privileges[ZERO], actualMenuOne.privileges[ZERO])
    }

    @Test
    fun `it should return menu when get menu with FT white list false`() = runBlocking {
        coEvery { repository.getMenu(any(), any()) } returns resultSuccessMenu

        val result = getMenuUseCase(isLocal = false, ftTapOnPhoneWhiteList = false)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultToCieloDataResult = result as CieloDataResult.Success
        val actualData = resultToCieloDataResult.value
        val expectedData = resultSuccessMenu.value

        assertEquals(expectedData.createdAt, actualData.createdAt)

        val actualMenuOne = actualData.menu[ZERO]
        val expectedMenuOne = expectedData.menu[ZERO]

        assertEquals(expectedMenuOne.menuTarget, actualMenuOne.menuTarget)
        assertEquals(expectedMenuOne.code, actualMenuOne.code)
        assertEquals(expectedMenuOne.icon, actualMenuOne.icon)
        assertEquals(expectedMenuOne.name, actualMenuOne.name)
        assertEquals(expectedMenuOne.shortIcon, actualMenuOne.shortIcon)
        assertEquals(expectedMenuOne.show, actualMenuOne.show)
        assertEquals(expectedMenuOne.showIcons, actualMenuOne.showIcons)
        assertEquals(expectedMenuOne.showItems, actualMenuOne.showItems)

        val actualItemOne = actualMenuOne.items?.get(ZERO)
        val expectedItemOne = expectedMenuOne.items?.get(ZERO)

        assertEquals(expectedItemOne?.menuTarget, actualItemOne?.menuTarget)
        assertEquals(expectedItemOne?.code, actualItemOne?.code)
        assertEquals(expectedItemOne?.icon, actualItemOne?.icon)
        assertEquals(expectedItemOne?.name, actualItemOne?.name)
        assertEquals(expectedItemOne?.shortIcon, actualItemOne?.shortIcon)
        assertEquals(expectedItemOne?.show, actualItemOne?.show)
        assertEquals(expectedItemOne?.showIcons, actualItemOne?.showIcons)
        assertEquals(expectedItemOne?.showItems, actualItemOne?.showItems)

        assertEquals(expectedMenuOne.privileges[ZERO], actualMenuOne.privileges[ZERO])
    }

    @Test
    fun `it should return API error when get menu`() = runBlocking {
        coEvery { repository.getMenu(any(), any()) } returns resultError

        val result = getMenuUseCase(isLocal = false, ftTapOnPhoneWhiteList = false)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return empty error when get menu`() = runBlocking {
        coEvery { repository.getMenu(any(), any())  } returns resultEmpty

        val result = getMenuUseCase(isLocal = false, ftTapOnPhoneWhiteList = false)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}