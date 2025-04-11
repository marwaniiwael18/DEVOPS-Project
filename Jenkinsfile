pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "localhost:8083"
        imageName = "aymenjallouli_4twin3_thunder_gestionski"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}"
        gitBranch = "Aymenjallouli_4twin3_thunder"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"

        // SonarQube
        SONAR_URL = "http://172.17.0.3:9000"
        SONAR_TOKEN = "squ_d998de19b2b748c23ba26217047d391c04ce8a30"
        SONAR_PROJECT_KEY = "AymenJallouli_4twin3_gestionski_v2"
        SONAR_PROJECT_NAME = "AymenJallouli-4Twin3-GestionSki-V2"

        // Email Configuration
        EMAIL_RECIPIENTS = "aymen.jallouli@esprit.tn"
        EMAIL_SENDER = "Marwaniwael88@gmail.com"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: gitBranch, url: gitRepo
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn verify jacoco:report -Dspring.profiles.active=test -T 1C'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco(execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: '**/test/**')
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Using SonarQube URL: ${SONAR_URL}"
                    def scannerHome = tool 'SonarScan'
                    withSonarQubeEnv {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8 \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.scanner.force-deprecated-java-version=true
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "DOCKER_BUILDKIT=1 docker build -t $registry/$imageName:$imageTag ."
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push --quiet $registry/$imageName:$imageTag"
                    }
                }
            }
        }

        stage('Run Application') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker pull $registry/$imageName:$imageTag"

                        // Replace IMAGE_TAG dynamically in docker-compose.yml
                        sh "sed -i 's|localhost:8083/aymenjallouli_4twin3_thunder_gestionski:IMAGE_TAG|$registry/$imageName:$imageTag|g' docker-compose.yml"
                        sh "cat docker-compose.yml" // Verification

                        // Cleanup existing containers to avoid conflicts
                        sh "docker-compose down --remove-orphans"

                        // Pass IMAGE_TAG explicitly to docker-compose
                        withEnv(["IMAGE_TAG=${imageTag}"]) {
                            sh "IMAGE_TAG=${imageTag} docker-compose up -d"
                        }
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
            echo "✅ Pipeline completed successfully!"
            emailext(
                subject: "✅ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                <html>
                <body>
                    <h2>✅ Build Successful: ${env.JOB_NAME}</h2>
                    <p>Build #${BUILD_NUMBER} completed successfully!</p>
                    
                    <h3>Build Information:</h3>
                    <ul>
                        <li>Status: ${currentBuild.currentResult}</li>
                        <li>Job: ${env.JOB_NAME}</li>
                        <li>Branch: ${gitBranch}</li>
                        <li>Image Tag: ${registry}/${imageName}:${imageTag}</li>
                        <li>Build URL: ${BUILD_URL}</li>
                        <li>Duration: ${currentBuild.durationString}</li>
                    </ul>
                    
                    <p>Check the console output for more details: <a href="${BUILD_URL}console">Console Output</a></p>
                    
                    <p>Regards,<br>Jenkins CI/CD System</p>
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
            echo "❌ Pipeline failed! Check the logs."
            emailext(
                subject: "❌ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                <html>
                <body>
                    <h2>❌ Build Failed: ${env.JOB_NAME}</h2>
                    <p>Build #${BUILD_NUMBER} has failed!</p>
                    
                    <h3>Build Information:</h3>
                    <ul>
                        <li>Status: ${currentBuild.currentResult}</li>
                        <li>Job: ${env.JOB_NAME}</li>
                        <li>Branch: ${gitBranch}</li>
                        <li>Build URL: ${BUILD_URL}</li>
                        <li>Duration: ${currentBuild.durationString}</li>
                    </ul>
                    
                    <p>Please check the console output for error details: <a href="${BUILD_URL}console">Console Output</a></p>
                    
                    <p>Regards,<br>Jenkins CI/CD System</p>
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