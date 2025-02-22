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

       stage('Install dependencies') {
           steps {
               script {
                   sh 'mvn clean install -DskipTests'  // Skip tests during installation
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

       stage('SonarQube Analysis') {
           steps {
               script {
                   def scannerHome = tool 'SonarScan'
                   withSonarQubeEnv('SonarQube') {
                       sh """
                           ${scannerHome}/bin/sonar-scanner \
                           -Dsonar.projectKey=CoursTest \
                           -Dsonar.projectName=CoursTest \
                           -Dsonar.projectVersion=1.0 \
                           -Dsonar.sources=src \
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