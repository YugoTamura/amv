package dev.aulait.amv.domain.process;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MethodAggregate {
  private MethodEntity method;
  private List<MethodCallEntity> methodCalls = new ArrayList<>();
  private List<FlowStatementEntity> flowStatements = new ArrayList<>();
}
