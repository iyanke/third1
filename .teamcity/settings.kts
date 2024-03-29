import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    buildType(Build)

    subProject(TestMute)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            scriptContent = "echo 1"
        }
    }

    triggers {
        vcs {
        }
    }
})


object TestMute : Project({
    name = "Test Mute"

    vcsRoot(TestMute_HttpsGithubComIyankeTestMuteRefsHeadsMaster)

    buildType(TestMute_Build)
})

object TestMute_Build : BuildType({
    name = "Build"

    vcs {
        root(TestMute_HttpsGithubComIyankeTestMuteRefsHeadsMaster)
    }

    steps {
        maven {
            goals = "clean test"
            pomLocation = ".teamcity/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "New build step"
            goals = "clean test"
            pomLocation = ".teamcity/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }
})

object TestMute_HttpsGithubComIyankeTestMuteRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/iyanke/test_mute#refs/heads/master"
    url = "https://github.com/iyanke/test_mute"
    branch = "refs/heads/master"
    authMethod = password {
        userName = "iyanke"
        password = "credentialsJSON:0a33f2ad-5f87-44fa-ba1c-aa334149e7a5"
    }
    param("useAlternates", "true")
})
