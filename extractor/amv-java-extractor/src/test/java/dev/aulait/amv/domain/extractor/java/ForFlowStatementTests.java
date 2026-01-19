package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFileDataFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ForFlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void forRelatedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/ForRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> forFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "forRelatedStatement", FlowStatementKind.FOR.code());

    assertAll(() -> assertEquals(2, forFs.size()));

    Map<Integer, FlowStatementFdo> forByLine =
        forFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.FOR.code(), forByLine.get(10).getKind()),
        () -> assertEquals("i <= 10", forByLine.get(10).getContent()),
        () -> assertEquals(10, forByLine.get(10).getLineNo()),
        () -> assertEquals(FlowStatementKind.FOR.code(), forByLine.get(14).getKind()),
        () -> assertEquals("Integer num : list", forByLine.get(14).getContent()),
        () -> assertEquals(14, forByLine.get(14).getLineNo()));
  }

  @Test
  void forNestedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/ForRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> forFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "forNestedFlowStatement", FlowStatementKind.FOR.code());

    assertAll(() -> assertEquals(2, forFs.size()));

    Map<Integer, FlowStatementFdo> forByLine =
        forFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.FOR.code(), forByLine.get(21).getKind()),
        () -> assertEquals("i <= 5", forByLine.get(21).getContent()),
        () -> assertEquals(21, forByLine.get(21).getLineNo()),
        () -> assertEquals(FlowStatementKind.FOR.code(), forByLine.get(22).getKind()),
        () -> assertEquals("Integer num : list", forByLine.get(22).getContent()),
        () -> assertEquals(22, forByLine.get(22).getLineNo()));
  }
}
