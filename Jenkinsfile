pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "192.168.100.47:8083"
        imageName = "SpringApplication"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}" // Génère un tag unique
    }

    stages {

       stage('Checkout Code') {
           steps {
               script {
                   git branch: 'main', url: 'https://github.com/Aymenjallouli/Devops.git'
                   sh 'tree -L 3' // Affiche l'arborescence sur 3 niveaux
               }
           }
       }


        stage('Verify pom.xml') {
            steps {
                script {
                    sh 'if [ ! -f pom.xml ]; then echo "pom.xml NOT FOUND!"; exit 1; fi'
                }
            }
        }

        stage('Install dependencies') {
            steps {
                script {
                    sh 'cd $WORKSPACE && mvn clean install'
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    sh 'cd $WORKSPACE && mvn test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool name: 'scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv('SonarQube') {
                        sh "${scannerHome}/bin/sonar-scanner"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'ls -l'  // Vérifier que Dockerfile est bien présent
                    sh "docker build -t $registry/$imageName:$imageTag ."
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push $registry/$imageName:$imageTag"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed! Check the logs."
        }
    }
}
