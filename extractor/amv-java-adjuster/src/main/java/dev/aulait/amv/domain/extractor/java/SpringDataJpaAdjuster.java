package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.CrudPointFdo;
import dev.aulait.amv.domain.extractor.fdo.InheritedTypeFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SpringDataJpaAdjuster implements MetadataAdjuster {

  @Override
  public void adjust(TypeFdo type) {

    for (InheritedTypeFdo extType : type.getExtendedTypes()) {
      if (StringUtils.startsWith(
          extType.getQualifiedName(), "org.springframework.data.repository.CrudRepository")) {

        type.getMethods().stream().forEach(this::extractReference);
        type.getMethods().addAll(extractCrudRepository(type, extType.getTypeArguments()));

      } else if (StringUtils.startsWith(
          extType.getQualifiedName(), "org.springframework.data.jpa.repository.JpaRepository")) {

        type.getMethods().stream().forEach(this::extractReference);
        type.getMethods().addAll(extractJpaRepository(type, extType.getTypeArguments()));
      }
    }
  }

  List<MethodFdo> extractCrudRepository(TypeFdo type, List<String> typeArgs) {
    List<MethodFdo> methods = new ArrayList<>();
    String entityType = typeArgs.get(0);

    MethodFdo save = new MethodFdo();
    save.setName("save");
    save.setQualifiedSignature(type.getQualifiedName() + ".save(S)");
    methods.add(save);

    save.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("C").build());
    save.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("U").build());

    // TODO: other methods

    return methods;
  }

  List<MethodFdo> extractListCrudRepository(TypeFdo type, List<String> typeArgs) {
    List<MethodFdo> methods = new ArrayList<>();
    String entityType = typeArgs.get(0);

    methods.addAll(extractCrudRepository(type, typeArgs));

    MethodFdo save = new MethodFdo();
    save.setName("saveAll");
    save.setQualifiedSignature(type.getQualifiedName() + ".saveAll(java.lang.Iterable)");
    methods.add(save);

    save.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("C").build());
    save.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("U").build());

    // TODO: other methods

    return methods;
  }

  List<MethodFdo> extractJpaRepository(TypeFdo type, List<String> typeArgs) {
    List<MethodFdo> methods = new ArrayList<>();
    String entityType = typeArgs.get(0);

    methods.addAll(extractListCrudRepository(type, typeArgs));

    MethodFdo saveAndFlush = new MethodFdo();
    saveAndFlush.setName("saveAndFlush");
    saveAndFlush.setQualifiedSignature(type.getQualifiedName() + ".saveAndFlush(S)");
    methods.add(saveAndFlush);

    saveAndFlush.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("C").build());
    saveAndFlush.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("U").build());

    // TODO: other methods

    return methods;
  }

  void extractReference(MethodFdo method) {

    if (StringUtils.isEmpty(method.getQualifiedSignature())) {
      return;
    }

    String returnType = method.getReturnType();

    if (returnType == null || "void".equals(returnType)) {
      return;
    }

    int index = StringUtils.ordinalIndexOf(method.getQualifiedSignature(), ".", 3);
    String namespaceStart = method.getQualifiedSignature().substring(0, index);

    if (!returnType.contains(namespaceStart)) {
      return;
    }

    if (returnType.startsWith(namespaceStart)) {
      method.getCrudPoints().add(CrudPointFdo.builder().type(returnType).crud("R").build());
      return;
    }

    String entityType = StringUtils.substringBetween(returnType, "<" + namespaceStart, ">");

    if (entityType == null) {
      return;
    }

    entityType = namespaceStart + entityType;
    method.getCrudPoints().add(CrudPointFdo.builder().type(entityType).crud("R").build());
  }
}
