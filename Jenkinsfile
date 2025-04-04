pipeline {
    agent any

    environment {
        gitBranch = "Yassine/Skier"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"
        registryCredentials = "nexus"
        registry = "10.0.2.15:8083"
        imageName = "yassinemanai_4twin3_thunder_gestionski"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}"

        // SonarQube
        SONAR_URL = "http://10.0.2.15:9000/"
        SONAR_TOKEN = "squ_45cb3eca7530f4e79b58393efd58ffb745510a0d"
        SONAR_PROJECT_KEY = "YassineManai_4twin3_gestionski_v2"
        SONAR_PROJECT_NAME = "YassineManai-4Twin3-GestionSki-V2"
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    git branch: gitBranch, url: gitRepo
                    sh 'ls -l'  // Verify Files
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                script {
                    sh 'mvn clean install -DskipTests'  // Clean & Install Dependencies
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
                script {
                    sh 'ls -l'  // Verify Dockerfile presence
                    // Build with the full registry path in the image name
                    sh "docker build -t ${registry}/${imageName}:${imageTag} ."
                }
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
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed! Check the logs."
            archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}