package com.example.bookmanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.bookmanager.model.Book;

public interface BookRepository extends MongoRepository<Book, String> {
	void deleteByUid(String uid);

	Optional<Book> findByUid(String uid);

	boolean existsByIsbn(String isbn);
}
