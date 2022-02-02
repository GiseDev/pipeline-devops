def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('NEXUS-USER')
            NEXUS_PASSWORD     = credentials('NEXUS-PASS')
        }
        parameters {
            choice(
                name:'compileTool',
                choices: ['Maven', 'Gradle'],
                description: 'Seleccione herramienta de compilacion'
            )
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                    if(params.compileTool == 'maven'){
                            //compilar maven
                            //def executor = load "maven.groovy"
                            //executor.call()
                            maven.call();
                        }else{
                            //compilar gradle
                            //def executor = load "gradle.groovy"
                            //executor.call()
                            gradle.call()
                        }
                    }
                }
                post{
                    success{
                        slackSend color: 'good', message: "Giselle [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa. Ejecutado con [${params.compileTool}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slack-token'
                    }
                    failure{
                        slackSend color: 'danger', message: "Giselle [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]. Ejecutado con [${params.compileTool}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slack-token'
                    }
                }
            }
        }
    }
}
return this;