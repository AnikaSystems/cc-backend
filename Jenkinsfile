pipeline {
    agent any
    tools {docker "docker"}
    stages {
         stage('Clone repository') { 
            steps { 
                script{
                checkout scm
                }
            }
        }
        stage('Initialize'){
            steps {
                def dockerHome = tool 'docker'
                env.PATH = "${dockerHome}/bin:${env.PATH}"
            }
        }
        stage('Build') { 
            steps { 
                script{
                 docker.build("springboot-activemq")
                 //sh './gradlew clean bootJar'
                }
            }
        }
        // stage('Test'){
        //     steps {
        //         // Run Gradle tests
        //         sh './gradlew clean test --no-daemon'
        //     }
        // }
        // stage('Deploy') {
        //     steps {
        //         script{
        //                 docker.withRegistry('246534174064.dkr.ecr.us-east-1.amazonaws.com', 'ecr:us-east-1:aws-credentials') {
        //             app.push("${env.BUILD_NUMBER}")
        //             app.push("latest")
        //             }
        //         }
        //     }
        // }
    }
}