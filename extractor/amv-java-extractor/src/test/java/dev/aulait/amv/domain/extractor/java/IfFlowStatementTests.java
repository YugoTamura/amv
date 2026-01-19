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

class IfFlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void ifRelatedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    List<FlowStatementFdo> ifFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifFlowStatement", FlowStatementKind.IF.code());

    List<FlowStatementFdo> elseIfFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifFlowStatement", FlowStatementKind.ELSE_IF.code());

    List<FlowStatementFdo> elseFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifFlowStatement", FlowStatementKind.ELSE.code());

    assertAll(
        () -> assertEquals(1, ifFs.size()),
        () -> assertEquals(1, elseIfFs.size()),
        () -> assertEquals(1, elseFs.size()));

    Map<Integer, FlowStatementFdo> ifByLine =
        ifFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> elseByLine =
        elseFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    FlowStatementFdo elseIf = elseIfFs.get(0);

    assertAll(
        () -> assertEquals(FlowStatementKind.IF.code(), ifByLine.get(11).getKind()),
        () -> assertEquals("flg", ifByLine.get(11).getContent()),
        () -> assertEquals(11, ifByLine.get(11).getLineNo()),
        () -> assertEquals(FlowStatementKind.ELSE_IF.code(), elseIf.getKind()),
        () -> assertEquals("flg", elseIf.getContent()),
        () -> assertEquals(13, elseIf.getLineNo()),
        () -> assertEquals(FlowStatementKind.ELSE.code(), elseByLine.get(15).getKind()),
        () -> assertEquals("else", elseByLine.get(15).getContent()),
        () -> assertEquals(15, elseByLine.get(15).getLineNo()));
  }

  @Test
  void multipleSyntaxFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    List<FlowStatementFdo> ifFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "duplicateCaseFlowStatement", FlowStatementKind.IF.code());

    List<FlowStatementFdo> forFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "duplicateCaseFlowStatement", FlowStatementKind.FOR.code());

    assertAll(() -> assertEquals(1, ifFs.size()), () -> assertEquals(1, forFs.size()));

    Map<Integer, FlowStatementFdo> ifByLine =
        ifFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> forByLine =
        forFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.IF.code(), ifByLine.get(22).getKind()),
        () -> assertEquals("readTestList.isEmpty()", ifByLine.get(22).getContent()),
        () -> assertEquals(22, ifByLine.get(22).getLineNo()),
        () -> assertEquals(FlowStatementKind.FOR.code(), forByLine.get(28).getKind()),
        () -> assertEquals("String testVal : readTestList", forByLine.get(28).getContent()),
        () -> assertEquals(28, forByLine.get(28).getLineNo()));
  }

  @Test
  void ifNestedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(this, "FlowStatementTestResources/IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    List<FlowStatementFdo> ifFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifNestedFlowStatement", FlowStatementKind.IF.code());

    List<FlowStatementFdo> elseIfFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifNestedFlowStatement", FlowStatementKind.ELSE_IF.code());

    List<FlowStatementFdo> elseFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "ifNestedFlowStatement", FlowStatementKind.ELSE.code());

    assertAll(
        () -> assertEquals(3, ifFs.size()),
        () -> assertEquals(1, elseIfFs.size()),
        () -> assertEquals(2, elseFs.size()));

    Map<Integer, FlowStatementFdo> ifByLine =
        ifFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> elseIfByLine =
        elseIfFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> elseByLine =
        elseFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.IF.code(), ifByLine.get(35).getKind()),
        () -> assertEquals("flg", ifByLine.get(35).getContent()),
        () -> assertEquals(35, ifByLine.get(35).getLineNo()),
        () -> assertEquals(FlowStatementKind.IF.code(), ifByLine.get(37).getKind()),
        () -> assertEquals("flg", ifByLine.get(37).getContent()),
        () -> assertEquals(37, ifByLine.get(37).getLineNo()),
        () -> assertEquals(FlowStatementKind.IF.code(), ifByLine.get(38).getKind()),
        () -> assertEquals("flg", ifByLine.get(38).getContent()),
        () -> assertEquals(38, ifByLine.get(38).getLineNo()),
        () -> assertEquals(FlowStatementKind.ELSE_IF.code(), elseIfByLine.get(45).getKind()),
        () -> assertEquals("flg", elseIfByLine.get(45).getContent()),
        () -> assertEquals(45, elseIfByLine.get(45).getLineNo()),
        () -> assertEquals(FlowStatementKind.ELSE.code(), elseByLine.get(42).getKind()),
        () -> assertEquals("else", elseByLine.get(42).getContent()),
        () -> assertEquals(42, elseByLine.get(42).getLineNo()),
        () -> assertEquals(FlowStatementKind.ELSE.code(), elseByLine.get(47).getKind()),
        () -> assertEquals("else", elseByLine.get(47).getContent()),
        () -> assertEquals(47, elseByLine.get(47).getLineNo()));
  }
}
