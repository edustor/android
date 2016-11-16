node {
    withCredentials([string(credentialsId: 'ci.sign.android.edustor.ru', variable: 'ciPass')]) {
        env.EDUSTOR_ANDROID_CI_KEY_PASSWORD = ciPass
    }

    withCredentials([string(credentialsId: 'debug.sign.android.edustor.ru', variable: 'debugPass')]) {
        env.EDUSTOR_ANDROID_DEBUG_KEY_PASSWORD = debugPass
    }

    stage "Environment preparation"

    docker.image("wutiarn/android").inside("-v /mnt/media/jenkins/cache/.gradle:/root/.gradle") {
        checkout scm

        stage "Dependencies download"
        sh "./gradlew"

        stage "Debug assemble"
        sh "./gradlew assembleDebug"

        stage "CI assemble"
        sh "./gradlew assembleCI"

        stage "Release assemble"
        sh "./gradlew assemblerelease"

        sh "mv app/build/outputs/apk/* ."
        sh "rm -f *-unaligned.apk"
        archiveArtifacts '*.apk'
    }
}