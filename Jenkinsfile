pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "springapplication"
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

       stage('Install dependencies') {
           steps {
               script {
                   sh 'mvn clean install '
               }
           }
       }
       stage('Check Maven Version') {
           steps {
               script {
                   sh 'mvn -v'
               }
           }
       }

       stage('Unit Test') {
           steps {
               script {
                   sh 'mvn test '  // Run tests separately
               }
           }
       }
       stage('Check Code Coverage') {
           steps {
               script {
                   jacoco(
                       execPattern: 'target/jacoco.exec',
                       classPattern: 'target/classes',
                       sourcePattern: 'src/main/java',
                       exclusionPattern: '**/test/**',
                       minimumInstructionCoverage: '80',  // Couverture minimale des instructions
                       minimumBranchCoverage: '80',       // Couverture minimale des branches
                       minimumComplexityCoverage: '80',   // Couverture minimale de la complexité
                       minimumLineCoverage: '80',         // Couverture minimale des lignes
                       minimumMethodCoverage: '80',       // Couverture minimale des méthodes
                       minimumClassCoverage: '80'         // Couverture minimale des classes
                   )
               }
           }
       }
       stage('Generate JaCoCo Report') {
           steps {
               script {
                   sh 'mvn jacoco:report'  // Génère le rapport JaCoCo
               }
           }
       }
       stage('Publish JaCoCo Report') {
           steps {
               script {
                   jacoco(
                       execPattern: 'target/jacoco.exec',  // Emplacement du fichier jacoco.exec
                       classPattern: 'target/classes',      // Emplacement des classes compilées
                       sourcePattern: 'src/main/java',      // Emplacement des sources
                       exclusionPattern: '**/test/**'       // Exclure les fichiers de test
                   )
               }
           }
       }

       stage('SonarQube Analysis') {
           steps {
               script {
                   def scannerHome = tool 'SonarScan'
                   withSonarQubeEnv  {
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
                    sh 'docker-compose down || true'  // Ensure the command doesn't fail if no containers are running
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Replace the image tag in docker-compose.yml
                    sh "sed -i 's|image: ${registry}/${imageName}:latest|image: ${registry}/${imageName}:${imageTag}|g' docker-compose.yml"

                    // Verify the changes
                    sh "cat docker-compose.yml"

                    // Start the services
                    sh 'docker-compose up -d'
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