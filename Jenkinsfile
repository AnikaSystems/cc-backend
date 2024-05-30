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
                // Run Gradle tests
                sh './gradlew clean test jacocoTestReport --no-daemon'
            }
            post {
                always {
                    junit 'build/test-results/**/*.xml'
                }
            }
        }

        stage('SonarQube analysis') {
            steps {
                script {
                    withSonarQubeEnv() {
                        sh './gradlew sonarqube'
                    }
                }
            }
        }

        stage('Deploy to ECR') {
            steps {
                script{
                    def BRANCH_NAME = scm.branches[0].name
                    docker.withRegistry("https://${env.ECR_FQDN}", "ecr:${env.DEPLOY_REGION}:${env.PIPELINE_CREDENTIAL_NAME}") {
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
                }
            }
        }
    }
}