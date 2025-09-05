#!groovy

node('android') {
    if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'homolog' || env.BRANCH_NAME == 'master') {
        if (env.BRANCH_NAME == 'develop') {
            SCHEDULE = pipelineTriggers([cron('@daily'), pollSCM('H/30 * * * *')])
        } else {
            SCHEDULE = pipelineTriggers([pollSCM('H/30 * * * *')])
        }
        properties([
            buildDiscarder(
                logRotator(
                    artifactDaysToKeepStr: '',
                    artifactNumToKeepStr: '',
                    daysToKeepStr: '3',
                    numToKeepStr: '2'
                )
            ),
            disableConcurrentBuilds(),
            [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: true],
            SCHEDULE
        ])
        wrap([$class: 'TimestamperBuildWrapper']) {
            def currentBranch = env.BRANCH_NAME.replaceAll('/', '-')
            println("currentBranch: " + currentBranch)

            def currentJob = env.JOB_NAME.replace("${JOB_BASE_NAME}", currentBranch)
            println("currentJob: " + currentJob)

            ws(env.WORKSPACE_HOME + currentJob) {
                def EMULATOR_PORT=5556
                def EMULATOR_NAME="emulator-${EMULATOR_PORT}"
                def EMULATOR_STARTED = false

                def BUILD_STARTED = false
                def EXCEPTION = null

                try {
                    stage('Check out branch') {
                        checkout scm
                        sh 'export EMULATOR_PORT2=' + EMULATOR_PORT
                        sh 'printenv'
                        sh 'chmod 766 gradlew'
                        sh 'chmod +x avd-manager.sh'
                    }

                    stage('Create local.properties') {
                        sh'''cat <<EOF >local.properties
############################## KeyStore Release ##############################
store_file_keystore_release_cielo=cieloDebug.keystore
store_password_release_cielo=123456
key_alias_release_cielo=cielo
key_password_release_cielo=123456

############################## KeyStore Release LIO##############################
store_file_keystore_release_cielo_lio=cieloDebug.keystore
store_password_release_cielo_lio=123456
key_alias_release_cielo_lio=cielo
key_password_release_cielo_lio=123456
                        '''
                    }
                    
                    sendBitbucketStatus('INPROGRESS')
                    BUILD_STARTED = true

                    if (env.BRANCH_NAME == 'develop') {
                        stage('Unit tests') {
                            sh './gradlew javaCodeCoverage'
                        }

                        stage('Unit tests reports') {
                            junit allowEmptyResults: true, testResults: '**/build/test-results/testStoreMockUnitTest/*.xml'
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'app/build/reports/tests/testStoreMockUnitTest',
                                reportFiles: 'index.html',
                                reportName: 'Unit tests report'
                            ])
                        }

                        stage('Start avd') {
                            try {
                                timeout(time: 2) {
                                    println("Trying to connect on emulator: " + EMULATOR_NAME)
                                    sh './avd-manager.sh'
                                    EMULATOR_STARTED = true
                                    println("Successfully connected on emulator: " + EMULATOR_NAME)
                                }
                            } catch (Exception e) {
                                println("EXCEPTION: " + e)
                                def mensagemErro = "Time to start emulator exceeded."
                                error "${mensagemErro}"
                            }
                        }

                        stage('Instrumentation tests') {
                            sh './gradlew connectedStoreMockAndroidTest -Pdevices=' + EMULATOR_NAME
                        }

                        stopEmulator(EMULATOR_NAME)
                        EMULATOR_STARTED = false

                        stage('Instrumentation tests reports') {
                            junit allowEmptyResults: true, testResults: 'app/build/outputs/androidTest-results/connected/flavors/STORE/TEST*.xml'
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'app/build/reports/androidTests/connected/flavors/STORE',
                                reportFiles: 'index.html',
                                reportName: 'Instrumentation tests report'
                            ])
                        }

                        //stage('Code coverage reports') {
                        // jacoco classPattern: 'app/build/intermediates/classes', exclusionPattern: '\'**/R.class\', \'**/R$*.class\', \'**/BuildConfig.*\', \'**/Manifest*.*\', \'**/*Test*.*\', \'**/RecyclerViewMatcher.class\', \'**/TimeUtils.class\', \'android/**/*.*\', \'**/*$*.class\', \'**/*Robot*.class\', \'**/splash/**\'', execPattern: 'app/build/jacoco/**.exec', inclusionPattern: '**/*Presenter*.class'
                        //}

                        stage('SonarQube: analisys') {
                            withSonarQubeEnv('SonarQube') {
                                sh './gradlew --info sonarqube -Dsonar.host.url=${SONAR_HOST_URL}'
                            }
                        }

                        /*stage('SonarQube: result') {
                            timeout(time: 5) { //setando timeout para 5m; O pipeline será abortado após esse tempo
                                def qg = waitForQualityGate() //Obtém taskId, setado no contexto do pipeline ao usar o método 'withSonarQubeEnv'
                                if (qg.status != 'OK') {
                                    def mensagemErro = "Pipeline execution aborted because status '"  + qg.status + "' of quality gate."
                                    error "${mensagemErro}"
                                }
                            }
                        }*/
                    }

                    if (env.BRANCH_NAME == 'homolog') {
                        stage('Build project') {
                            parallel 'HML': {
                                sh './gradlew assembleStoreHomolog'
                            }, 'PRD': {
                                sh './gradlew assembleStoreRelease'
                            }, failFast: true
                        }
                        /*
                        stage('Functional tests') {
                            sh './gradlew test'
                        }
                        */
                        stage('Write release notes') {
                            commitHash = getCommitHashFromLastSuccessfulBuild()

                            parallel 'HML': {
                                FILE_NAME_HML = "release_notes_hml.txt"
                                sh 'echo Ambiente: Homologação > ' + FILE_NAME_HML
                                writeGitLogOnFile(commitHash, FILE_NAME_HML)
                            }, 'PRD': {
                                FILE_NAME_PRD = "release_notes_prd.txt"
                                sh 'echo Ambiente: Produção > ' + FILE_NAME_PRD
                                writeGitLogOnFile(commitHash, FILE_NAME_PRD)
                            }, failFast: true
                        }

                        stage('Send apk to Beta') {
//                            sh './gradlew crashlyticsUploadDistributionStoreRelease'
                            sh './gradlew crashlyticsUploadDistributionStoreHomolog'
                        }

                        archivePrdApk()
                    }

                    if (env.BRANCH_NAME == 'master') {
                        stage('Save files from job Homolog') {
                            copyArtifacts filter: 'app/build/outputs/**/app-store-release.apk, app/build/outputs/**/mapping.txt',
                                fingerprintArtifacts: true,
                                projectName: 'cielo-app/android/homolog',
                                selector: lastSuccessful()
                        }

                        archivePrdApk()
                    }

                    if ( (BUILD_STARTED) ) {
                        sendBitbucketStatus('SUCCESSFUL')
                    }
                } catch (Exception e) {
                    EXCEPTION = e
                    println("EXCEPTION: " + EXCEPTION)
                    println("EXCEPTION MESSAGE: " + EXCEPTION.getMessage())

                    if (EXCEPTION.getMessage().contains("code 143")) {
                        currentBuild.result = "ABORTED"
                    } else {
                        currentBuild.result = "FAILURE"
                    }

                    if (EMULATOR_STARTED) {
                        stopEmulator(EMULATOR_NAME)
                    }

                    if (BUILD_STARTED) {
                        sendBitbucketStatus('FAILED')
                    }

                    println("build result: " + currentBuild.result)

                    if( (currentBuild.result != null) && (currentBuild.result != "ABORTED") )  {
                        /*stage('Send e-mail') {
                            mail(
                                //to: 'dev_mpayment_cieloapp@m4u.com.br',
                                to: 'bruno.macedo@m4u.com.br',
                                subject: "[Cielo App] Job '" + env.JOB_NAME.replaceAll('%2F', '/') + " (${env.BUILD_NUMBER})' status is " + currentBuild.result,
                                body: "Please go to: ${env.RUN_DISPLAY_URL}"
                            )
                        }*/

                        stage('Send Slack notification') {
                            def message = "@channel\nJob *'" + env.JOB_NAME.replaceAll('%2F', '/') + " (${env.BUILD_NUMBER})'* status is *" +
                                    currentBuild.result + "*\n" + "Please go to: ${env.RUN_DISPLAY_URL}"
                            slackSend channel: '#android', color: 'danger', message: message, teamDomain: 'cielo-app', token: 'XgNkll49nQDuVVkMcRN8L4t1'
                        }
                    }

                    throw EXCEPTION
                }
            }
        }
    }
}

