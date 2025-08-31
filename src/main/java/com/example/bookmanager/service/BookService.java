package com.example.bookmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bookmanager.dto.BookRequest;
import com.example.bookmanager.exception.ResourceNotFoundException;
import com.example.bookmanager.model.Book;
import com.example.bookmanager.repository.BookRepository;

@Service
public class BookService {
	private final BookRepository bookRepository;
	private final SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	public BookService(BookRepository bookRepository, SequenceGeneratorService sequenceGeneratorService) {
		this.bookRepository = bookRepository;
		this.sequenceGeneratorService = sequenceGeneratorService;
	}

	public Book createBook(BookRequest req) {
		// server-side uniqueness check for ISBN
		if (bookRepository.existsByIsbn(req.getIsbn())) {
			throw new IllegalArgumentException("ISBN already exists");
		}

		Book book = new Book();
		book.setUid(sequenceGeneratorService.generateBookUid());
		book.setTitle(req.getTitle());
		book.setAuthor(req.getAuthor());
		book.setPublicationDate(req.getPublicationDate());
		book.setIsbn(req.getIsbn());
		book.setGenre(req.getGenre());
		book.setRating(req.getRating());
		book.setDescription(req.getDescription());

		return bookRepository.save(book);
	}

	public Page<Book> getAll(int page, int size, String sortBy, boolean asc) {
		Sort sort = Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
		Pageable pageable = PageRequest.of(page, size, sort);
		return bookRepository.findAll(pageable);
	}

	public Book getByUid(String uid) {
		return bookRepository.findByUid(uid)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with uid: " + uid));
	}

	public void deleteByUid(String uid) {
		Book book = getByUid(uid);
		bookRepository.delete(book);
	}

	public Book updateBook(String uid, BookRequest request) {
		Book existing = bookRepository.findByUid(uid).orElseThrow(() -> new RuntimeException("Book not found"));

		existing.setTitle(request.getTitle());
		existing.setAuthor(request.getAuthor());
		existing.setPublicationDate(request.getPublicationDate());
		existing.setIsbn(request.getIsbn());
		existing.setGenre(request.getGenre());
		existing.setRating(request.getRating());
		existing.setDescription(request.getDescription());

		return bookRepository.save(existing);
	}

}
