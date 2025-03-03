pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')
        // Set these to your actual MySQL server
        MYSQL_HOST = '192.168.77.129'  // Host IP where MySQL is running
        MYSQL_PORT = '3306'
        MYSQL_DB = 'stationSki'
        MYSQL_USER = 'root'
        MYSQL_PASS = ''
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
                            -Dspring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=${MYSQL_USER} \
                            -Dspring.datasource.password=${MYSQL_PASS} \
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
    }
}