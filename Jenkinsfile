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
                    image: alpine/k8s:1.28.9
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
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build --no-daemon'
                }
            }
        }

        stage('Build & Push Image with Kaniko') {
            steps {
                container('kaniko') {
                    withCredentials([usernamePassword(credentialsId: 'firstapp-credentials-access-azure', usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
                        sh '''
                            mkdir -p /kaniko/.docker
                            AUTH=$(printf "%s:%s" "$ACR_USER" "$ACR_PASS" | base64 | tr -d '\\n')
                            echo "{\\"auths\\":{\\"$ACR_NAME\\":{\\"auth\\":\\"$AUTH\\"}}}" > /kaniko/.docker/config.json
                            /kaniko/executor \
                              --context "$(pwd)" \
                              --dockerfile Dockerfile \
                              --destination "$ACR_NAME/$IMAGE_NAME:$IMAGE_TAG"
                        '''
                    }
                }
            }
        }

        stage('Deploy to AKS') {
            steps {
                container('kubectl') {
                    withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {
                        sh """
                            sed -e 's|IMAGE_TAG_PLACEHOLDER|${IMAGE_TAG}|g' k8s/deployment.yaml > k8s/deployment-final.yaml
                            kubectl apply -f k8s/deployment-final.yaml
                            kubectl apply -f k8s/service.yaml
                        """
                    }
                }
            }
        }

    }
}