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
}
return this;