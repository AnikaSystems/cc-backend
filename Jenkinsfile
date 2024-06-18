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

        stage('Test (JUnit / Jacoco)'){
            steps {
                script {
                    echo "Run Unit Tests"
                }
            }
        }

        stage('SonarQube analysis') {
            steps {
                script {
                    withSonarQubeEnv() {
                        sh './gradlew sonar'
                    }
                }
            }
        }

        stage('Deploy to ECR') {
            steps {
                script{
                    def BRANCH_NAME = scm.branches[0].name
                    docker.withRegistry("https://${env.ECR_FQDN}", "ecr:${env.DEPLOY_REGION}:aws-rapid-jenkins-ecr-user") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("${BRANCH_NAME}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Trivy Scan') {
            steps {
                script {
                    echo "Run Trivy Scanner"
                    sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image cc-backend:${env.BUILD_NUMBER}"
                }
            }
        }
    }
}