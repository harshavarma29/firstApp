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
                  - name: git
                    image: alpine/git:latest
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
                script {
                    def lastCommitMessage = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    if (lastCommitMessage.startsWith('Update firstapp image to tag')) {
                        currentBuild.result = 'ABORTED'
                        error('Skipping build: this commit was made by Jenkins itself, not a real code change.')
                    }
                }
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

        stage('Update Manifest for ArgoCD') {
            steps {
                container('git') {
                    withCredentials([usernamePassword(credentialsId: 'github-push-image-access', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
                        sh """
                            git config --global user.email "jenkins@firstapp.local"
                            git config --global user.name "Jenkins CI"
                            git config --global --add safe.directory '*'
                            sed -i 's|${ACR_NAME}/${IMAGE_NAME}:.*|${ACR_NAME}/${IMAGE_NAME}:${IMAGE_TAG}|g' k8s/deployment.yaml
                            git add k8s/deployment.yaml
                            git commit -m "Update firstapp image to tag ${IMAGE_TAG}" || echo "No changes to commit"
                            git push https://\$GIT_USER:\$GIT_TOKEN@github.com/harshavarma29/firstApp.git HEAD:dev-argocd
                        """
                    }
                }
            }
        }

    }
}