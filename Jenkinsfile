pipeline {
  agent any

  environment {
    // Maven settings.xml 路径（如有私服）
    MAVEN_OPTS = '-Dmaven.repo.local=/home/jenkins/agent/.m2/repository'
    DOCKER_REGISTRY = "harbor.laria.cn"
    DOCKER_NAMESPACE = "default"
    PROJECT_NAME = "e-commerce-platform:1.0-SNAPSHOT"
  }

  tools {
    maven 'maven-3.9.3'  // Jenkins 中定义的 Maven 名称
    jdk 'JDK 17'     // 对应 JDK，或改成你用的版本
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Env Check') {
      steps {
        sh '''
          echo "Java version:"
          java -version
          echo "Maven version:"
          mvn -version
        '''
      }
    }

    stage('Docker Login') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'harbor', // Jenkins中配置的 Harbor 凭据ID
          usernameVariable: 'HARBOR_USER',z
          passwordVariable: 'HARBOR_PASS'
        )]) {
          sh """
            echo "$HARBOR_PASS" | docker login ${DOCKER_REGISTRY} -u "$HARBOR_USER" --password-stdin
          """
        }
      }
    }

    stage('Push Docker Image') {
      steps {
        configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
           sh 'mvn clean deploy -s $MAVEN_SETTINGS -DskipTests=true'
        }
      }
    }

    stage('Deploy to K8s') {
      when {
        expression { fileExists('k8s/deployment.yaml') }
      }
      steps {
        sh """
          kubectl apply -f k8s/deployment.yaml
        """
      }
    }
  }

  post {
    success {
      echo "✅ 部署完成：${PROJECT_NAME}:${env.BUILD_NUMBER}"
    }
    failure {
      echo "❌ 构建或部署失败"
    }
  }
}
