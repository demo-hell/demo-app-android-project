//package br.com.mobicare.cielo.meuCadastro.presentation.robot;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.test.ActivityInstrumentationTestCase2;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import br.com.mobicare.cielo.R;
//import br.com.mobicare.cielo.commons.presentation.utils.FragmentUtilActivity;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroDomicilioBancario;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroSolucoesContratadas;
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract;
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MeuCadastroFragment;
//
///**
// * Created by benhur.souza on 27/04/2017.
// */
//
//public class MeuCadastroViewRobot extends ActivityInstrumentationTestCase2<FragmentUtilActivity> {
//
//    private MeuCadastroFragment fragment;
//
//    public MeuCadastroViewRobot() {
//        super(FragmentUtilActivity.class);
//    }
//
//    public Context getContext(){
//        return this.getActivity();
//    }
//
//    public MeuCadastroContract.View getView(){
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
//        fragment = new MeuCadastroFragment();
//
//        //adicionar fragment
//        getActivity().getSupportFragmentManager().beginTransaction().addInFrame(R.id.mainframe, fragment).commit();
//        getInstrumentation().waitForIdleSync();
//
//        testActivityNotNull();
//    }
//
//    @Test
//    public void testActivityNotNull() {
//        FragmentUtilActivity myActivity = getActivity();
//        assertNotNull(myActivity);
//    }
//
//    public MeuCadastroViewRobot showContent() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showContent();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot hideContent() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideContent();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot showLoading() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showProgress();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot hideLoading() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideProgress();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot showError(final String error) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.showError(error);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot loadDadosEstabelecimento(final MeuCadastroObj meuCadastroObj) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.loadDadosEstabelecimento(meuCadastroObj);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot loadEndederecos(final MeuCadastroEndereco meuCadastroEndereco) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.loadEndederecos(meuCadastroEndereco);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot selectedEnderecoFisico() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.selectedEnderecoFisico();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot selectedEnderecoContato() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.selectedEnderecoContato();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot hideButtonEnderecos() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                fragment.hideButtonEnderecos();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot loadSolucoesContratadas(final MeuCadastroSolucoesContratadas... solucoes) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.loadSolucoesContratadas(solucoes);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot hideSolucoesContratadas() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideSolucoesContratadas();
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot loadDomicilioBancario(final MeuCadastroDomicilioBancario... bancos) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.loadDomicilioBancario(bancos);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot loadUniqueDomicilioBancario(final MeuCadastroDomicilioBancario banco) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.loadUniqueDomicilioBancario(banco);
//            }
//        });
//        return this;
//    }
//
//    public MeuCadastroViewRobot hideBandeirasHabilitadas() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fragment.hideBandeirasHabilitadas();
//            }
//        });
//        return this;
//    }
//
//    /**
//     * Retorna uma instancia do MeuCadastroResult para
//     * fazer as verificações das ações na view
//     *
//     * @return
//     */
//    public MeuCadastroResult start() {
//        return new MeuCadastroResult();
//    }
//
//}
