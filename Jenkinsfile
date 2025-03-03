pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')  // Ensure 'scanner' matches the Jenkins credential ID
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'registration', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        echo "Running JUnit tests using external MySQL..."
                        sh """
                            mvn test \
                            -Dspring.datasource.url=jdbc:mysql://192.168.77.129:3306/stationSki?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=root \
                            -Dspring.datasource.password= \
                            -Dspring.jpa.hibernate.ddl-auto=update
                        """
                    } catch (Exception e) {
                        echo "JUnit tests failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    try {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=tn.esprit.myspringapp \
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Package Application') {
            steps {
                script {
                    echo "Packaging the application..."
                    sh "mvn package -DskipTests"
                    
                    // Archive the packaged .jar file in Jenkins
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
            post {
                success {
                    echo "Application packaged successfully!"
                }
                failure {
                    echo "Failed to package application!"
                }
            }
        }
    }
}
