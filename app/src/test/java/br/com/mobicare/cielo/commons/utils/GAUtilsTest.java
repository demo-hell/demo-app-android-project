//package br.com.mobicare.cielo.commons.utils;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.view.View;
//
//import com.google.android.gms.analytics.Tracker;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
////import br.com.mobicare.cielo.CieloApplication;
//import br.com.mobicare.cielo.R;
//import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * Created by benhur.souza on 02/05/2017.
// */
//
//@RunWith(MockitoJUnitRunner.class)
//public class GAUtilsTest {
//
//    @Mock
//    private CentralAjudaContract.View mView;
//    @Mock
//    private Context mMockContext;
//    @Mock
//    private View mMockView;
//
//    @Before
//    public void getTracker() {
//        gaLinkTest();
//        when(mMockContext.getResources()).thenReturn(mock(Resources.class));
////        when(mMockContext.getResources().getString(R.string.ga_interaction)).thenReturn("TEST");
//        when((mMockContext.getApplicationContext())).thenReturn(mock(CieloApplicationk.class));
//        when(((CieloApplicationk) mMockContext.getApplicationContext()).getDefaultTracker()).thenReturn(mock(Tracker.class));
//    }
//
//    @Test
//    public void setIDTest() throws Exception {
//        GAUtils.Companion.setIDGA(mMockContext, "");
//        GAUtils.Companion.setIDGA(mMockContext, "123456");
//    }
//
//    @Test
//    public void sendIDGATest() throws Exception{
//        GAUtils.Companion.sendIDGA(mMockContext, "");
//        GAUtils.Companion.sendIDGA(mMockContext, "123456");
//    }
//
//    @Test
//    public void sendDimensionTest() throws Exception{
//        GAUtils.Companion.sendDimension(mMockContext);
//    }
//
////    @Test
////    public void pushCampaignsTest() throws Exception{
////        GAUtils.pushCampaigns(mMockContext,"TEST");
////    }
//
//    @Test
//    public void pushOpenScreenEvent() throws Exception{
//        GAUtils.Companion.pushOpenScreenEvent(mMockContext,"TEST");
//
//        when(mMockContext.getResources().getString(R.string.ga_meu_cadastro)).thenReturn("TEST");
//        GAUtils.Companion.pushOpenScreenEvent(mMockContext, R.string.ga_meu_cadastro);
//    }
//
////    @Test
////    public void sendInteractionEvent() throws Exception{
////        GAUtils.Companion.sendInteractionEvent(mMockContext, "Action", "Label");
////
//////        when(mMockContext.getResources().getString(R.string.ga_meu_cadastro)).thenReturn("TEST");
////        GAUtils.Companion.sendInteractionEvent(mMockContext, R.string.ga_meu_cadastro, "Label");
////    }
////
////    @Test
////    public void sendInteractionEventTest() throws Exception{
////        when(mMockContext.getString(R.string.ga_interaction_button)).thenReturn("Button");
////        GAUtils.Companion.sendButtonInteractionEvent(mMockContext,"ScreenName","Label");
////
//////        when(mMockContext.getResources().getString(R.string.ga_meu_cadastro)).thenReturn("TEST");
////        GAUtils.Companion.sendButtonInteractionEvent(mMockContext, R.string.ga_meu_cadastro, "Label");
////
////    }
//
//    private void gaLinkTest() {
////        when(mMockView.getContext()).thenReturn(mMockContext);
////        when(mMockContext.getString(R.string.ga_ajuda)).thenReturn("Mock");
////        when(mMockContext.getString(R.string.ga_interaction_link)).thenReturn("Link");
////        when(mMockContext.getString(R.string.ga_interaction)).thenReturn("interaction");
//        when((mMockContext.getApplicationContext())).thenReturn(mock(CieloApplication.class));
////        when(((CieloApplication) mMockContext.getApplicationContext()).getDefaultTracker()).thenReturn(mock(Tracker.class));
//    }
//
//}
