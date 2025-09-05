# Cielo App  

Aplicativo cliente (Android) para a Cielo.

## Flavors

**Dev**: Código referente ao aplicativo que será utilizado durante o processo de desenvolvimento e homologação. <br>
**Store**: Código referente ao aplicativo que será enviado para a Play Store.

## Build Variants

**devHomolog**: ambiente utilizado para o desenvolvimento das demandas pelo time dev. 
O ambiente está apontado para HML e utilizando o flavor de homolog que possibilita o processo de debug.<br>

**devRelease**: ambiente utilizado para o homologação das demandas pelo time de QA. 
O ambiente está apontado para HML e utilizando o flavor de release que impossibilita o processo de debug.<br>

**storeHomolog**: ambiente apontado para produção que utiliza o flavor de homolog  que **permite o debug**.<br>
**storeRelease**: ambiente apontado para produção que utiliza o flavor de release que **não permite o debug**.<br>

## Pré-requisitos para executar o projeto

#### 1 - baixar pasta keys que contém credenciais de acesso
[Link para download](https://cielo-my.sharepoint.com/:f:/g/personal/caues_cielo_com_br/EtEmAWG1toZAsYuWVs96P5oBpecMFl9ekvW6id3rih0PuQ?e=rafiHq) 

#### 2 - adicionar o local.properties no projeto
**Atenção:**
Os campos **release_cielo_store_file** e **cielo_guardsquare** devem apontar para **keys/cielo-release.keystore** e **keys/dexguard-license.txt**, respectivamente. Sendo *keys* a pasta baixada no passo anterior.

```
MacOs:

sdk.dir=/Users/yourUsername/Library/Android/sdk
release_cielo_key_password=0mz19nx2
release_cielo_store_file=/Users/yourUsername/Documents/keys/cielo-release.keystore
release_cielo_key_alias=cielo
release_cielo_store_key_password=cielodwpibom
allow_me_credentials_username=platao
allow_me_credentials_password=maze road gift aids news dean
cielo_guardsquare=/Users/yourUsername/Documents/keys/dexguard-license.txt
firebase_keystore_file=/Users/yourUsername/Documents/cielo/cieloFirebase.json
path_apk=/Users/yourUsername/Documents/cielo/app/build/outputs/apk/dev/release/app-dev-release-protected.apk
```

```
Windows:

sdk.dir=C\:\\Users\\yourUsername\\AppData\\Local\\Android\\Sdk
release_cielo_key_password=0mz19nx2
release_cielo_store_file=C\:\\Users\\yourUsername\\Documentos\\key\\cielo-release.keystore
release_cielo_key_alias=cielo
release_cielo_store_key_password=cielodwpibom
allow_me_credentials_username=platao
allow_me_credentials_password=maze road gift aids news dean
cielo_guardsquare=C\:\\Users\\yourUsername\\Documentos\\key\\dexguard-license.txt
```

## Arquitetura

O aplicativo foi feito seguindo o MVP (Model - View - Presenter), separados em 3 principais camadas:

### Camadas


- [DATA LAYER](./DATALAYER.md)
- [PRESENTATION LAYER](./PRESENTATIONLAYER.md)
- [DOMAIN LAYER](./DOMAINLAYER.md)


![Alt text](./readme/imgs/layers.png?raw=true) 


### Comunicacao entre as camadas

A comunicação entra Data Layer e Presentation Layer é dado atraves do repository e do presenter


![comunicacao img](./readme/imgs/comunicacao.png?raw=true) 

### Existe uma classe Injection (br.com.mobicare.cielo.injection), para auxiliar nas dependencias

```kotlin
object Injection {
    ...
    fun provideSplashRepository(context: Context): SplashRepository {
            checkNotNull(context)
            return SplashRepository.getInstance(
                    SplashAPIDataSource.getInstance(context),
                    SplashLocalDataSource.getInstance())
        }
    
    fun provideMeusRecebimentosHomeRepository(context: Context): MeusRecebimentosRepository {
            checkNotNull(context)
            return  MeusRecebimentosRepository.getInstance(MeusRecebimentosAPIDataSource.getInstance(context))
        }
    ...
}
```

```kotlin
class MeusRecebimentosFragment : android.support.v4.app.Fragment(), MeusRecebimentosContract.View {

    ...
    override fun onStart() {
        super.onStart()

        ...
        presenter = MeusRecebimentosPresenter(this, context, Injection.provideMeusRecebimentosRepository(context))
        ...
    }
}

```