def sendBitbucketStatus(def status) {
    stage('Send Bitbucket status') {
        bitbucketStatusNotify(
            buildState: status
        )
    }
}

def stopEmulator(def name) {
    stage('Stop avd') {
        println("Shutting down emulator: " + name)

        sh '''expect << EOF
			spawn telnet localhost 5556
			expect "OK"
			send   "auth YADulXZ8yDqTLZPc\\r"
			expect "OK"
			send   "kill\\r"
			expect "OK: killing emulator, bye bye"
			expect "OK"
			expect "Connection closed by foreign host."
			send   "exit\\r"
		'''
    }
}

def getCommitHashFromLastSuccessfulBuild() {
    def lastSuccessfulBuild = null

    try {
        lastSuccessfulBuild = currentBuild.rawBuild.getPreviousSuccessfulBuild()
        return lastSuccessfulBuild.getAction(hudson.plugins.git.util.BuildData.class).lastBuiltRevision.sha1String
    } catch(Exception e) {
        println("Error on recovering commit hash from last successful build. EXCEPTION: " + e)
        return null
    }
}

def writeGitLogOnFile(String commitHash, String filename) {
    sh 'date +"%F %T" >> ' + filename
    GIT_LOG_FORMAT = "--format=%B"

    if (commitHash != null) {
        println("commitHash: " + commitHash)
        sh 'git log ' + GIT_LOG_FORMAT + ' ' + commitHash + '..HEAD >> ' + filename
    } else {
        sh 'git log -1 ' + GIT_LOG_FORMAT + ' >> ' + filename
    }
}

def archivePrdApk() {
    stage('Archive PRD apk') {
        archiveArtifacts 'app/build/outputs/apk/store/release/app-store-release.apk'
        archiveArtifacts 'app/build/outputs/mapping/store/release/mapping.txt'
    }
}