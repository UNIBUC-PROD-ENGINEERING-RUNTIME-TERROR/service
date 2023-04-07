pipeline {
    agent any
    environment {
        DOCKER_HUB_CREDS = credentials("docker_hub_creds")
        GITHUB_TOKEN_CREDS = credentials("github_token")
    }

    stages {
        stage("Build & Test") {
            steps {
                sh "./gradlew clean build"
            }
        }

        stage("Run unit tests") {
            steps {
                sh "./gradlew test"
            }
        }

        stage("Tag latest image") {
            steps {
                script {
                    sh([script: 'git fetch --tag', returnStdout: true]).trim()
                    env.MAJOR_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 1', returnStdout: true]).trim()
                    env.MINOR_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 2', returnStdout: true]).trim()
                    env.PATCH_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 3', returnStdout: true]).trim()
                    env.IMAGE_TAG = "${env.MAJOR_VERSION}.\$((${env.MINOR_VERSION} + 1)).${env.PATCH_VERSION}"
                }
            }
        }

        stage("Build & push new image") {
            steps {
                sh "docker build -t $DOCKER_HUB_CREDS_USR/hello-img:${env.IMAGE_TAG} ."
                sh "echo $DOCKER_HUB_CREDS_PSW | docker login docker.io -u $DOCKER_HUB_CREDS_USR --password-stdin"
                sh "docker push $DOCKER_HUB_CREDS_USR/hello-img:${env.IMAGE_VERSION}"
            }
        }

        stage("Update github latest image tag") {
            steps {
                sh "git tag ${env.IMAGE_TAG}"
                sh "git push https://$GITHUB_TOKEN_CREDS_PSW@github.com/UNIBUC-PROD-ENGINEERING-RUNTIME-TERROR/service.git ${env.IMAGE_TAG}"
            }
        }

        stage ("Deploy & run integration tests") {
            steps {
                sh "IMAGE_TAG=${env.IMAGE_TAG} DOCKER_HUB_USERNAME=$DOCKER_HUB_CREDS_USR docker-compose up -d hello"
                sh "./gradlew testIT"
                sh "./gradlew testE2E"
            }
        }
    }
}
