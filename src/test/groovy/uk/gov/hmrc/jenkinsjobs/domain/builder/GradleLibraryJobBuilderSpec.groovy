package uk.gov.hmrc.jenkinsjobs.domain.builder

import javaposse.jobdsl.dsl.Job
import spock.lang.Specification
import uk.gov.hmrc.jenkinsjobs.JobParents

@Mixin(JobParents)
class GradleLibraryJobBuilderSpec extends Specification {

    void 'test XML output'() {
        given:
        GradleLibraryJobBuilder jobBuilder = new GradleLibraryJobBuilder('test-job')

        when:
        Job job = jobBuilder.build(jobParent())

        then:
        with(job.node) {
            scm.userRemoteConfigs.'hudson.plugins.git.UserRemoteConfig'.url.text() == 'git@github.com:hmrc/test-job.git'
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('CLASSPATH')
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('JAVA_HOME')
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('PATH')
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('TMP')
            triggers.'com.cloudbees.jenkins.gitHubPushTrigger'.spec.text() == ''
            builders.'hudson.plugins.gradle.Gradle'.tasks.text().contains('clean test bintrayUpload --info')
            buildWrappers.'hudson.plugins.build__timeout.BuildTimeoutWrapper'.strategy.timeoutMinutes.text() == '10'
        }
    }

    void 'test extended build timeout'() {
        given:
        GradleLibraryJobBuilder jobBuilder = new GradleLibraryJobBuilder('test-job')

        when:
        Job job = jobBuilder.withExtendedTimeout().build(jobParent())

        then:
        with(job.node) {
            buildWrappers.'hudson.plugins.build__timeout.BuildTimeoutWrapper'.strategy.timeoutMinutes.text() == '20'
        }
    }
}
