pipeline {
    agent any
    environment {
        SONARQUBE_SERVER = 'SonarQube'
        dockerHubRepo = 'marwaniwael/gestion-ski'
        imageTag = "1.0-${env.BUILD_NUMBER}"  // Unique Tag per Build
        dockerHubCredentials = 'docker-hub'  // Jenkins credentials ID for Docker Hub (you must add it in Jenkins)
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Build') {
            steps {
                // Add JaCoCo agent here
                sh 'mvn clean compile jacoco:prepare-agent'
            }
        }



        stage('Run Tests') {
            steps {
                sh 'mvn test jacoco:report'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: '**/test/**'
                )
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
                    sh "docker build -t ${dockerHubRepo}:${imageTag} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', dockerHubCredentials) {
                        sh "docker push ${dockerHubRepo}:${imageTag}"
                    }
                }
            }
        }

         stage('Run Application') {
             steps {
                 script {
                     // Pull image from Docker Hub
                     sh "docker pull ${dockerHubRepo}:${imageTag}"

                     // Replace IMAGE_TAG placeholder in docker-compose.yml
                     sh "sed -i 's|marwaniwael/gestion-ski:IMAGE_TAG|${dockerHubRepo}:${imageTag}|g' docker-compose.yml"
                     sh "cat docker-compose.yml" // Optional: For debugging


                     // Run application with updated tag
                     withEnv(["IMAGE_TAG=${imageTag}"]) {
                         sh "IMAGE_TAG=${imageTag} docker-compose up -d"
                     }
                 }
             }
         }
          stage("Run Prometheus") {
                     steps {
                         script {
                             sh 'docker start prometheus || docker run -d --name prometheus prom/prometheus'
                         }
                     }
                 }

                 stage("Run Grafana") {
                     steps {
                         script {
                             sh 'docker start grafana || docker run -d --name grafana grafana/grafana'
                         }
                     }
                 }

    }

    post {
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
