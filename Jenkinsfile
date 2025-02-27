pipeline {
    agent any

environment {
    registryCredentials = "nexus"
    registry = "localhost:8083" // Utilisation du nom du conteneur Nexus au lieu de localhost
    imageName = "aymenjallouli_4twin3_thunder_gestionski"
    imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Génère un tag unique pour chaque build
    gitBranch = "Aymenjallouli_4twin3_thunder"
    gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"  // Ajout de l'URL du repo

    // Configuration de SonarQube
    SONAR_HOST = "sonar"  // Nom du conteneur SonarQube
    SONAR_URL = "http://sonar:9000"  // URL correcte pour Jenkins
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
                    sh 'mvn test jacoco:report -Dspring.profiles.active=test'  // Exécuter les tests unitaires
                }
            }
        }
         stage('Publish JaCoCo Report') {
                    steps {
                        jacoco(
                            execPattern: 'target/jacoco.exec',  // Fichier d'exécution JaCoCo
                            classPattern: 'target/classes',     // Répertoire des classes compilées
                            sourcePattern: 'src/main/java',     // Répertoire des sources
                            exclusionPattern: '**/test/**'      // Exclure les classes de test
                        )
                    }
                }

 stage('Create SonarQube Project') {
    steps {
        script {
            // Vérifier si le projet existe déjà
            def response = sh(
                script: """
                    curl -u ${SONAR_TOKEN}: -s "${SONAR_URL}/api/projects/search?projects=${SONAR_PROJECT_KEY}"
                """,
                returnStdout: true
            ).trim()

            // Vérifier si le projet est bien listé dans la réponse JSON
            if (response.contains("\"key\":\"${SONAR_PROJECT_KEY}\"")) {
                echo "Le projet SonarQube ${SONAR_PROJECT_KEY} existe déjà. Pas besoin de le créer."
            } else {
                echo "Le projet SonarQube ${SONAR_PROJECT_KEY} n'existe pas. Création en cours..."
                sh """
                    curl -u ${SONAR_TOKEN}: -X POST "${SONAR_URL}/api/projects/create" \
                      -d "project=${SONAR_PROJECT_KEY}&name=${SONAR_PROJECT_NAME}"
                """
            }
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
