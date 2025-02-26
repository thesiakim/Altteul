pipeline {
    agent any

    environment {
        PROJECT_NAME = 'altteul'
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        GIT_REPO = 'https://lab.ssafy.com/s12-webmobile1-sub1/S12P11C203.git'
        GIT_BRANCH = 'master'
    }

    stages {

        stage("Clean") {
            steps {
                cleanWs(
                    deleteDirs: true,
                    cleanWhenFailure : true
                )
            }

            post {
                failure {
                    echo "❌ Clean Failed!"
                }
                success {
                    echo "✅ Clean Success!"
                }
            }
        }

        stage('Git Clone') {
            steps {
                git branch: "${GIT_BRANCH}",
                credentialsId: 'C203',
                url: "${GIT_REPO}"
            }

            post {
                failure {
                    echo "❌ Checkout Failed!"
                }
                success {
                    echo "✅ Checkout Success!"
                }
            }
        }

        stage('Secret Download') {
            steps {
                withCredentials([
                    file(credentialsId: 'BE-ENV', variable: 'BE_ENV_FILE'),
                    file(credentialsId: 'FE-ENV', variable: 'FE_ENV_FILE'),
                    file(credentialsId: 'REDIS_CONF', variable: 'REDIS_CONF_FILE'),
                    file(credentialsId: 'MOCK_DATA', variable: 'MOCK_DATA_FILE')
                ]) {
                    script {
                        sh '''
                        # 백엔드 환경변수 파일 복사
                        cp $BE_ENV_FILE altteul-be/.env
                        cp $BE_ENV_FILE .env

                        # 프론트엔드 환경변수 파일 복사
                        cp $FE_ENV_FILE altteul-fe/.env

                        # Redis 설정 파일 복사
                        mkdir -p resources/redis
                        cp $REDIS_CONF_FILE resources/redis/redis.conf
                        chmod 774 resources/redis/redis.conf

                        # SQL 데이터 복사 (초기 데이터 로딩용)
                        cp $MOCK_DATA_FILE altteul-be/src/main/resources/data.sql
                        '''
                    }
                }
            }
        }

        stage('Stop Previous Containers') {
            steps {
                script {
                    // 기존 실행 중인 컨테이너들 정리
                    sh "docker compose down || true"
                }
            }
        }

        stage('Start Containers') {
            steps {
                script {
                    sh '''
                    docker compose up --build -d
                    '''
                }
            }
        }
    }

    post {
        failure {
            node('built-in') {  // 내장 노드 사용
                script {
                    sh "docker compose logs"  // 실패 시 도커 로그 확인
                }
            }
        }
    }
}
