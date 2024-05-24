pipeline {
    agent { label'ec2-agent' }
    options {
        skipStagesAfterUnstable()
    }
    stages {
         stage('Clone repository') { 
            steps { 
                script{
                checkout scm
                }
            }
        }
        stage('Build') { 
            steps { 
                script{
                 app = docker.build("cc-backend:${env.BUILD_NUMBER}")
                }
            }
        }
        stage('Deploy to ECR') {
            steps {
                script{
                    docker.withRegistry('https://246534174064.dkr.ecr.us-east-1.amazonaws.com', "ecr:us-east-1:${env.PIPELINE_CREDENTIAL_NAME}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
    }
}