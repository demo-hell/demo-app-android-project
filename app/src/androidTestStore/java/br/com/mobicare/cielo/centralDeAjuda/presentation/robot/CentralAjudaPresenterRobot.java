//package br.com.mobicare.cielo.centralDeAjuda.presentation.robot;
//
//import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj;
//import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.CentralAjudaPresenter;
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage;
//import br.com.mobicare.cielo.injection.Injection;
//
///**
// * Created by benhur.souza on 27/04/2017.
// */
//
//public class CentralAjudaPresenterRobot {
//
//    private CentralAjudaViewRobot robot;
//    private CentralAjudaPresenter presenter;
//
//    public CentralAjudaPresenterRobot( CentralAjudaViewRobot robot) {
//        this.robot = robot;
//
//        presenter = new CentralAjudaPresenter(robot.getView(), Injection.INSTANCE.INSTANCE.provideCentralDeAjudaRepository(robot.getActivity()), robot.getContext());
//    }
//
//    public CentralAjudaPresenterRobot callAPI() {
//        presenter.callAPI();
//        return this;
//    }
//
//    public CentralAjudaPresenterRobot callPhone(String number){
//        presenter.callPhone(robot.getActivity(), number);
//        return this;
//    }
//
//    public CentralAjudaPresenterRobot startAPI(){
//        robot.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //Chama método onStart()
//                presenter.onStart();
//            }
//        });
//        return this;
//    }
//
//    public CentralAjudaPresenterRobot onSuccessAPI(final CentralAjudaObj obj){
//        robot.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                presenter.onAuthenticationSuccess(obj);
//                presenter.onFinish();
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaPresenterRobot onErrorAPI(final String error){
//        robot.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ErrorMessage errorMessage = new ErrorMessage();
//                errorMessage.setMessage(error);
//                presenter.onStart();
//                presenter.onError(errorMessage);
//            }
//        });
//
//        return this;
//    }
//
//    public CentralAjudaResult start(){
//        return new CentralAjudaResult();
//    }
//}
