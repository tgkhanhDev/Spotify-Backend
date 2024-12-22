pipeline {
    agent any

    tools {
        maven 'my_maven'  // 'Maven' matches the name you configured in Global Tool Configuration
    }

    stages {
        stage('Check Versions') {
            steps {
                echo 'Checking Java and Maven versions...'
                sh 'java -version'  // Check Java version
                sh 'mvn -v'  // Check Maven version
            }
        }
        stage('Test') {
            steps {
                echo 'Jenkins pipeline is redeploying!!!'
            }
        }
        stage('Step1: Navigate to music-service') {
            steps {
                dir('music-service') {
                    echo 'Navigated to music-service directory'
                }
            }
        }
        stage('Step2: Build source with Maven') {
            steps {
                dir('music-service') {
                    sh 'mvn clean package'
                    echo 'Source built successfully with Maven'
                }
            }
        }
        stage('Step3: Build Docker image') {
            steps {
                dir('music-service') {
                    sh 'docker build -t music-service:latest .'
                    echo 'Docker image built successfully'
                }
            }
        }
        stage('Step4: Run Docker image') {
            steps {
                sh 'docker run -d -p 8005:8080 --name music-service music-service:latest'
                echo 'Docker image running on port 8005'
            }
        }
        stage('Step5: Done') {
            steps {
                echo 'Pipeline execution completed successfully!'
            }
        }
    }
}