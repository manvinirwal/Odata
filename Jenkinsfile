pipeline {
    agent any
    environment{

      GIT_URL = "https://github.com/manvinirwal/Odata.git"
    }
    stages {
         stage("Git clone"){
             steps{
                 cleanWs()
                 git credentialsId: 'GIT_CRED', url: "${GIT_URL}"
                 script {
                  env.COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                 }
             }
          }
      
        stage("Maven Clean, Build, Docker Push"){
              steps{
                      withCredentials([string(credentialsId: 'DOCKER_PASS', variable: 'DOCKER_PASS')]) {
                       sh "docker login -u vinchilu -p ${DOCKER_PASS}"
                       }
                      sh "chmod +x mvnw"
                      sh "docker build . -t vinchilu/sample:${COMMIT_ID}"
                      sh "docker push vinchilu/sample:${COMMIT_ID}"
               }
           }
        
        
    }
}