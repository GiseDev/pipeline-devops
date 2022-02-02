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
                  switch(params.compileTool)
                    {
                        case 'Maven':
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
                        break;
                        case 'Gradle':
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        break;
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