pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "aymenjallouli_4twin3_thunder_gestionski"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Génère un tag unique pour chaque build
        gitBranch = "Aymenjallouli_4twin3_thunder"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"  // Ajout de l'URL du repo
        SONAR_URL = "http://192.168.100.47:9000"  // URL de SonarQube
        SONAR_TOKEN = "squ_65d3b090f57666eaa1f74c863a93e4010b788917"  // Token SonarQube
        SONAR_PROJECT_KEY = "AymenJallouli_Twin3_GestionSki"  // Clé du projet SonarQube
        SONAR_PROJECT_NAME = "AymenJallouli_Twin3_GestionSki"  // Nom du projet SonarQube
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    git branch: gitBranch, url: gitRepo
                    sh 'ls -l'  // Vérifier si les fichiers sont présents
                }
            }
        }

        stage('Install dependencies') {
            steps {
                script {
                    sh 'mvn clean install'  // Installer les dépendances, nettoyer et compiler
                }
            }
        }

        stage('Check Maven Version') {
            steps {
                script {
                    sh 'mvn -v'  // Vérifier la version de Maven
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    sh 'mvn test jacoco:report'  // Exécuter les tests unitaires
                }
            }
        }

        stage('Create SonarQube Project') {
            steps {
                script {
                    // Créez un projet SonarQube via l'API
                    sh """
                        curl -u ${SONAR_TOKEN}: -X POST "${SONAR_URL}/api/projects/create" \
                          -d "project=${SONAR_PROJECT_KEY}&name=${SONAR_PROJECT_NAME}"
                    """
                }
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
                            -Dsonar.projectVersion=1.0 \
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
                script {
                    sh 'ls -l'  // Vérifier si le Dockerfile est présent
                    sh "docker build -t $registry/$imageName:$imageTag ."  // Construire l'image Docker avec le tag approprié
                }
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

        stage('Stop Services with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down || true'  // Arrêter les services en cours, ne pas échouer si aucun conteneur n'est en cours d'exécution
                }
            }
        }

        stage('Run Application') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker pull $registry/$imageName:$imageTag"
                        sh "sed -i 's|IMAGE_TAG|${imageTag}|g' docker-compose.yml"

                        // Remplacement dynamique de IMAGE_TAG dans docker-compose.yml
                        sh "sed -i 's|IMAGE_TAG|$imageTag|g' docker-compose.yml"

                        // Vérifier le fichier après modification
                        sh "cat docker-compose.yml"

                        // Lancer les services avec Docker Compose
                        sh "docker-compose up -d"
                    }
                }
            }
        }

        stage("Run Prometheus") {
            steps {
                script {
                    sh 'docker start prometheus || docker run -d --name prometheus prom/prometheus'
                }
            }
        }

        stage("Run Grafana") {
            steps {
                script {
                    sh 'docker start grafana || docker run -d --name grafana grafana/grafana'
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed! Check the logs."
        }
    }
}