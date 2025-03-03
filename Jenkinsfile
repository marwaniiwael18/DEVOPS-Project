pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')
        // Adding database configuration for tests
        DB_HOST = '127.0.0.1'
        DB_PORT = '3306'
        DB_NAME = 'stationSki'
        DB_USER = 'root'
        DB_PASS = ''
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
            post {
                success {
                    echo "Maven build successful!"
                }
                failure {
                    echo "Maven build failed!"
                }
            }
        }
        
        stage('Setup Test Database') {
            steps {
                script {
                    // Check if MySQL is running, start if needed
                    sh '''
                        if ! docker ps | grep -q mysql; then
                            echo "Starting MySQL container for tests..."
                            docker run --name mysql-test -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e MYSQL_DATABASE=stationSki -p 3306:3306 -d mysql:5.7
                            # Wait for MySQL to initialize
                            echo "Waiting for MySQL to initialize..."
                            sleep 20
                        else
                            echo "MySQL is already running"
                        fi
                    '''
                    
                    // Verify database connection
                    sh '''
                        echo "Verifying database connection..."
                        docker exec mysql-test mysql -u root -e "SELECT 1;"
                        echo "Database is ready!"
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        echo "Running JUnit tests..."
                        // Pass database configuration to tests
                        sh '''
                            mvn test -Dspring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=${DB_USER} \
                            -Dspring.datasource.password=${DB_PASS} \
                            -Dspring.jpa.hibernate.ddl-auto=update
                        '''
                    } catch (Exception e) {
                        echo "JUnit tests failed: ${e}"
                        // Don't throw the exception - let Jenkins handle test failures properly
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                success {
                    echo "All JUnit tests passed!"
                }
                failure {
                    echo "Some JUnit tests failed!"
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { currentBuild.result != 'FAILURE' }
            }
            steps {
                script {
                    try {
                        echo "Running SonarQube analysis..."
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
            post {
                success {
                    echo "SonarQube analysis successful!"
                }
                failure {
                    echo "SonarQube analysis failed!"
                }
            }
        }
    }

    post {
        always {
            script {
                // Clean up database container
                sh '''
                    echo "Cleaning up test resources..."
                    if docker ps -a | grep -q mysql-test; then
                        docker stop mysql-test || true
                        docker rm mysql-test || true
                    fi
                '''
            }
        }
        success {
            echo "Build, Tests, and SonarQube analysis completed successfully!"
        }
        failure {
            echo "Build, Tests, or SonarQube analysis failed!"
        }
    }
}