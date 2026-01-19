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

class TryCatchFlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void tryCatchFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(
            this, "FlowStatementTestResources/TryCatchFinallyFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> tryFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "trycatchStatement", FlowStatementKind.TRY.code());

    List<FlowStatementFdo> catchFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "trycatchStatement", FlowStatementKind.CATCH.code());

    assertAll(() -> assertEquals(1, tryFs.size()), () -> assertEquals(1, catchFs.size()));

    Map<Integer, FlowStatementFdo> tryByLine =
        tryFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> catchByLine =
        catchFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.TRY.code(), tryByLine.get(8).getKind()),
        () -> assertEquals("try", tryByLine.get(8).getContent()),
        () -> assertEquals(8, tryByLine.get(8).getLineNo()),
        () -> assertEquals(FlowStatementKind.CATCH.code(), catchByLine.get(10).getKind()),
        () -> assertEquals("catch (Exception e)", catchByLine.get(10).getContent()),
        () -> assertEquals(10, catchByLine.get(10).getLineNo()));
  }

  @Test
  void tryCatchNestedFlowStatementKindCodeTest() {
    Path sourceFile =
        ResourceUtils.res2path(
            this, "FlowStatementTestResources/TryCatchFinallyFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    List<FlowStatementFdo> tryFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "tryCatchNestedStatement", FlowStatementKind.TRY.code());

    List<FlowStatementFdo> catchFs =
        SourceFileDataFactory.findFlowStatementByNameKind(
            extractedSource, "tryCatchNestedStatement", FlowStatementKind.CATCH.code());

    assertAll(() -> assertEquals(2, tryFs.size()), () -> assertEquals(3, catchFs.size()));

    Map<Integer, FlowStatementFdo> tryByLine =
        tryFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    Map<Integer, FlowStatementFdo> catchByLine =
        catchFs.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    FlowStatementFdo::getLineNo, java.util.function.Function.identity()));

    assertAll(
        () -> assertEquals(FlowStatementKind.TRY.code(), tryByLine.get(16).getKind()),
        () -> assertEquals("try", tryByLine.get(16).getContent()),
        () -> assertEquals(16, tryByLine.get(16).getLineNo()),
        () -> assertEquals(FlowStatementKind.TRY.code(), tryByLine.get(19).getKind()),
        () -> assertEquals("try", tryByLine.get(19).getContent()),
        () -> assertEquals(19, tryByLine.get(19).getLineNo()),
        () -> assertEquals(FlowStatementKind.CATCH.code(), catchByLine.get(29).getKind()),
        () -> assertEquals("catch (IllegalArgumentException e)", catchByLine.get(29).getContent()),
        () -> assertEquals(29, catchByLine.get(29).getLineNo()),
        () -> assertEquals(FlowStatementKind.CATCH.code(), catchByLine.get(31).getKind()),
        () -> assertEquals("catch (IOException e)", catchByLine.get(31).getContent()),
        () -> assertEquals(31, catchByLine.get(31).getLineNo()),
        () -> assertEquals(FlowStatementKind.CATCH.code(), catchByLine.get(34).getKind()),
        () ->
            assertEquals(
                "catch (IOException | IllegalArgumentException e)",
                catchByLine.get(34).getContent()),
        () -> assertEquals(34, catchByLine.get(34).getLineNo()));
  }
}
