pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "localhost:8083"
        imageName = "aymenjallouli_4twin3_thunder_gestionski"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}"
        gitBranch = "Aymenjallouli_4twin3_thunder"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"

        // SonarQube
        SONAR_URL = "http://sonar:9000"
        SONAR_TOKEN = "squ_65d3b090f57666eaa1f74c863a93e4010b788917"
        SONAR_PROJECT_KEY = "AymenJallouli_Twin3_GestionSki"
        SONAR_PROJECT_NAME = "AymenJallouli_Twin3_GestionSki"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: gitBranch, url: gitRepo
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean install test jacoco:report -Dspring.profiles.active=test'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco(execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: '**/test/**')
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarScan'
                    withSonarQubeEnv {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8 \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build --rm -t $registry/$imageName:$imageTag ."
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push $registry/$imageName:$imageTag"  // Pousser l'image Docker vers Nexus
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    sh 'docker-compose down || true' // Arrêter les anciens conteneurs
                    sh "docker pull $registry/$imageName:$imageTag"
                    sh "sed -i 's|IMAGE_TAG|$imageTag|g' docker-compose.yml"
                    sh "docker-compose up -d"
                }
            }
        }

        stage("Start Monitoring Services") {
            steps {
                script {
                    sh 'docker start prometheus || docker run -d --name prometheus prom/prometheus'
                    sh 'docker start grafana || docker run -d --name grafana grafana/grafana'
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
            sh 'docker image prune -a -f' // Nettoyage des anciennes images inutilisées
        }
        failure {
            echo "❌ Pipeline failed! Check the logs."
        }
    }
}
