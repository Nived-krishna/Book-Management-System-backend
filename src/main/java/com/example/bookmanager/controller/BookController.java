package com.example.bookmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookmanager.dto.BookRequest;
import com.example.bookmanager.model.Book;
import com.example.bookmanager.repository.BookRepository;
import com.example.bookmanager.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {
	private final BookService bookService;
	private final BookRepository bookRepository;

	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
		this.bookRepository = null;
	}

	/**
	 * Create a new book. Server-side validation applied via @Valid.
	 */
	@PostMapping
	public ResponseEntity<Book> create(@Valid @RequestBody BookRequest request) {
		Book created = bookService.createBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	/**
	 * List books with pagination and sorting. Query params: page (0-based), size,
	 * sortBy, asc (true/false)
	 */
	@GetMapping
	public ResponseEntity<Page<Book>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size, @RequestParam(defaultValue = "title") String sortBy,
			@RequestParam(defaultValue = "true") boolean asc) {
		Page<Book> result = bookService.getAll(page, size, sortBy, asc);
		return ResponseEntity.ok(result);
	}

	/**
	 * Get a single book by UID (B-001).
	 */
	@GetMapping("/{uid}")
	public ResponseEntity<Book> get(@PathVariable String uid) {
		Book book = bookService.getByUid(uid);
		return ResponseEntity.ok(book);
	}

	/**
	 * Delete a book by UID.
	 */
	@DeleteMapping("/{uid}")
	public ResponseEntity<Void> delete(@PathVariable String uid) {
		bookService.deleteByUid(uid);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{uid}")
	public ResponseEntity<Book> update(@PathVariable String uid, @Valid @RequestBody BookRequest request) {
		Book updated = bookService.updateBook(uid, request);
		return ResponseEntity.ok(updated);
	}

}
