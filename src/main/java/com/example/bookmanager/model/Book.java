package com.example.bookmanager.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * MongoDB document representing a book.
 */
@Document(collection = "books")
public class Book {
	@Id
	private String id; // MongoDB ObjectId

	@Indexed(unique = true)
	private String uid; // Generated e.g., B-001

	private String title;
	private String author;

	// Store date as ISO yyyy-MM-dd in JSON
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate publicationDate;

	@Indexed(unique = true)
	private String isbn; // 13-digit string

	private String genre;
	private Integer rating; // 1..5

	// Optional fields
	private String description;

	public Book() {
	}

	// Getters & setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
