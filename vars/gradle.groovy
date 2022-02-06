import utilities.*

def call(stages)
{
    def listStagesOrder = [
        'git' : 'gitmerge',
        'build': 'stageCleanBuildTest',
        'sonar': 'stageSonar',
        'run_spring_curl': 'stageRunSpringCurl',
        'upload_nexus': 'stageUploadNexus',
        'download_nexus': 'stageDownloadNexus',
        'run_jar': 'stageRunJar',
        'curl_jar': 'stageCurlJar'
    ]

    def arrayUtils = new array.arrayExtentions();
    def stagesArray = []
        stagesArray = arrayUtils.searchKeyInArray(stages, ";", listStagesOrder)

    if (stagesArray.isEmpty()) {
        echo 'El pipeline se ejecutará completo'
        allStages()
    } else {
        echo 'Stages a ejecutar :' + stagesArray
        stagesArray.each{ stageFunction ->//variable as param
            echo 'Ejecutando ' + stageFunction
            "${stageFunction}"()
        }
    }
}

def gitmerge(){
    //git remote add origin 'https://${env.GITHUB_TOKEN}@github.com/GiseDev/ejemplo_gradle1.git'
    withCredentials([
            gitUsernamePassword(credentialsId: 'jenkins-git-user', gitToolName: 'Default', variable: 'TOKEN')
        ]) { 
            sh "echo 'en git step'"
            sh '''
                git fetch -p 
                git checkout ''develop''; git pull
                git checkout ''feature-test-git''
                git merge develop;
                git push origin ''feature-test-git''
              '''
        }
}

def stageCleanBuildTest(){
    env.TAREA = "Paso 1: Build && Test"
    stage("$env.TAREA"){
        sh "echo 'Build && Test!'"
        sh "gradle clean build"
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

def stageRunSpringCurl() {
    stage("Paso 3: Curl Springboot Gradle sleep 20"){
        env.TAREA = "Paso 3: Curl Springboot Gradle sleep 30"
        sh "gradle bootRun&"
        sh "sleep 30 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
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

def allStages(){
    stageCleanBuildTest()
    stageSonar()
    stageRunSpringCurl()
    stageUploadNexus()
    stageDownloadNexus()
    stageRunJar()
    stageCurlJar()
}
return this;