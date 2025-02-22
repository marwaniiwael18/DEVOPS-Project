pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "springapplication"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Generates a unique tag for each build
        gitRepo = "https://github.com/Aymenjallouli/Devops.git"
        gitBranch = "CoursTest"
    }

    stages {

        stage('Checkout Code') {
            steps {
                script {
                    git branch: gitBranch, url: gitRepo
                    sh 'ls -l'  // Verify if the files are present
                }
            }
        }

        stage('Install dependencies') {
            steps {
                script {
                    sh 'mvn clean install'  // Install dependencies, clean the project and compile
                }
            }
        }

        stage('Check Maven Version') {
            steps {
                script {
                    sh 'mvn -v'  // Check Maven version to ensure Maven is installed correctly
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    sh 'mvn test'  // Run unit tests
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
                    sh 'ls -l'  // Check if the Dockerfile is present
                    sh "docker build -t $registry/$imageName:$imageTag ."  // Build Docker image with the appropriate tag
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push $registry/$imageName:$imageTag"  // Push the Docker image to Nexus registry
                    }
                }
            }
        }

        stage('Stop Services with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down || true'  // Stop any running services, don't fail if no containers are running
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Replace the image tag in docker-compose.yml dynamically
                    sh "sed -i 's|image: ${registry}/${imageName}:latest|image: ${registry}/${imageName}:${imageTag}|g' docker-compose.yml"

                    // Print the updated docker-compose file for verification
                    sh "cat docker-compose.yml"

                    // Start the services using Docker Compose
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Commit Code to Git') {
            steps {
                script {
                    sh 'git config --global user.email "aymen.jallouli@esprit.tn"'  // Configure git user
                    sh 'git config --global user.name "Aymenjallouli"'
                    sh 'git add .'  // Stage the changes
                    sh 'git commit -m "Automated commit after build and deployment"'  // Commit changes
                    sh 'git push origin $gitBranch'  // Push to the Git repository
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
