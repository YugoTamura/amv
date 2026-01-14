package dev.aulait.amv.interfaces.project;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodebaseDataFactory {

  public static CodebaseDto createCodebase() {
    return CodebaseDto.builder()
        .id(RandomStringUtils.insecure().next(22, true, true))
        .name(RandomStringUtils.insecure().next(36, true, true))
        .url(RandomStringUtils.insecure().next(36, true, true))
        .build();
  }
}
