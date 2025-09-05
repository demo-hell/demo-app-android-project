package br.com.mobicare.cielo.meusRecebimentos.presentation.presenter

import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.MeusRecebimentosGraficoContract
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.MeusRecebimentosGraficoPresenterNew
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import com.github.mikephil.charting.data.Entry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*
import kotlin.collections.ArrayList

class MeusRecebimentosGraficoPresenterUnitTest{

    private lateinit var mView: MeusRecebimentosGraficoContract.View
    private lateinit var mRepository: MeusRecebimentosGraficoRepository
    private lateinit var presenter: MeusRecebimentosGraficoPresenterNew
    private lateinit var list: ArrayList<IncomingObj>

    @Before
    fun setUp() {
        mView = Mockito.mock(MeusRecebimentosGraficoContract.View::class.java)
        mRepository = Mockito.mock(MeusRecebimentosGraficoRepository::class.java)
        Mockito.`when`(mView.isAttached()).thenReturn(true)
        presenter = MeusRecebimentosGraficoPresenterNew(mView, mRepository)
        list = getIncomingListObj()
    }

    @Test
    fun getValuesTest(){
        Assert.assertEquals(presenter.getValues(),  ArrayList<Entry>())
        presenter.initGraph(getIncomingListObj())
        Assert.assertNotNull(presenter.getValues())
        Assert.assertFalse(presenter.getValues().isEmpty())
    }

//    @Test
//    fun getFirstYLabelTest(){
//        var time = presenter.getYLabel(0,12f)
//        Assert.assertNotNull(time)
//        Assert.assertEquals(time, "")
//    }

//    @Test
//    fun getLastYLabelTest(){
//        var time = presenter.getYLabel(11,12f)
//        Assert.assertNotNull(time)
//        Assert.assertEquals(time, "")
//    }

//    @Test
//    fun getYLabelTest(){
//        var time = presenter.getYLabel(2,12f)
//        Assert.assertNotNull(time)
//        Assert.assertEquals(time, "R$ 12")
//    }

//    @Test
//    fun getYSelectedLabelTest(){
//        presenter.selected = 8f
//        var time = presenter.getYLabel(8,12f)
//        Assert.assertNotNull(time)
//        Assert.assertTrue(time!!.contains("*"))
//    }

    @Test
    fun getXLabelTest(){
        presenter.createList(list)
        Assert.assertFalse(presenter.labelMap.size == 0)
        var label = presenter.getXLabel(2)
        Assert.assertNotNull(label)
        Assert.assertEquals(label, presenter.labelMap[2])
    }

//    @Test
//    fun getXSelectedLabelTest(){
//        presenter.createList(pageElements)
//        Assert.assertFalse(presenter.labelMap.size == 0)
//        var time = presenter.getXLabel(8)
//        Assert.assertNotNull(time)
//        Assert.assertTrue(time!!.contains("*"))
//    }

//    @Test
//    fun initGraphTest(){
//        presenter.initGraph(pageElements)
//
//        Assert.assertEquals(presenter.incomingObjList, pageElements)
//        Assert.assertEquals(presenter.selected, 8f)
//        Assert.assertEquals(presenter.currentPosition, 7)
//
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).loadGraph()
//        Mockito.verify(mView).loadHeaderData(presenter.incomingObjList)
//        Mockito.verify(mView).setHighlightItem(presenter.selected)
//    }

//    @Test
//    fun createListTest(){
//        presenter.createList(pageElements)
//
//        Assert.assertNotNull(presenter.labelMap)
//        Assert.assertNotNull(presenter.entriesList)
//        Assert.assertEquals(presenter.selected, 8f)
//        Assert.assertEquals(presenter.currentPosition, 7)
//
//        Mockito.verify(mView).setViewPagerCurrentItem(presenter.currentPosition)
//    }

