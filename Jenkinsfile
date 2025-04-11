pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        dockerHubRepo = 'marwaniwael/gestion-ski'
        imageTag = "1.0-${env.BUILD_NUMBER}"
        dockerHubCredentials = 'docker-hub'
        EMAIL_RECIPIENTS = 'marwani.wael88@gmail.com'
        EMAIL_SENDER = 'marwani.wael88@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Build') {
            steps {
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
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
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
                sh "docker build -t ${dockerHubRepo}:${imageTag} ."
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
                    sh '''
                        if [ "$(docker ps -a -q -f name=mysql_db)" ]; then
                            echo "Removing existing mysql_db container..."
                            docker rm -f mysql_db
                        fi

                        docker-compose down || true
                    '''

                    sh "sed -i 's|marwaniwael/gestion-ski:IMAGE_TAG|${dockerHubRepo}:${imageTag}|g' docker-compose.yml"
                    sh "cat docker-compose.yml"

                    sh "IMAGE_TAG=${imageTag} docker-compose up -d"
                }
            }
        }

        stage("Run Prometheus") {
            steps {
                script {
                    sh 'docker start prometheus || docker run -d --name prometheus -p 9090:9090 prom/prometheus'
                }
            }
        }

        stage("Run Grafana") {
            steps {
                script {
                    sh 'docker start grafana || docker run -d --name grafana -p 3000:3000 grafana/grafana'
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build successful!"
            emailext(
                subject: "✅ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                    <html>
                    <body>
                        <h2>✅ Build Successful: ${env.JOB_NAME}</h2>
                        <p>Build #${BUILD_NUMBER} completed successfully!</p>

                        <h3>Build Info:</h3>
                        <ul>
                            <li>Status: ${currentBuild.currentResult}</li>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Tag: ${dockerHubRepo}:${imageTag}</li>
                            <li>Build URL: <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                            <li>Duration: ${currentBuild.durationString}</li>
                        </ul>

                        <p><a href="${BUILD_URL}console">View Console Output</a></p>
                        <p>-- Jenkins CI/CD</p>
                    </body>
                    </html>
                """,
                to: "${EMAIL_RECIPIENTS}",
                from: "${EMAIL_SENDER}",
                mimeType: 'text/html',
                attachLog: true
            )
        }

        failure {
            echo "❌ Build failed!"
            emailext(
                subject: "❌ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                    <html>
                    <body>
                        <h2>❌ Build Failed: ${env.JOB_NAME}</h2>
                        <p>Build #${BUILD_NUMBER} has failed!</p>

                        <h3>Build Info:</h3>
                        <ul>
                            <li>Status: ${currentBuild.currentResult}</li>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Tag: ${dockerHubRepo}:${imageTag}</li>
                            <li>Build URL: <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                            <li>Duration: ${currentBuild.durationString}</li>
                        </ul>

                        <p><a href="${BUILD_URL}console">View Console Output</a></p>
                        <p>-- Jenkins CI/CD</p>
                    </body>
                    </html>
                """,
                to: "${EMAIL_RECIPIENTS}",
                from: "${EMAIL_SENDER}",
                mimeType: 'text/html',
                attachLog: true
            )
        }
    }
}
