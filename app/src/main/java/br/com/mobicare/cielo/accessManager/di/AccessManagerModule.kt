package br.com.mobicare.cielo.accessManager.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.accessManager.AccessManagerApi
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserCpfContract
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserCpfPresenter
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserEmailContract
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserEmailPresenter
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserEstablishmentContract
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserEstablishmentPresenter
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserNationalityContract
import br.com.mobicare.cielo.accessManager.addUser.AccessManagerAddUserNationalityPresenter
import br.com.mobicare.cielo.accessManager.assignRole.AccessManagerAssignRoleContract
import br.com.mobicare.cielo.accessManager.assignRole.AccessManagerAssignRolePresenter
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersPresenter
import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserDetailContract
import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserDetailPresenter
import br.com.mobicare.cielo.accessManager.customProfile.AccessManagerCustomProfileContract
import br.com.mobicare.cielo.accessManager.customProfile.AccessManagerCustomProfilePresenter
import br.com.mobicare.cielo.accessManager.data.datasource.remote.AccessManagerRemoteDataSource
import br.com.mobicare.cielo.accessManager.data.datasource.remote.NewAccessManagerApi
import br.com.mobicare.cielo.accessManager.data.repository.NewAccessManagerRepositoryImpl
import br.com.mobicare.cielo.accessManager.domain.repository.NewAccessManagerRepository
import br.com.mobicare.cielo.accessManager.domain.usecase.GetCustomActiveProfilesUseCase
import br.com.mobicare.cielo.accessManager.domain.usecase.PostAssignRoleUseCase
import br.com.mobicare.cielo.accessManager.expired.AccessManagerExpiredInvitationContract
import br.com.mobicare.cielo.accessManager.expired.AccessManagerExpiredInvitationPresenter
import br.com.mobicare.cielo.accessManager.foreignDetail.AccessManagerForeignDetailContract
import br.com.mobicare.cielo.accessManager.foreignDetail.AccessManagerForeignDetailPresenter
import br.com.mobicare.cielo.accessManager.home.AccessManagerHomeContract
import br.com.mobicare.cielo.accessManager.home.AccessManagerHomePresenter
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveContract
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceivePresenter
import br.com.mobicare.cielo.accessManager.presentation.batchProfileChange.AccessManagerBatchChangeProfileViewModel
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationRepository
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val presenterModule = module {
    factory(name = "accessManagerHomePresenter") { (view: AccessManagerHomeContract.View) ->
        AccessManagerHomePresenter(
            AccessManagerRepository(get()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )
    }
    factory(name = "accessManagerAddUserCpfPresenter") { (view: AccessManagerAddUserCpfContract.View) ->
        AccessManagerAddUserCpfPresenter(
            AccessManagerRepository(get()),
            view
        )
    }
    factory(name = "accessManagerAddUserEmailPresenter") { (view: AccessManagerAddUserEmailContract.View) ->
        AccessManagerAddUserEmailPresenter(
            AccessManagerRepository(get()),
            view
        )
    }
    factory(name = "accessManagerAddUserNationalityPresenter") { (view: AccessManagerAddUserNationalityContract.View) ->
        AccessManagerAddUserNationalityPresenter(
            AccessManagerRepository(get()),
            view
        )
    }
    factory(name = "accessManagerAddUserEstablishmentPresenter") { (view: AccessManagerAddUserEstablishmentContract.View) ->
        AccessManagerAddUserEstablishmentPresenter(
            AccessManagerRepository(get()),
            view,
            UserPreferences.getInstance()
        )
    }
    factory(name = "accessManagerAssignRolePresenter") { (view: AccessManagerAssignRoleContract.View) ->
        AccessManagerAssignRolePresenter(
            AccessManagerRepository(get()),
            view
        )
    }
    factory(name = "accessManagerAssignedUsersPresenter") { (view: AccessManagerAssignedUsersContract.View) ->
        AccessManagerAssignedUsersPresenter(
            AccessManagerRepository(get()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            UserPreferences.getInstance()
        )
    }
    factory(name = "assignedUserDetailPresenter") { (view: AssignedUserDetailContract.View) ->
        AssignedUserDetailPresenter(
            AccessManagerRepository(get()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "accessManagerExpiredPresenter") { (view: AccessManagerExpiredInvitationContract.View) ->
        AccessManagerExpiredInvitationPresenter(
            view,
            AccessManagerRepository(get()),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "accessManagerForeignDetailPresenter") { (view: AccessManagerForeignDetailContract.View) ->
        AccessManagerForeignDetailPresenter(
            view,
            AccessManagerRepository(get()),
            UserPreferences.getInstance()
        )
    }

    factory(name = "accessManagerInviteCreateUserPresenter") { (view: InviteReceiveContract.View) ->
        InviteReceivePresenter(
            AccessManagerRepository(get()),
            SelfRegistrationRepository(SelfRegistrationSource(androidContext())),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "AccessManagerCustomProfilePresenter") { (view: AccessManagerCustomProfileContract.View) ->
        AccessManagerCustomProfilePresenter(
            AccessManagerRepository(get()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val viewModelModule = module {
    viewModel {
        AccessManagerBatchChangeProfileViewModel(
            get(),
            get(),
            FeatureTogglePreference.instance
        )
    }
}

val useCaseModule = module {
    factory { GetCustomActiveProfilesUseCase(get()) }
    factory { PostAssignRoleUseCase(get()) }
}

val repositoryModule = module {
    factory<NewAccessManagerRepository> { NewAccessManagerRepositoryImpl(get()) }
}

val remoteDataSourceModule = module {
    factory { AccessManagerRemoteDataSource(get(), get()) }
}

val apiModule = module {
    single("accessManagerApi") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(AccessManagerApi::class.java) as AccessManagerApi
    }

    factory("newAccessManagerApi") {
        createCieloService(
            NewAccessManagerApi::class.java,
            BuildConfig.HOST_API,
            get()
        )
    }
}

val accessManagerModule = listOf(
    presenterModule,
    viewModelModule,
    useCaseModule,
    repositoryModule,
    remoteDataSourceModule,
    apiModule
)