pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')
        DB_HOST = '192.168.77.129' // Use host IP instead of localhost
        DB_PORT = '3306'
        DB_NAME = 'stationSki'
        DB_USER = 'root'
        DB_PASS = ''
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

        stage('Run Docker Compose') {
            steps {
                // Use docker-compose on the host machine
                sh 'cd ${WORKSPACE} && docker-compose up -d'
                // Wait for MySQL to be ready
                sh 'sleep 15'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        echo "Running JUnit tests..."
                        sh '''
                            mvn test -Dspring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=${DB_USER} \
                            -Dspring.datasource.password=${DB_PASS} \
                            -Dspring.jpa.hibernate.ddl-auto=update
                        '''
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
    }

    post {
        always {
            // Clean up resources, but only if we started them
            sh 'cd ${WORKSPACE} && docker-compose down || true'
        }
    }
}