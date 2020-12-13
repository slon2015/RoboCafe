import com.github.rmee.cli.base.Cli
import com.github.rmee.cli.base.CliExecSpec
import com.github.rmee.kubectl.KubectlExec

plugins {
    id("org.unbroken-dome.helm-releases") version "1.3.0"
    id("com.github.rmee.kubectl") version "1.1.20200614081240"
}

val allConfigMapName = "robocafeall"

group = "com.robocafe.all"

helm {
    releases {
        all {
            from(chart(project = ":helmCharts", chart = "all"))
        }
    }
}

tasks.create("createAllConfigMap", Exec::class) {
    group = "setup"
    cliExec.exec(CliExecSpec())
}