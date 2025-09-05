package br.com.mobicare.cielo.commons.menu.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.repository.remote.MenuRepositoryImpl
import br.com.mobicare.cielo.commons.domain.datasource.MenuLocalDataSource
import br.com.mobicare.cielo.commons.domain.datasource.MenuRemoteDataSource
import br.com.mobicare.cielo.commons.menu.utils.MenuFactory
import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MenuRepositoryTest {

    private val remoteDataSource = mockk<MenuRemoteDataSource>()
    private val localDataSource = mockk<MenuLocalDataSource>()
    private val repository = MenuRepositoryImpl(remoteDataSource, localDataSource)

    private val resultSuccessSave = CieloDataResult.Success(true)
    private val resultSuccessMenu = CieloDataResult.Success(MenuFactory.menuResponse)
    private val resultSuccessPosVirtualWhiteList = CieloDataResult.Success(
        PosVirtualWhiteListResponse(true)
    )
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should get menu remote data source only once with menu is remote and FT white list true`() = runBlocking {
        coEvery { remoteDataSource.getMenu() } returns resultSuccessMenu
        coEvery { remoteDataSource.getPosVirtualWhiteList() } returns resultSuccessPosVirtualWhiteList
        coEvery { localDataSource.saveMenuLocalStorage(any()) } returns resultSuccessSave
        coEvery { localDataSource.savePosVirtualWhiteList(any()) } returns resultSuccessSave

        repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = true)

        coVerify(exactly = ONE) { remoteDataSource.getMenu() }
        coVerify(exactly = ONE) { remoteDataSource.getPosVirtualWhiteList() }
        coVerify(exactly = ZERO) { localDataSource.getMenu() }
        coVerify(exactly = ONE) { localDataSource.saveMenuLocalStorage(any()) }
        coVerify(exactly = ONE) { localDataSource.savePosVirtualWhiteList(any()) }
    }

    @Test
    fun `it should get white list remote data source only once with menu is remote and FT white list false`() = runBlocking {
        coEvery { remoteDataSource.getMenu() } returns resultSuccessMenu
        coEvery { localDataSource.saveMenuLocalStorage(any()) } returns resultSuccessSave
        coEvery { localDataSource.savePosVirtualWhiteList(any()) } returns resultSuccessSave

        repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = false)

        coVerify(exactly = ONE) { remoteDataSource.getMenu() }
        coVerify(exactly = ZERO) { remoteDataSource.getPosVirtualWhiteList() }
        coVerify(exactly = ZERO) { localDataSource.getMenu() }
        coVerify(exactly = ONE) { localDataSource.saveMenuLocalStorage(any()) }
        coVerify(exactly = ONE) { localDataSource.savePosVirtualWhiteList(any()) }
    }

    @Test
    fun `it should get white list remote data source only once with menu is local and FT white list true`() = runBlocking {
        coEvery { localDataSource.getMenu() } returns resultSuccessMenu

        repository.getMenu(isLocal = true, ftTapOnPhoneWhiteList = true)

        coVerify(exactly = ZERO) { remoteDataSource.getMenu() }
        coVerify(exactly = ZERO) { remoteDataSource.getPosVirtualWhiteList() }
        coVerify(exactly = ONE) { localDataSource.getMenu() }
        coVerify(exactly = ZERO) { localDataSource.saveMenuLocalStorage(any()) }
        coVerify(exactly = ZERO) { localDataSource.savePosVirtualWhiteList(any()) }
    }

    @Test
    fun `it should get white list remote data source only once with menu is local and FT white list false`() = runBlocking {
        coEvery { localDataSource.getMenu() } returns resultSuccessMenu

        repository.getMenu(isLocal = true, ftTapOnPhoneWhiteList = false)

        coVerify(exactly = ZERO) { remoteDataSource.getMenu() }
        coVerify(exactly = ZERO) { remoteDataSource.getPosVirtualWhiteList() }
        coVerify(exactly = ONE) { localDataSource.getMenu() }
        coVerify(exactly = ZERO) { localDataSource.saveMenuLocalStorage(any()) }
        coVerify(exactly = ZERO) { localDataSource.savePosVirtualWhiteList(any()) }
    }

    @Test
    fun `it should return menu when get menu with menu is remote and FT white list true`() = runBlocking {
        coEvery { remoteDataSource.getMenu() } returns resultSuccessMenu
        coEvery { remoteDataSource.getPosVirtualWhiteList() } returns resultSuccessPosVirtualWhiteList
        coEvery { localDataSource.saveMenuLocalStorage(any()) } returns resultSuccessSave
        coEvery { localDataSource.savePosVirtualWhiteList(any()) } returns resultSuccessSave

        val result = repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = true)

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
    fun `it should return menu when get menu with menu is remote and FT white list false`() = runBlocking {
        coEvery { remoteDataSource.getMenu() } returns resultSuccessMenu
        coEvery { remoteDataSource.getPosVirtualWhiteList() } returns resultSuccessPosVirtualWhiteList
        coEvery { localDataSource.saveMenuLocalStorage(any()) } returns resultSuccessSave
        coEvery { localDataSource.savePosVirtualWhiteList(any()) } returns resultSuccessSave

        val result = repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = false)

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
    fun `it should return menu when get menu with menu is local and FT white list true`() = runBlocking {
        coEvery { localDataSource.getMenu() } returns resultSuccessMenu

        val result = repository.getMenu(isLocal = true, ftTapOnPhoneWhiteList = true)

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
    fun `it should return menu when get menu with menu is local and FT white list false`() = runBlocking {
        coEvery { localDataSource.getMenu() } returns resultSuccessMenu

        val result = repository.getMenu(isLocal = true, ftTapOnPhoneWhiteList = false)

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
        coEvery { remoteDataSource.getMenu() } returns resultError

        val result = repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = true)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return empty error when get menu`() = runBlocking {
        coEvery { remoteDataSource.getMenu() } returns resultEmpty

        val result = repository.getMenu(isLocal = false, ftTapOnPhoneWhiteList = true)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}