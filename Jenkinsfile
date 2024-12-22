pipeline {
    agent any

    tools {
        maven 'my_maven'  // Correct syntax: specify Maven tool name here
        dockerTool 'my_docker'  // Corrected tool type for Docker
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
        stage('Step2.999: Start Docker Daemon') {
            steps {
                script {
                    // Start Docker service if it's not running
                    sh 'sudo systemctl start docker'
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