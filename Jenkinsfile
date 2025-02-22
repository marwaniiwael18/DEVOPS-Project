pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "SpringApplication"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Génère un tag unique
    }

    stages {

        stage('Checkout Code') {
            steps {
                script {
                    git branch: 'CoursTest', url: 'https://github.com/Aymenjallouli/Devops.git'
                    sh 'ls -l'  // Vérifier si les fichiers sont bien présents
                }
            }
        }

        stage('Start Services with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose up -d'
                    sh 'sleep 20' // Attendre que MySQL soit prêt
                }
            }
        }

        stage('Install dependencies') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool name: 'scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv('SonarQube') {
                        sh "${scannerHome}/bin/sonar-scanner"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'ls -l'  // Vérifier que Dockerfile est bien présent
                    sh "docker build -t $registry/$imageName:$imageTag ."
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push $registry/$imageName:$imageTag"
                    }
                }
            }
        }

        stage('Stop Services with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down'
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed! Check the logs."
        }
    }
}