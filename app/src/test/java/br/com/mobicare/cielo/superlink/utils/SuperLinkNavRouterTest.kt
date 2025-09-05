package br.com.mobicare.cielo.superlink.utils

import br.com.mobicare.cielo.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SuperLinkNavRouterTest {

    private lateinit var navStartRouter: SuperLinkNavStartRouter

    private val flowStartPosVirtual = SuperLinkNavStartRouter.FlowStartArg.POS_VIRTUAL.name
    private val flowStartHome = SuperLinkNavStartRouter.FlowStartArg.HOME.name

    private val expectedPosVirtualRedId = R.id.tipoVendaPagamentoPorLinkFragment
    private val expectedDefaultResId = R.id.linkPaymentFragment

    @Before
    fun setUp() {
        navStartRouter = SuperLinkNavStartRouter()
    }

    @Test
    fun `it should set the POS Virtual flow start origin and configure the start destination correctly`() = navStartRouter.run {
        setFlowStartOrigin(flowStartPosVirtual)

        assertTrue(isFlowOriginFromPosVirtual)
        assertEquals(expectedPosVirtualRedId, startDestinationResId)
    }

    @Test
    fun `it should set the Home flow start origin and configure the start destination correctly`() = navStartRouter.run {
        setFlowStartOrigin(flowStartHome)

        assertFalse(isFlowOriginFromPosVirtual)
        assertEquals(expectedDefaultResId, startDestinationResId)
    }

    @Test
    fun `it should configure the default start destination correctly when flow start origin is not set`() = navStartRouter.run {
        assertFalse(isFlowOriginFromPosVirtual)
        assertEquals(expectedDefaultResId, startDestinationResId)
    }

}