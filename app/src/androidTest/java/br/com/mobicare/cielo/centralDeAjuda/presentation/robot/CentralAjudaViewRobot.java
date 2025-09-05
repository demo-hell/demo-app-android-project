//package br.com.mobicare.cielo.centralDeAjuda.presentation.robot;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.test.ActivityInstrumentationTestCase2;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import br.com.mobicare.cielo.R;
//import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj;
//import br.com.mobicare.cielo.centralDeAjuda.domains.entities.Manager;
//import br.com.mobicare.cielo.centralDeAjuda.domains.entities.PhoneSupport;
//import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract;
//import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaFragment;
//import br.com.mobicare.cielo.commons.presentation.utils.FragmentUtilActivity;
//
///**
// * Created by benhur.souza on 27/04/2017.
// */
//
//public class CentralAjudaViewRobot extends ActivityInstrumentationTestCase2<FragmentUtilActivity> {
//
//    public CentralAjudaViewRobot() {
//        super(FragmentUtilActivity.class);
//    }
//
//    private CentralAjudaFragment fragment;
//
//    public Context getContext(){
//        return this.getActivity();
//    }
//
//    public CentralAjudaContract.View getView(){
//        return fragment;
//    }
//
//    @Before
//    public void setup() throws Exception {
//        super.setUp();
//
//        // Injecting the Instrumentation instance is required
//        // for your test to run with AndroidJUnitRunner.
//        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
//        fragment = new CentralAjudaFragment();
//
//        //adicionar fragment
//        getActivity().getSupportFragmentManager().beginTransaction().addInFrame(R.id.mainframe, fragment).commit();
//        getInstrumentation().waitForIdleSync();
//
//        testActivityNotNull();
//    }
//
//
//    @Test
//    public void testActivityNotNull() {
//        FragmentUtilActivity myActivity = getActivity();
//        assertNotNull(myActivity);
//    }
//
//    public CentralAjudaViewRobot showLoading() {
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showProgress();
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaViewRobot hideLoading() {
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideProgress();
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaViewRobot showError(final String error){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showError(error);
//            }
//        });
//        return this;
//    }
//
//    public CentralAjudaViewRobot showContent(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showContent();
//            }
//        });
//        return this;
//    }
//
//
//    public CentralAjudaViewRobot hideContent(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideContent();
//            }
//        });
//        return this;
//    }
//
//
//    public CentralAjudaViewRobot showGestor(final Manager gestorComercial) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showContent();
//                fragment.showGestor(gestorComercial);
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaViewRobot hideGestor() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideGestor();
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaViewRobot loadHeader(final CentralAjudaObj obj){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showContent();
//                fragment.loadHeader(obj);
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaViewRobot loadPhoneSupport(final PhoneSupport obj){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showContent();
//                fragment.loadPhoneSupport(obj);
//            }
//        });
//
//        return this;
//    }
//
//
//    /**
//     * Retorna uma instancia do CentralAjudaResult para
//     * fazer as verificações das ações na view
//     *
//     * @return
//     */
//    public CentralAjudaResult start() {
//        return new CentralAjudaResult();
//    }
//
//}
