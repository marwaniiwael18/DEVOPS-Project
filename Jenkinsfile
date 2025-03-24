pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        registry = "172.20.0.4:8083"  // Nexus Docker registry IP and port
        registryCredentials = "nexus"  // Jenkins credentials ID for Nexus
        imageName = "gestion-station-ski"
        imageTag = "1.0-${env.BUILD_NUMBER}"  // Unique image tag for each build
        gitBranch = "subscription-wael"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"

        // SonarQube configuration
        SONAR_URL = "http://sonar:9000"
        SONAR_TOKEN = "sqa_19f340c425ba1543e3dc43b3961674627c8c958b"
        SONAR_PROJECT_KEY = "gestion-station-ski"
        SONAR_PROJECT_NAME = "Gestion Station Ski"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: gitBranch, credentialsId: 'github', url: gitRepo
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Wait for MySQL') {
            steps {
                script {
                    echo "Checking MySQL connection..."
                    def ready = false
                    def attempts = 0
                    while (!ready && attempts < 15) {
                        try {
                            sh '''
                                mysql -h mysqldb -u root -proot -e 'SELECT 1;'
                            '''
                            ready = true
                            echo "MySQL is ready!"
                        } catch (exc) {
                            attempts++
                            echo "MySQL not ready yet (attempt ${attempts}/15), waiting..."
                            sh 'sleep 5'
                        }
                    }
                    if (!ready) {
                        error "MySQL did not become available in time"
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
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
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv(SONARQUBE_SERVER) {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.tests=src/test/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.junit.reportsPath=target/surefire-reports \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'ls -l'  // Verify Dockerfile presence
                    sh "docker build -t ${registry}/${imageName}:${imageTag} ."
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh """
                            echo "${PASSWORD}" | docker login -u ${USERNAME} --password-stdin ${registry}
                            docker push ${registry}/${imageName}:${imageTag}
                        """
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            cleanWs()
        }
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
