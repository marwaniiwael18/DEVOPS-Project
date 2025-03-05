pipeline {
    agent any

    environment {
        gitBranch = "Yassine/Skier"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"

          // SonarQube
                SONAR_URL = "http://sonar:9000"
                SONAR_TOKEN = "squ_e0bf2d44f9b939e174de3785765df2a6ccffe154"
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

        stage('Run Unit Tests') {
            steps {
                script {
                    sh 'mvn test -Dspring.profiles.active=test'  // Run Unit Tests
                }
            }
        }
    }
     stage('SonarQube Analysis') {
                steps {
                    script {
                        def scannerHome = tool 'Sonar-Scan'
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