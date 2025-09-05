# Presentation Layer

Camada responsável pela parte visual. (Activity, fragment,...)


![presentation img](./readme/imgs/presentationLayer.png?raw=true) 


#### Essa camada é dividida em duas partes
- UI: Onde se encontram toda parte visual (Acitivty, Fragments,...)
- PRESENTER:  Onde se encontram os presenters. Responsáveis por controlar as UI`s

#####A comunicação entre a UI e o Presenter é feita através dos contracts.

![contract img](./readme/imgs/contract.png?raw=true) 

######- Contract
```kotlin
interface LoginContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showAlert(@StringRes titleId : Int = -1, @StringRes messageId : Int = -1, message: String = "")
        ...
    }

    interface Presenter {
        fun callAPI(ec: String, username: String, psw: String)
        fun ecIsValid(ec: String): Boolean
        fun callPrecisaAjuda()
        ...
    }
}
```

######- View
```kotlin
...
class LoginFragment : Fragment(), LoginContract.View {

    private var presenter: LoginPresenter? = null
    ...
    
}
```



######- Presenter
```java
class LoginPresenter(private var mView: LoginContract.View,private var repository: LoginRepository) : LoginContract.Presenter {
    ...
}
```

