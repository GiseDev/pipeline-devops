def call(stages){
    echo 'Stages a ejecutar :' + stages

    //def stagesList = stages.split(";")
    //stagesList.each{
    //     println("===>${it}")
    //     "${it}"()
    //}
//
    //def listStagesOrder = [
    //    'build': 'stageCleanBuildTest',
    //    'sonar': 'stageSonar',
    //    'run_spring_curl': 'stageRunSpringCurl',
    //    'upload_nexus': 'stageUploadNexus',
    //    'download_nexus': 'stageDownloadNexus',
    //    'run_jar': 'stageRunJar',
    //    'curl_jar': 'stageCurlJar'
    //]

    //if (stages == "") {
    //    echo 'El pipeline se ejecutará completo'
    //    allStages()
    //} else {
    //    echo 'Stages a ejecutar :' + stages
    //    listStagesOrder.each { stageName, stageFunction ->
    //        stages.each{ stageToExecute ->//variable as param
    //            if(stageName.equals(stageToExecute)){
    //            echo 'Ejecutando ' + stageFunction
    //            "${stageFunction}"()
    //            }
    //        }
    //    }
​   // }
}
return this;