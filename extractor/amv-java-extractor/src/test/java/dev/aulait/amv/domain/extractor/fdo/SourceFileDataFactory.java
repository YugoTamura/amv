package dev.aulait.amv.domain.extractor.fdo;

import org.apache.commons.lang3.StringUtils;

public class SourceFileDataFactory {

  public static MethodFdo findMethodByName(SourceFdo source, String methodName) {
    for (TypeFdo type : source.getTypes()) {
      for (MethodFdo method : type.getMethods()) {
        if (method.getName().equals(methodName)) {
          return method;
        }
      }
    }
    return null;
  }

  public static MethodCallFdo findMethodCallByName(
      SourceFdo source, String callerMethodName, String calleeMethodName) {
    for (MethodCallFdo methodCall : findMethodByName(source, calleeMethodName).getMethodCalls()) {
      if (StringUtils.contains(methodCall.getQualifiedSignature(), calleeMethodName)) {
        return methodCall;
      }
    }
    return null;
  }
}
