pipeline {
    agent any

    tools {
        maven 'my_maven'  // Correct syntax: specify Maven tool name here
    }

    stages {

        stage('Set Build Name') {
            steps {
                script {
                    // Retrieve the commit message from the latest commit
                    def commitMessage = sh(
                        script: "git log -1 --pretty=%B",
                        returnStdout: true
                    ).trim()
                    // Set the build display name to the commit message
                    currentBuild.displayName = commitMessage
                }
            }
        }

        stage('Step 1: Navigate to music-service') {
            steps {
                dir('music-service') {
                    echo 'Navigated to music-service directory'
                }
            }
        }
        stage('Step 2: Build source with Maven') {
            steps {
                dir('music-service') {
                    sh 'mvn clean package'
                    echo 'Source built successfully with Maven'
                }
            }
        }
        
        
        stage('Step 3: Remove old container') {
            steps {
                dir('music-service') {
                    sh 'docker rm -f music-service'
                    echo 'Remove container successfully'
                }
            }
        }

        stage('Step 4: Build Docker image') {
            steps {
                dir('music-service') {
                    sh 'docker build -t music-service:latest .'
                    echo 'Docker image built successfully'
                }
            }
        }
        stage('Step 5: Run Docker image') {
            steps {
                sh 'docker run -d -p 8005:8080 --name music-service music-service:latest'
                echo 'Docker image running on port 8005'
            }
        }

        stage('Step 6: Remove Dangling Images') {
            steps {
                sh 'docker images -f "dangling=true" -q | xargs docker rmi'
                echo 'Dangling images removed successfully'
            }
        }
    }
}