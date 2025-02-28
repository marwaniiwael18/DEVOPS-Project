pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.73.128:9000'  // SonarQube URL
        SONARQUBE_TOKEN = credentials('sonar-api-token')  // SonarQube token stored in Jenkins credentials
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
            post {
                success {
                    echo "Maven build successful!"
                }
                failure {
                    echo "Maven build failed!"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    try {
                        echo "Running SonarQube analysis..."
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=tn.esprit.myspringapp \  // Use your actual project key here
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e}"
                        throw e  // Re-throw the exception to mark the stage as failed
                    }
                }
            }
            post {
                success {
                    echo "SonarQube analysis successful!"
                }
                failure {
                    echo "SonarQube analysis failed!"
                }
            }
        }
    }

    post {
        success {
            echo "Build and SonarQube analysis successful!"
        }
        failure {
            echo "Build or SonarQube analysis failed!"
        }
    }
}
