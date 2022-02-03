/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/
def call(stages){

    def listStagesOrder = [
        'build': 'stageCleanBuildTest',
        'sonar': 'stageSonar',
        'run_spring_curl': 'stageRunSpringCurl',
        'upload_nexus': 'stageUploadNexus',
        'download_nexus': 'stageDownloadNexus',
        'run_jar': 'stageRunJar',
        'curl_jar': 'stageCurlJar'
    ]
​
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
​
def stageRunSpringCurl() {
    stage("Paso 3: Curl Springboot Gradle sleep 20"){
        env.TAREA = "Paso 3: Curl Springboot Gradle sleep 20"
        sh "gradle bootRun&"
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}

def stageUploadNexus() {

    stage("Paso 4: Subir Nexus"){
        env.TAREA = "Paso 4: Subir Nexus"
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'devops-usach-nexus',
        packages: [
            [$class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '',
                    extension: 'jar',
                    filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar'
                ]
            ],
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '0.0.1'
                ]
            ]
        ]
    }
}

def stageDownloadNexus() {

    stage("Paso 5: Descargar Nexus"){
        env.TAREA = "Paso 5: Descargar Nexus"
        sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
    }
}
def stageRunJar() {
    stage("Paso 6: Levantar Artefacto Jar"){
        env.TAREA = "Paso 6: Levantar Artefacto Jar"
        sh 'nohup bash java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'
    }
}
def stageCurlJar() {
    stage("Paso 7: Testear Artefacto - Dormir(Esperar 50sg) "){
        env.TAREA = "Paso 7: Testear Artefacto"
        sh "sleep 50 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}
return this;