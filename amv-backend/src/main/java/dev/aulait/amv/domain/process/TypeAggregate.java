package dev.aulait.amv.domain.process;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TypeAggregate {
  private TypeEntity type;
  private List<MethodAggregate> methods = new ArrayList<>();
}
