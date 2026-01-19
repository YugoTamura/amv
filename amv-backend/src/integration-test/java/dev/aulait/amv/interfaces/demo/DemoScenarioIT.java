package dev.aulait.amv.interfaces.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.async.AsyncExecStatus;
import dev.aulait.amv.arch.async.AsyncExecWsClient;
import dev.aulait.amv.arch.exec.ExecUtils;
import dev.aulait.amv.arch.test.TestConfig;
import dev.aulait.amv.arch.util.JenvUtils;
import dev.aulait.amv.interfaces.project.CodebaseClient;
import dev.aulait.amv.interfaces.project.CodebaseDto;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

class DemoScenarioIT {

  AsyncExecWsClient asyncExecWsClient = new AsyncExecWsClient();
  CodebaseClient codebaseClient = new CodebaseClient();

  @Test
  @EnabledIf("onCi")
  void test() {
    DemoScenarioFacade.getInstance().runIfNotLoaded();
  }

  boolean onCi() {
    return Boolean.parseBoolean(System.getenv("CI"));
  }

  boolean isJava11Installed() {
    return TestConfig.getInstance().isUsingContainer()
        || StringUtils.isNotEmpty(JenvUtils.javaHome("11"));
  }

  boolean onCiAndJava11() {
    return onCi() && isJava11Installed();
  }

  @Test
  @EnabledIf("onCiAndJava11")
  void java11GradleMyBatisTest() {

    if (TestConfig.getInstance().isUsingContainer()) {
      String dockerPsCmd = "docker ps -q -f \"name=amv-container-back\"";
      String containerId = ExecUtils.execWithStdout(dockerPsCmd, Map.of());
      String dockerCmd =
          String.format(
              "docker exec -u 0 %s chown -R jboss:jboss /home/jboss/.gradle", containerId);
      ExecUtils.exec(dockerCmd, Map.of());
    }

    String codebaseId =
        codebaseClient.save(
            CodebaseDto.builder()
                .name("spring-boot-realworld-example-app")
                .url("https://github.com/gothinkster/spring-boot-realworld-example-app.git")
                .build());

    codebaseClient.analyze(codebaseId);

    assertEquals(AsyncExecStatus.COMPLETED, asyncExecWsClient.waitUntilFinished(codebaseId));
  }
}