//    @Test
//    fun updateDataTest(){
//        var position = 5f
//        presenter.incomingObjList = pageElements
//
//        presenter.updateData(position)
//        Assert.assertEquals(presenter.selected, position)
//        Assert.assertEquals(presenter.currentPosition, position.toInt() - 1)
//
//        Mockito.verify(mView).moveViewToX(position - 2.5f)
//        Mockito.verify(mView).setViewPagerCurrentItem(presenter.currentPosition)
//
//        Mockito.verify(mView).showNextButton()
//        Mockito.verify(mView).showPreviousButton()
//
//        Mockito.verify(mView).changeItem(position.toInt() - 1, presenter.incomingObjList[presenter.currentPosition])
//    }

//    @Test
//    fun updateLastDataTest(){
//        var position = pageElements.size.toFloat()
//        presenter.incomingObjList = pageElements
//
//        presenter.updateData(position)
//        Assert.assertEquals(presenter.selected, position)
//        Assert.assertEquals(presenter.currentPosition, position.toInt() - 1)
//
//        Mockito.verify(mView).moveViewToX(position - 2.5f)
//        Mockito.verify(mView).setViewPagerCurrentItem(presenter.currentPosition)
//
//        Mockito.verify(mView).hideNextButton()
//        Mockito.verify(mView).showPreviousButton()
//        Mockito.verify(mView).changeItem(position.toInt() - 1, presenter.incomingObjList[presenter.currentPosition])
//    }

//    @Test
//    fun updateFirstDataTest(){
//        var position = 1f
//        presenter.incomingObjList = pageElements
//
//        presenter.updateData(position)
//        Assert.assertEquals(presenter.selected, position)
//        Assert.assertEquals(presenter.currentPosition, position.toInt() - 1)
//
//        Mockito.verify(mView).moveViewToX(position - 2.5f)
//        Mockito.verify(mView).setViewPagerCurrentItem(presenter.currentPosition)
//
//        Mockito.verify(mView).showNextButton()
//        Mockito.verify(mView).hidePreviousButton()
//        Mockito.verify(mView).changeItem(position.toInt() - 1, presenter.incomingObjList[presenter.currentPosition])
//    }

    @Test
    fun onClickNextTest(){
        presenter.onClickNext()
        Mockito.verify(mView).setHighlightItem(presenter.currentPosition + 2f)
    }

    @Test
    fun onClickPreviousTest(){
        presenter.onClickPrevious()
        Mockito.verify(mView).setHighlightItem(presenter.currentPosition.toFloat())
    }

    @Test
    fun getEntriesListTest(){
        var entry = Entry()
        var entryList = ArrayList<Entry>()
        entryList.add(entry)
        entryList.add(entry)
        entryList.add(entry)

        presenter.entriesList = entryList
        Assert.assertNotNull(presenter.entriesList)
        Assert.assertEquals(presenter.entriesList, entryList)
    }

    @Test
    fun getLabelMapTest(){
        var labelList = HashMap<Int, String>()
        labelList.put(0, "1")
        labelList.put(1, "2")
        labelList.put(2, "3")

        presenter.labelMap = labelList
        Assert.assertNotNull(presenter.labelMap)
        Assert.assertEquals(presenter.labelMap, labelList)
    }

    @Test
    fun getMviewTest(){
        presenter.mView = mView
        Assert.assertEquals(presenter.mView, mView)
    }

    @Test
    fun getCurrentPositionTest(){
        presenter.currentPosition = 2
        Assert.assertEquals(presenter.currentPosition, 2)
    }

    /******************** MOCKS ********************/

    fun getIncomingListObj(): ArrayList<IncomingObj>{
        var obj = ArrayList<IncomingObj>()
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        obj.add(getIncomingObj(true)) // CurrentDay
        obj.add(getIncomingObj())
        obj.add(getIncomingObj())
        return obj
    }

    fun getIncomingObj(currentDay: Boolean = false):IncomingObj{
        var obj = IncomingObj()
        obj.mainDay = currentDay
        obj.dayOfWeek = "10"
        obj.dayOfMonth = "QUA"
        obj.totalDeposited = 100f.toDouble()
        return obj
    }


}