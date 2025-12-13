package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFileDataFactory;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpringDataJpaAdjusterTests {
  final String SAVE_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.save(S)";

  final String SAVE_ALL_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.saveAll(java.lang.Iterable)";

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void saveDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo save = SourceFileDataFactory.findMethodByName(extractedSource, "save");

    assertEquals(SAVE_QUALIFIED_SIG, save.getQualifiedSignature());
  }

  @Test
  void saveReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveCall =
        SourceFileDataFactory.findMethodCallByName(extractedSource, "save", "save");

    assertEquals(SAVE_QUALIFIED_SIG, saveCall.getQualifiedSignature());
  }

  @Test
  void saveAllDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo saveAll = SourceFileDataFactory.findMethodByName(extractedSource, "saveAll");

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAll.getQualifiedSignature());
  }

  @Test
  void saveAllReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveAllCall =
        SourceFileDataFactory.findMethodCallByName(extractedSource, "saveAll", "saveAll");

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAllCall.getQualifiedSignature());
  }
}
