pipeline {
    agent any

    tools {
        maven 'my_maven'  // Correct syntax: specify Maven tool name here
    }

    stages {
        stage('Check PWD') {
                steps {
                    script {
                        // Print the current working directory
                        echo "Current working directory: ${pwd()}"
                }
            }
       https://image-media.trangiangkhanh.site/db0d8fc2-a53f-4fec-8c7f-c901cecd1b4b_2025-01-09T07:51:04.273111110.png }

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

        stage('Step 1.1: Navigate and Build music-service') {
            steps {
                dir('music-service') {
                    echo 'Navigated to music-service directory'
                    sh 'mvn clean package'
                    echo 'Source built successfully with Maven'
                }
            }
        }


        stage('Step 1.2: Navigate to auth-service') {
            steps {
                dir('auth-service') {
                    echo 'Navigated to auth-service directory'
                    sh 'mvn clean package'
                    echo 'Source built successfully with Maven'
                }
            }
        }

        stage('Step 1.3: Navigate to media-service') {
            steps {
                dir('media-service') {
                    echo 'Navigated to media-service directory'
                        sh 'mvn clean package'
                        echo 'Source built successfully with Maven'
                }
            }
        }

        stage('Step 1.4: Navigate to api-gateway') {
            steps {
                dir('api-gateway') {
                    echo 'Navigated to api-gateway directory'
                    sh 'mvn clean package'
                    echo 'Source built successfully with Maven'
                }
            }
        }
        
        stage('Step 2: Remove old container') {
            steps {
                sh 'docker-compose down'
                echo 'Remove container successfully'
            }
        }

        stage('Step 3: Run Docker Composes') {
            steps {
                sh 'docker-compose up -d'
                echo 'Docker image running on port 8005'
            }
        }

        stage('Step 4: Remove Dangling Images') {
            steps {
                script {
                    // Get list of dangling image IDs
                    def danglingImages = sh(script: 'docker images -f "dangling=true" -q', returnStdout: true).trim()

                    // Check if there are any dangling images
                    if (danglingImages) {
                        sh "echo '${danglingImages}' | xargs docker rmi"
                        echo 'Dangling images removed successfully!'
                    } else {
                        echo 'No dangling images to remove.'
                    }
                }
            }
        }
    }
}