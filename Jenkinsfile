pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend: build & test') {
            steps {
                // Root pom.xml is a reactor aggregator (see CONVENTIONS.md): this builds
                // common-lib before the services that depend on it, then every Spring Boot
                // service, in one dependency-ordered pass.
                sh './mvnw -B clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend: lint & build') {
            parallel {
                stage('berkay-public') {
                    steps {
                        dir('berkay-public') {
                            sh 'npm ci'
                            sh 'npm run lint'
                            sh 'npm run build'
                        }
                    }
                }
                stage('berkay-personnel') {
                    steps {
                        dir('berkay-personnel') {
                            sh 'npm ci'
                            sh 'npm run lint'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }

        stage('Docker: build images') {
            steps {
                // Builds every service's image via docker-compose.yml (task 0.2) without
                // starting the stack or pushing anywhere - proves each Dockerfile still
                // builds on top of the freshly-verified jars/bundles above. Registry push
                // and per-environment deploy are added in ANALYSIS.md task 10.3.
                sh 'docker compose build'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
