pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIAL = credentials('docker')
        DOCKER_IMAGE_NAME = "springApp"  // Docker image name
        DOCKER_TAG = "latest"  // Docker image tag
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'registration', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Build Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_HUB_CREDENTIAL_USR', passwordVariable: 'DOCKER_HUB_CREDENTIAL_PSW')]) {
                    sh """
                        docker build -t ${DOCKER_HUB_CREDENTIAL_USR}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .
                    """
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_HUB_CREDENTIAL_USR', passwordVariable: 'DOCKER_HUB_CREDENTIAL_PSW')]) {
                    sh "echo ${DOCKER_HUB_CREDENTIAL_PSW} | docker login -u ${DOCKER_HUB_CREDENTIAL_USR} --password-stdin"
                    sh "docker push ${DOCKER_HUB_CREDENTIAL_USR}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                }
            }
        }
    }

    post {
        success {
            echo "Build and Docker image push successful!"
        }
        failure {
            echo "Build or Docker image push failed!"
        }
    }
}
