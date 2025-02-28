pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
         registry = "nexus:8081"
            registryCredentials = "nexus"
        imageName = "gestion-station-ski"
        imageTag = "1.0-${env.BUILD_NUMBER}"  // Unique Tag per Build
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
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
                    withSonarQubeEnv('SonarQube') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \\
                            -Dsonar.projectKey=gestion-station-ski \\
                            -Dsonar.sources=src/main/java \\
                            -Dsonar.tests=src/test/java \\
                            -Dsonar.java.binaries=target/classes \\
                            -Dsonar.junit.reportsPath=target/surefire-reports \\
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
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
                    sh "docker build -t ${registry}/${imageName}:${imageTag} ."  // Fixed image name
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://${registry}", registryCredentials) {
                        sh "docker push ${registry}/$imageName:$imageTag"
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