pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "harshavarma29/firstApp-clone"
        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    stages {

        stage('Build JAR') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG}"

                    withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-credentials',
                            usernameVariable: 'USER',
                            passwordVariable: 'PWD'
                        )]) {
                        sh "docker login -u ${USER} -p ${PWD}"
                        sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Update Helm Chart Tag') {
            steps {
                script {
                    sh "sed -i 's|tag: .*|tag: \"${IMAGE_TAG}\"|g' chart/values.yaml"

                    git config user.name "harshavarma29"
                    git config user.email "harshavarma29@gmail.com"
                    git add chart/values.yaml
                    git commit -m "ci: bump chart image tag to ${IMAGE_TAG}"
                    git push https://\${GITHUB_TOKEN}@github.com/harshavarma29/firstApp.git HEAD:dev
                }
            }
        }

    }
}