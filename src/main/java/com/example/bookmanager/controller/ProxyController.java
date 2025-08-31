package com.example.bookmanager.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ProxyController - Forwards ISBN queries to Google Books API. - Returns a
 * compact JSON: { description, coverUrl, infoLink, raw } - Strips HTML tags
 * from description for safer display. - Uses short timeouts and basic error
 * handling.
 */
@RestController
@RequestMapping("/api/proxy")
@CrossOrigin(origins = "*")
public class ProxyController {

	@Value("${google.books.api.key:}")
	private String googleApiKey;

	private RestTemplate rest;

	public ProxyController() {
		// basic timeouts to avoid blocking forever
		SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
		rf.setConnectTimeout(3_000);
		rf.setReadTimeout(5_000);
		this.rest = new RestTemplate(rf);
	}

	@GetMapping("/google-books")
	public ResponseEntity<Map<String, Object>> googleBooks(@RequestParam String isbn) {
		// basic validation
		if (isbn == null || isbn.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "isbn parameter is required"));
		}

		try {
			String query = "isbn:" + isbn.trim();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl("https://www.googleapis.com/books/v1/volumes").queryParam("q", query)
					.queryParam("maxResults", 1);

			if (googleApiKey != null && !googleApiKey.isBlank()) {
				uriBuilder.queryParam("key", googleApiKey);
			}

			String url = uriBuilder.toUriString();

			ResponseEntity<Map> resp = rest.getForEntity(url, Map.class);
			if (resp.getStatusCode() != HttpStatus.OK || resp.getBody() == null) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
						.body(Map.of("error", "Failed to fetch from Google Books"));
			}

			Map body = resp.getBody();
			List items = (List) body.get("items");
			if (items == null || items.isEmpty()) {
				// return empty extras (valid 200)
				return ResponseEntity.ok(Map.of("description", null, "coverUrl", null, "infoLink", null, "raw", body));
			}

			Map vol = (Map) items.get(0);
			Map volInfo = (Map) vol.get("volumeInfo");

			String description = null;
			String infoLink = null;
			String coverUrl = null;

			if (volInfo != null) {
				Object d = volInfo.get("description");
				if (d != null) {
					// strip HTML tags to keep frontend display safe
					description = stripHtml(d.toString()).trim();
				}

				Object il = volInfo.get("infoLink");
				if (il != null)
					infoLink = il.toString();

				Object imgObj = volInfo.get("imageLinks");
				if (imgObj instanceof Map) {
					Map imageLinks = (Map) imgObj;
					// prefer largest available
					if (imageLinks.get("extraLarge") != null)
						coverUrl = imageLinks.get("extraLarge").toString();
					else if (imageLinks.get("large") != null)
						coverUrl = imageLinks.get("large").toString();
					else if (imageLinks.get("medium") != null)
						coverUrl = imageLinks.get("medium").toString();
					else if (imageLinks.get("thumbnail") != null)
						coverUrl = imageLinks.get("thumbnail").toString();
					else if (imageLinks.get("smallThumbnail") != null)
						coverUrl = imageLinks.get("smallThumbnail").toString();
				}
			}

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("description", (description == null || description.isEmpty()) ? null : description);
			result.put("coverUrl", coverUrl);
			result.put("infoLink", infoLink);
			result.put("raw", body);

			return ResponseEntity.ok(result);

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Exception while fetching Google Books: " + ex.getMessage()));
		}
	}

	// Very small helper to remove HTML tags
	private String stripHtml(String s) {
		if (s == null)
			return null;
		// remove tags and normalize whitespace
		String without = s.replaceAll("(?s)<[^>]*>", " ");
		return without.replaceAll("\\s{2,}", " ").trim();
	}
}
