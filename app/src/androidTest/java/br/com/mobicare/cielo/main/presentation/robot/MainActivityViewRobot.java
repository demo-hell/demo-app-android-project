//package br.com.mobicare.cielo.main.presentation.robot;
//
//import android.support.test.InstrumentationRegistry;
//import android.test.ActivityInstrumentationTestCase2;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import br.com.mobicare.cielo.main.presentation.ui.activities.MainActivity;
//
///**
// * Created by benhur.souza on 09/05/2017.
// */
//public class MainActivityViewRobot extends ActivityInstrumentationTestCase2<MainActivity> {
//
//    private MainActivity mainActivity;
//
//    public MainActivityViewRobot() {
//        super(MainActivity.class);
//    }
//
//    @Before
//    public void setup() throws Exception {
//        // Injecting the Instrumentation instance is required
//        // for your test to run with AndroidJUnitRunner.
//        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
//        getInstrumentation().waitForIdleSync();
//
//        testActivityNotNull();
//    }
//
//
//    @Test
//    public void testActivityNotNull() {
//        mainActivity = getActivity();
//        assertNotNull(mainActivity);
//    }
//
//    public MainActivityViewRobot showMenuLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.showMenuLogado();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot showMenuDeslogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.showMenuDeslogado();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectInicio(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuDeslogado().selectInicio();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectCentralAjuda(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuDeslogado().selectAjuda();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectNotificacao(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuDeslogado().selectNotificacao();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectSobre(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuDeslogado().selectSobre();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectInicioLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectInicio();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectMinhasVendas(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectMinhasVendas();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectMeusRecebimentos(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectMeusRecebimentos();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectAntecipe(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectAntecipe();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectProdutos(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectProdutos();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectFidelidade(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectFidelidade();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectMeuCadastroLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectMeuCadastro();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectAjudaLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectedAjuda();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectNotificacaoLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectedNotificacao();
//            }
//        });
//
//        return this;
//    }
//
//    public MainActivityViewRobot selectsobreLogado(){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getMenuLogado().selectedSobre();
//            }
//        });
//
//        return this;
//    }
//
//
//    public MainActivityResult start() {
//        return new MainActivityResult();
//    }
//}