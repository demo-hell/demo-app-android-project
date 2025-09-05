# Data Layer

Camada responsável por toda a comunicação com o CORE e controle dos dados armezenados localmente.


![data layer img](./readme/imgs/dataLayer.png?raw=true) 

## Clients

- Local Data Source: Responsável pela gerenciamento dos dados locais.
- Remote Data Source: Responsável pelo gerenciamento dos dados remotos, como a comunicação com o Core.


## Repository

- Responsável por gerenciar os dados locais e remoto. É a unica classe da Data Layer que tem comunicação com a Presentation Layer.

## Informações importantes

#####- URL
As urls ficam todas no ![build.gradlew](./app/build.gradlew). As urls dependem do build variant.
Para usar a brokenServiceUrl basta chamar BuildConfig.SERVER_URL

```kotlin
        ...
        homolog {
            debuggable true
            buildConfigField "String", "SERVER_URL", "\"http://54.211.209.82:8080\""
            ...
        }

        dev {
            debuggable true
            buildConfigField "String", "SERVER_URL", "\"http://54.211.209.82:8082\""
            ...
        }
        ...
```
 
#####- CieloAPI (br.com.mobicare.cielo.commons.data.clients.api)
Onde se encontram todos os endpoints

```kotlin
    ...
    @GET("/api/customer/" + BuildConfig.API_VERSION + "/checkMassiva/{ec}")
    fun isMassiva(@Path("ec") ec: String): Observable<MassivaStatusObj>
    
    @POST("/api/customer/" + BuildConfig.API_VERSION + "/login")
    fun login(@Body params: LoginParams): Observable<LoginObj>
    ...
```


#####- CieloAPIServices (br.com.mobicare.cielo.commons.data.clients.api)
Onde se encontra a configuração do client

```kotlin
    ...
    val client: OkHttpClient
            get() {
                val httpClient = OkHttpClient.Builder()
                httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)
                httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                httpClient.addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                            .addHeader("deviceId", DeviceInfo.getInstance().deviceId)
                            .addHeader("os", Build.VERSION.SDK_INT.toString())
                            .addHeader("appVersion", BuildConfig.VERSION_NAME.toString())
                            .addHeader("Content-Type", "application/json")
                            .addHeader("channel", "ANDROID")
                    val token = UserPreferences.getInstance().getToken(context)
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.addHeader("token", token)
                    }
    
                    val ec = MenuPreference.instance.getEC(context)
                    if (ec != null && !ec.isEmpty()) {
                        requestBuilder.addHeader("ec", ec)
                    }
    
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
    
                return httpClient.build()
            }
    ...
```

## Libraries

- Retrofit
- RX
- Gson

```kotlin

    //Retrofit
    compile 'com.squareup.retrofit2:retrofit:2.0.2'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.2'
    compile 'com.squareup.retrofit2:converter-gson:2.0.0'
    //RxAndroid
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'io.reactivex:rxjava:1.1.6'

```