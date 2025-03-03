pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')
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
            post {
                success {
                    echo "Maven build successful!"
                }
                failure {
                    echo "Maven build failed!"
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        echo "Running JUnit tests..."
                        sh 'mvn test'
                    } catch (Exception e) {
                        echo "JUnit tests failed: ${e}"
                        throw e
                    }
                }
            }
            post {
                success {
                    echo "All JUnit tests passed!"
                }
                failure {
                    echo "Some JUnit tests failed!"
                    script {
                        currentBuild.result = 'UNSTABLE'
                    }
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
                            -Dsonar.projectKey=tn.esprit.myspringapp \
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e}"
                        throw e
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

        stage('Publish Test Results') {
            steps {
                script {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        success {
            echo "Build, Tests, and SonarQube analysis completed successfully!"
        }
        failure {
            echo "Build, Tests, or SonarQube analysis failed!"
        }
    }
}
