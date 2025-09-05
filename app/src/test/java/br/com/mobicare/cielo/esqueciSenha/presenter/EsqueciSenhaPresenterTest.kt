package br.com.mobicare.cielo.esqueciSenha.presenter

/*
const val USER_NAME_DIGITAL_MOCK = "42709015005"
const val CODE_MOCK = "237"
const val BRANCH_MOCK = "6471"
const val ACCOUNT_MOCK = "17813145"
const val ACCOUNT_TYPE_MOCK = "CC"
const val MERCHANT_ID_DIGITAL_MOCK = "2006080940"
const val MSG_SUCCESS_MOCK = "t******@u****.net"
private const val AKAMAI_SENSOR_DATA = " 3,a,tS5ta5h/0kmMCGWWtAsbrNRiRw9ePpLvCDI5WbitHh/0dIxBD0aRlEfaOaUsF5PsHuaAuPKj+4/5oPH1mWw+qAA24=,MoVHJkOG1sLmiJ88aG0y"


class EsqueciSenhaPresenterTest {

    private val login = Login(username = USER_NAME_DIGITAL_MOCK)
    private val bank = Bank(code = CODE_MOCK,
            branch = BRANCH_MOCK,
            account = ACCOUNT_MOCK,
            accountType = ACCOUNT_TYPE_MOCK)

    private val pid = Pid(merchantId = MERCHANT_ID_DIGITAL_MOCK,
            bank = bank)

    private val data = RecoveryPassword(login, pid)

    lateinit var presenter: EsqueciSenhaPresenter

    @Mock
    lateinit var view: EsqueciSenhaContract.View

    @Mock
    lateinit var repository: EsqueciSenhaNewRepository

    @Mock
    lateinit var bankRepository: BankDataRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = EsqueciSenhaPresenter(view, bankRepository, repository)
    }


    @Test
    fun `When calling API forgot password return is success`() {
        doAnswer {
            (it.arguments[1] as APICallbackDefault<String, String>).onSuccess(MSG_SUCCESS_MOCK)
        }.whenever(repository).recoveryPassword(
                data = eq(data),
                callback = any(),
                akamaiSensorData = eq(AKAMAI_SENSOR_DATA)
        )

        presenter.resetPassword(data)
        verify(view).hideProgress()
        verify(view).showSuccess(MSG_SUCCESS_MOCK)
    }

    @Test
    fun `When calling API forgot password return is error`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[1] as APICallbackDefault<String, String>).onError(errorMessage)
        }.whenever(repository).recoveryPassword(
                data = eq(data),
                callback = any(),
                akamaiSensorData = eq(AKAMAI_SENSOR_DATA)
        )

        presenter.resetPassword(data)
        verify(view).hideProgress()
        verify(view).showError(errorMessage)
    }
}*/
