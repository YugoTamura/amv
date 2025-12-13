package dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources;

import java.util.Set;

public class BookService {

  BookRepository repository;

  public void save(BookEntity book) {
    repository.save(book);
  }

  public void saveAll(Set<BookEntity> books) {
    repository.saveAll(books);
  }
}
