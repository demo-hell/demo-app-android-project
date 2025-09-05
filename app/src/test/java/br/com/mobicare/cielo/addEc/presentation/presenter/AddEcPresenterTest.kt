package br.com.mobicare.cielo.addEc.presentation.presenter

import br.com.mobicare.cielo.adicaoEc.domain.api.AddEcRepository
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccount
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.adicaoEc.domain.model.ParamsEc
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcContract
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcPresenter
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO
import br.com.mobicare.cielo.esqueciSenha.domains.entities.CODE_BANCO_CAIXA_ECONOMICA
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val CODE_BANCO_DO_NORDESTE = "4"
private const val CODE_BANCO_INTER = "77"

private const val CURRENT_ACCOUNT = "Conta Corrente"
private const val SAVINGS_ACCOUNT = "Conta Poupança"
private const val SIMPLE_ACCOUNT = "Conta Simples"
private const val PUBLIC_ENTITY_ACCOUNT = "Entidades Públicas"
private const val ACCOUNT_TYPE_PJ = "Pessoa Jurídica"
private const val ACCOUNT_TYPE_PF = "Pessoa Física"

class AddEcPresenterTest {

    private val banksJsonExample =
        "[{\"code\":\"1\",\"name\":\"BANCO DO BRASIL S.A.\"},{\"code\":\"3\",\"name\":\"BANCO DA AMAZONIA S.A.\"},{\"code\":\"4\",\"name\":\"BANCO DO NORDESTE\"},{\"code\":\"21\",\"name\":\"BANESTES S.A.\"},{\"code\":\"25\",\"name\":\"BANCO ALFA S.A.\"}]"

    private val accountTypeList = listOf(
        BankMaskVO(CURRENT_ACCOUNT), BankMaskVO(SAVINGS_ACCOUNT),
        BankMaskVO(SIMPLE_ACCOUNT), BankMaskVO(PUBLIC_ENTITY_ACCOUNT)
    ).map { SelectItem(it.name, it.name) }

    private val params = ParamsEc(ACCOUNT_TYPE_PJ, ACCOUNT_TYPE_PF, CURRENT_ACCOUNT, SAVINGS_ACCOUNT, SIMPLE_ACCOUNT, PUBLIC_ENTITY_ACCOUNT)

    private lateinit var bankObj: BanksSet
    private val ioScheduler = Schedulers.trampoline()
    private val uiScheduler = Schedulers.trampoline()

    @Mock
    lateinit var view: AddEcContract.View

    @Mock
    lateinit var addEcRepository: AddEcRepository

