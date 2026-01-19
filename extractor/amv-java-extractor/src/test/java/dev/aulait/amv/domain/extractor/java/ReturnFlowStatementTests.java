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

class ReturnFlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void returnFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/ReturnFlowStatement.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> returnFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "returnStatement", FlowStatementKind.RETURN.code());

    assertAll(() -> assertEquals(1, returnFs.size()));

    Map<Integer, FlowStatementFdo> returnByLine =
        returnFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.RETURN.code(), returnByLine.get(6).getKind()),
        () -> assertEquals("innerRtnMethod()", returnByLine.get(6).getContent()),
        () -> assertEquals(6, returnByLine.get(6).getLineNo()));
  }

  @Test
  void returnNestedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/ReturnFlowStatement.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> returnFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "returnNestedStatement", FlowStatementKind.RETURN.code());

    assertAll(() -> assertEquals(2, returnFs.size()));

    Map<Integer, FlowStatementFdo> returnByLine =
        returnFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.RETURN.code(), returnByLine.get(12).getKind()),
        () -> assertEquals("innerRtnMethod()", returnByLine.get(12).getContent()),
        () -> assertEquals(12, returnByLine.get(12).getLineNo()),
        () -> assertEquals(FlowStatementKind.RETURN.code(), returnByLine.get(14).getKind()),
        () -> assertEquals("defaultRtnMethod()", returnByLine.get(14).getContent()),
        () -> assertEquals(14, returnByLine.get(14).getLineNo()));
  }
}
