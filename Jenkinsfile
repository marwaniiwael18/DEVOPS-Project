pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')  // Ensure 'scanner' matches the Jenkins credential ID
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "192.168.77.129:8081"
        NEXUS_REPOSITORY = "maven-releases"
        NEXUS_CREDENTIAL_ID = "nexus-credentials"
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
                            -Dspring.datasource.url=jdbc:mysql://192.168.77.129:3306/stationSki?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=root \
                            -Dspring.datasource.password= \
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

        stage('Package Application') {
            steps {
                script {
                    echo "Packaging the application..."
                    sh "mvn package -DskipTests"
                    
                    // Archive the packaged .jar file in Jenkins
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
            post {
                success {
                    echo "Application packaged successfully!"
                }
                failure {
                    echo "Failed to package application!"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image..."
                    sh "docker build -t myspringapp:${BUILD_NUMBER} ."
                    sh "docker tag myspringapp:${BUILD_NUMBER} myspringapp:latest"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    try {
                        // Read POM details
                        def pom = readMavenPom file: 'pom.xml'
                        
                        // Upload to Nexus
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: pom.version,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [
                                    artifactId: pom.artifactId,
                                    classifier: '',
                                    file: "target/${pom.artifactId}-${pom.version}.jar",
                                    type: 'jar'
                                ]
                            ]
                        )
                        
                        echo "Artifact successfully uploaded to Nexus!"
                    } catch (Exception e) {
                        echo "Failed to upload artifact to Nexus: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }
}