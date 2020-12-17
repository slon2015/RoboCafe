import com.github.rmee.cli.base.Cli
import com.github.rmee.kubectl.KubectlExecSpec
import com.github.rmee.kubectl.KubectlExtension
import com.google.cloud.tools.minikube.MinikubeTask
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

fun KubectlExtension.exec(action: KubectlExecSpec.() -> Unit) {
    val spec = KubectlExecSpec()
    project.configure(spec, closureOf(action))
    exec(spec)
}

plugins {
    id("org.unbroken-dome.helm-releases") version "1.3.0"
    id("com.github.rmee.kubectl") version "1.1.20200614081240"
    id("com.google.cloud.tools.minikube") version "1.0.0-alpha.3"
}

val allConfigMapName = "robocafeall"

group = "com.robocafe.all"

helm {
    releases {
        create("all") {
            from(chart(project = ":helmCharts", chart = "all"))
            valueFiles.setFrom("./all/values.yml")
        }
    }
}

kubectl {
    namespace = "default"
    cli(closureOf<Cli> {
        isDockerized = false
    })
}

tasks.getByName("minikubeStart", MinikubeTask::class) {
    flags = arrayOf("--vm-driver=docker")
}

tasks.create("prepareForBuildImages") {
    val dockerEnv = minikube.getDockerEnv("minikube")
    parent!!.tasks.withType<BootBuildImage> {
        docker {
            host = dockerEnv["DOCKER_HOST"]
            certPath = dockerEnv["DOCKER_CERT_PATH"]
            isTlsVerify = dockerEnv["DOCKER_TLS_VERIFY"] == "1"
        }
    }
}

tasks.register("createAllConfigMap") {
    group = "setup"
    kubectl.exec {
        commandLine = listOf("kubectl","delete","configmap","robocafeall")
        isIgnoreExitValue = true
    }
    kubectl.exec("create configmap robocafeall --from-file=./all/config.yml")
}

tasks.getByName("helmInstall") {
    dependsOn("createAllConfigMap", ":bootBuildImage")
}



