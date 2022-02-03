def call(stages)
{
    echo 'Stages a ejecutar :' + stages
    def mapa = [100 : 'Continue', 200 : 'OK', 400 : 'badRequest']
    echo 'valor => ' + mapa.size()
    def listStagesOrder = [
        'build': 'stageCleanBuildTest',
        'sonar': 'stageSonar',
        'run_spring_curl': 'stageRunSpringCurl',
        'upload_nexus': 'stageUploadNexus',
        'download_nexus': 'stageDownloadNexus',
        'run_jar': 'stageRunJar',
        'curl_jar': 'stageCurlJar'
    ]

    if (stages.isEmpty()) {
        echo 'El pipeline se ejecutará completo'
        allStages()
    } else {
        echo 'Stages a ejecutar :' + stages
        listStagesOrder.each { stageName, stageFunction ->
            stages.each{ stageToExecute ->//variable as param
                if(stageName.equals(stageToExecute)){
                echo 'Ejecutando ' + stageFunction
                "${stageFunction}"()
                }
            }
        }
​
    }
}
def stageCleanBuildTest(){
    env.TAREA = "Paso 1: Build && Test"
    stage("$env.TAREA"){
        sh "echo 'Build && Test!'"
        sh "gradle clean build"
        // code
    }
}

def stageSonar() {
    stage("Paso 2: Sonar - Análisis Estático"){
        env.TAREA = "Paso 2: Sonar - Análisis Estático"
        sh "echo 'Análisis Estático!'"
        withSonarQubeEnv('sonarqube') {
            sh './gradlew sonarqube -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build'
        }
    }
}

def allStages(){
    stageCleanBuildTest()
    stageSonar()
}
return this;