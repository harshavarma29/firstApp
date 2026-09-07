pipeline {
    agent any

    environment {
        ACR_NAME = "firstappacr.azurecr.io"
        IMAGE_NAME = "firstapp"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR file with Gradle') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${ACR_NAME}/${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'firstapp-credentials-access-azure', usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
                    sh "echo $ACR_PASS | docker login ${ACR_NAME} -u $ACR_USER --password-stdin"
                    sh "docker push ${ACR_NAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }

        }

        stage('Deploy to AKS') {
            steps {
                withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {
                    sh """
                        sed -e 's|IMAGE_TAG_PLACEHOLDER|${IMAGE_TAG}|g' deployment.yaml > deployment-final.yaml
                        kubectl apply -f deployment-final.yaml
                        kubectl apply -f service.yaml
                    """
                }
            }
        }

    }
}