package dev.aulait.amv.domain.process;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class SourceFileService {

  private final EntityManager em;
  private final CrudPointRepository crudPointRepository;
  private final FieldRepository fieldRepository;
  private final MethodParamRepository methodParamRepository;

  @Transactional
  public void save(SourceFileAggregate sourceFile) {
    em.persist(sourceFile.getSourceFile());

    sourceFile.getTypes().forEach(this::saveDeclaration);
  }

  @Transactional
  public int saveDeclaration(TypeAggregate typeAggregate) {
    em.persist(typeAggregate.getType());

    fieldRepository.saveAll(typeAggregate.getType().getFields());

    typeAggregate.getMethods().forEach(this::saveDeclaration);

    return 1;
  }

  @Transactional
  public int saveDeclaration(MethodAggregate methodAggregate) {
    MethodEntity method = methodAggregate.getMethod();
    em.persist(method);

    if (method.getEntryPoint() != null) {
      em.persist(method.getEntryPoint());
    }

    methodParamRepository.saveAll(method.getMethodParams());

    var persistedFlowIds = new HashSet<String>();
    methodAggregate.getFlowStatements().forEach(fs -> saveDeclaration(fs, persistedFlowIds));

    methodAggregate.getMethodCalls().stream()
        .sorted(Comparator.comparing(mc -> mc.getId().getSeqNo()))
        .forEach(
            mc -> {
              saveDeclaration(mc.getFlowStatement(), persistedFlowIds);
              em.persist(mc);
            });
    crudPointRepository.saveAll(method.getCrudPoints());
    return 1;
  }

  private void saveDeclaration(FlowStatementEntity flow, Set<String> persistedIds) {
    if (flow == null) return;

    String id = flow.getId();
    if (id != null && !persistedIds.add(id)) {
      return;
    }
    saveDeclaration(flow.getFlowStatement(), persistedIds);
    em.persist(flow);
  }
}
