pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "springapplication"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Génère un tag unique pour chaque build
        gitRepo = "https://github.com/Aymenjallouli/Devops.git"
        gitBranch = "CoursTest"
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
                    sh 'mvn test'  // Exécuter les tests unitaires
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
                            -Dsonar.projectKey=CoursTest \
                            -Dsonar.projectName=CoursTest \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8
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

                       // Remplacement dynamique de IMAGE_TAG dans docker-compose.yml
                       sh "sed -i 's|IMAGE_TAG|$imageTag|g' docker-compose.yml"

                       // Vérifier le fichier après modification
                       sh "cat docker-compose.yml"


                   }
               }
           }
       }

               stage("Run prometheus") {
                   steps {
                       script {
                           sh 'docker start prometheus || docker run -d --name prometheus prom/prometheus'
                          }
                   }
               }
               stage("Run grafana") {
                   steps {
                       script {
                           sh 'docker start grafana || docker run -d --name grafana grafana/grafana'
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
