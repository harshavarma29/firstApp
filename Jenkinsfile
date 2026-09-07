pipeline {
    agent {
        kubernetes {
            yaml '''
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                  - name: jnlp
                    resources:
                      requests:
                        memory: "512Mi"
                        cpu: "200m"
                      limits:
                        memory: "1Gi"
                        cpu: "500m"
                  - name: jdk
                    image: eclipse-temurin:17-jdk
                    command: ["cat"]
                    tty: true
                    resources:
                      requests:
                        memory: "1Gi"
                        cpu: "500m"
                      limits:
                        memory: "3Gi"
                        cpu: "1"
                  - name: kaniko
                    image: gcr.io/kaniko-project/executor:debug
                    command: ["sleep"]
                    args: ["9999999"]
                    resources:
                      requests:
                        memory: "512Mi"
                        cpu: "250m"
                      limits:
                        memory: "2Gi"
                        cpu: "1"
                  - name: kubectl
                    image: bitnami/kubectl:latest
                    command: ["sleep"]
                    args: ["9999999"]
                    resources:
                      requests:
                        memory: "128Mi"
                        cpu: "100m"
                      limits:
                        memory: "256Mi"
                        cpu: "250m"
            '''
        }
    }

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
                container('jdk') {
                    sh 'echo "Running in container:" && hostname'
                    sh 'which java && java -version'
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build --no-daemon'
                }
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