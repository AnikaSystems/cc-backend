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
                        echo "Run SonarQube Scanner"
                    }
                }
            }
        }

        stage('Deploy to ECR') {
            steps {
                script{
                    def BRANCH_NAME = scm.branches[0].name
                    docker.withRegistry("https://${env.ECR_FQDN}", "ecr:${env.DEPLOY_REGION}:aws-rapid-jenkins-user") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("${BRANCH_NAME}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Trivy Image Scan') {
            steps {
                script {
                    echo "Run Trivy Image Scanner"
                    sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image cc-backend:${env.BUILD_NUMBER}"
                }
            }
        }

        stage('Trivy Secret Scan') {
            steps {
                script {
                    echo "Run Trivy GitHub Repo Scanner"
                    def BRANCH_NAME = scm.branches[0].name
                    echo "Scanning on branch: ${BRANCH_NAME}"
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'GITHUB_PAT', usernameVariable: 'DUMMY_USER')]) {
                        sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -e GITHUB_TOKEN=${GITHUB_PAT} aquasec/trivy repo github.com/AnikaSystems/cc-backend --branch ${BRANCH_NAME} --scanners secret"
                    }
                }
            }
        }
    }
}