    private lateinit var addEcPresenter: AddEcPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        addEcPresenter = AddEcPresenter(view, addEcRepository, uiScheduler, ioScheduler)
    }

    @Test
    fun `Success on the fetch account types list for banks EXCEPT CAIXA with a PJ account profile`() {
        addEcPresenter.fetchAccountTypes(CODE_BANCO_DO_NORDESTE, ACCOUNT_TYPE_PJ, params)

        val argumentCaptor = argumentCaptor<ArrayList<SelectItem<BankMaskVO>>>()

        val accountTypeListFiltered = accountTypeList.filter {
            it == SelectItem(SAVINGS_ACCOUNT, SAVINGS_ACCOUNT) || it == SelectItem(
                CURRENT_ACCOUNT,
                CURRENT_ACCOUNT
            )
        }

        verify(view).prepareBottomSheetAccountTypeList(argumentCaptor.capture())
        assertTrue(argumentCaptor.allValues.contains(accountTypeListFiltered as ArrayList<*>))
    }

    @Test
    fun `Success on the fetch account types list for banks EXCEPT CAIXA with a PF account profile`() {
        addEcPresenter.fetchAccountTypes(CODE_BANCO_INTER, ACCOUNT_TYPE_PF, params)

        val argumentCaptor = argumentCaptor<ArrayList<SelectItem<BankMaskVO>>>()
        val accountTypeListFiltered = accountTypeList.filter {
            it == SelectItem(SAVINGS_ACCOUNT, SAVINGS_ACCOUNT) || it == SelectItem(
                CURRENT_ACCOUNT,
                CURRENT_ACCOUNT
            )
        }

        verify(view).prepareBottomSheetAccountTypeList(argumentCaptor.capture())
        assertTrue(argumentCaptor.allValues.contains(accountTypeListFiltered as ArrayList<*>))
    }

    @Test
    fun `Success on the fetch account types list for CAIXA with a PF account profile`() {
        addEcPresenter.fetchAccountTypes(CODE_BANCO_CAIXA_ECONOMICA, ACCOUNT_TYPE_PF, params)

        val argumentCaptor = argumentCaptor<ArrayList<SelectItem<BankMaskVO>>>()
        val accountTypeListFiltered = accountTypeList.filter {
            it == SelectItem(SAVINGS_ACCOUNT, SAVINGS_ACCOUNT) || it == SelectItem(
                CURRENT_ACCOUNT,
                CURRENT_ACCOUNT
            ) || it == SelectItem(SIMPLE_ACCOUNT, SIMPLE_ACCOUNT)
        }

        verify(view).prepareBottomSheetAccountTypeList(argumentCaptor.capture())
        assertTrue(argumentCaptor.allValues.contains(accountTypeListFiltered as ArrayList<*>))
    }

    @Test
    fun `Success on the fetch account types list for CAIXA with a PJ account profile`() {
        addEcPresenter.fetchAccountTypes(CODE_BANCO_CAIXA_ECONOMICA, ACCOUNT_TYPE_PJ, params)

        val argumentCaptor = argumentCaptor<ArrayList<SelectItem<BankMaskVO>>>()
        val accountTypeListFiltered = accountTypeList.filter {
            it == SelectItem(CURRENT_ACCOUNT, CURRENT_ACCOUNT) || it == SelectItem(
                PUBLIC_ENTITY_ACCOUNT,
                PUBLIC_ENTITY_ACCOUNT
            )
        }

        verify(view).prepareBottomSheetAccountTypeList(argumentCaptor.capture())
        assertTrue(argumentCaptor.allValues.contains(accountTypeListFiltered as ArrayList<*>))
    }

    @Test
    fun `Success on get bank list`() {
        bankObj = Gson().fromJson(banksJsonExample, BanksSet::class.java)

        val returnSuccess = Observable.just(bankObj)
        val argumentCaptor = argumentCaptor<ArrayList<SelectItem<BankMaskVO>>>()

        doReturn(returnSuccess).`when`(addEcRepository).getAllBanks()

        addEcPresenter.getBankList()

        verify(view).prepareBottomSheetBankList(argumentCaptor.capture())
        assertTrue(argumentCaptor.allValues.isNotEmpty())
    }

    @Test
    fun `Error on get bank list`() {
        val retrofitException = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(retrofitException)

        doReturn(errorObservable).`when`(addEcRepository).getAllBanks()

        addEcPresenter.getBankList()

        verify(view, never()).prepareBottomSheetBankList(any())
    }

    //ProfileType = PJ or PF
    //AccountType = currentAccount, simpleAccount, savingsAccount or publicEntityAccount
    @Test
    fun `Should return 'CP' when account type is 'Conta Poupança' and the bank isn't 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "2006075050",
            BankAccount("3009876578", SAVINGS_ACCOUNT, "6850", CODE_BANCO_DO_NORDESTE)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PF, params)

        assertEquals("CP", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return 'CC' when account type is 'Conta Corrente' and the bank isn't 'CAIXA' `() {
        val ecObj = BankAccountObj(
            "3027791043",
            BankAccount("9989968565", CURRENT_ACCOUNT, "9718", CODE_BANCO_INTER)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PJ, params)

        assertEquals("CC", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return '001' when account type is 'Conta Corrente' and profile type is 'Pessoa Física' and the bank is 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "1095508797",
            BankAccount("2343345654", CURRENT_ACCOUNT, "1010", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PF, params)

        assertEquals("001", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return '002' when account type is 'Conta Simples' and profile type is 'Pessoa Física' and the bank is 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "2224832800",
            BankAccount("9998898912", SIMPLE_ACCOUNT, "4310", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PF, params)

        assertEquals("002", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return '003' when account type is 'Conta Corrente' and profile type is 'Pessoa Jurídica' and the bank is 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "43335670001",
            BankAccount("3334332789", CURRENT_ACCOUNT, "6824", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PJ, params)

        assertEquals("003", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return '013' when account type is 'Conta Poupança' and profile type is 'Pessoa Física' and the bank is 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "1009029878",
            BankAccount("1223434525", SAVINGS_ACCOUNT, "6812", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PF, params)

        assertEquals("013", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Should return '006' when account type is 'Entidades Públicas' and profile type is 'Pessoa Jurídica' and the bank is 'CAIXA'`() {
        val ecObj = BankAccountObj(
            "4436455412",
            BankAccount("1317677690", PUBLIC_ENTITY_ACCOUNT, "6850", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val bankAccountObjReturned = addEcPresenter.prepareObjectToSubmit(ecObj, ACCOUNT_TYPE_PJ, params)

        assertEquals("006", bankAccountObjReturned.bankAccount.accountType)
    }

    @Test
    fun `Success on add new EC and show bottom sheet success`() {
        val ecObj = BankAccountObj(
            "1009029878",
            BankAccount("1223434525", SAVINGS_ACCOUNT, "6812", CODE_BANCO_CAIXA_ECONOMICA)
        )
        val success = retrofit2.Response.success(200)
        val successObservable = Observable.just(success)

        doReturn(successObservable).whenever(addEcRepository).addNewEc(ecObj, "80514")

        addEcPresenter.addNewEc(ecObj, "80514")

        verify(view).showBottomSheetSuccess()
        verify(view, never()).showError()
    }

    @Test
    fun `Error on add new EC and show bottom sheet error`() {
        val ecObj = BankAccountObj(
            "1023443766",
            BankAccount("1223434525", SAVINGS_ACCOUNT, "6812", CODE_BANCO_CAIXA_ECONOMICA)
        )

        val retrofitException = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(retrofitException)
        doReturn(errorObservable).whenever(addEcRepository).addNewEc(ecObj, "506999")

        addEcPresenter.addNewEc(ecObj, "506999")

        verify(view).showError(any())
        verify(view, never()).showBottomSheetSuccess()
    }
}


