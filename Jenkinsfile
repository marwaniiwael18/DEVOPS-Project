pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        NEXUS_URL = "http://nexus:8081/repository/maven-releases11/"
        NEXUS_CREDENTIALS = "nexus"
        registry = "http://nexus:8081"  // Nexus Repository
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
                script {
                    try {
                        sh 'mvn clean test -Dspring.profiles.active=test'
                    } catch (Exception e) {
                        echo "Tests failed, but continuing pipeline"
                        currentBuild.result = 'UNSTABLE'
                    } finally {
                        archiveArtifacts artifacts: 'target/surefire-reports/*', fingerprint: true
                    }
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
                    sh "docker build -t nexus:8081/gestion-station-ski:$imageTag ."  // Remove 'http://'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh """
                    mvn deploy -DskipTests \\
                    -s /var/jenkins_home/.m2/settings.xml
                """
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