package com.globaroman.test_task_innovateiu.service;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */

public class DocumentManager {

    private final List<Document> documents = new ArrayList<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document == null) {
            throw new RuntimeException("The document cannot be empty");
        }
        if (document.getAuthor() == null) {
            throw new RuntimeException("The document must contain the Author");
        }

        if (document.getId() == null) {
            document.setId(generateUniqueId());
        }

        documents.removeIf(d -> d.getId().equals(document.getId()));

        documents.add(document);

        return findById(document.getId()).orElseThrow(() ->
                new RuntimeException("The document was not created. Try again."));
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        Set<Document> documentList = new HashSet<>();

        if (request.getAuthorIds() != null) {
            Set<Document> docByAuthor = documents.stream()
                    .filter(d -> request.getAuthorIds().contains(d.getAuthor().getId()))
                    .collect(Collectors.toSet());
            documentList.addAll(docByAuthor);
        }

        if (request.getContainsContents() != null) {
            Set<Document> docByContent = documents.stream()
                    .filter(d -> filterByContainsContents(d.getContent(), request.getContainsContents()))
                    .collect(Collectors.toSet());
            documentList.addAll(docByContent);
        }

        if (request.getTitlePrefixes() != null) {
            Set<Document> docByTitle = documents.stream()
                    .filter(d -> filterByTitlePrefixes(d.getTitle(), request.getTitlePrefixes()))
                    .collect(Collectors.toSet());
            documentList.addAll(docByTitle);
        }

        if (request.getCreatedFrom() != null && request.getCreatedTo() != null) {
            Set<Document> docByData = documents.stream()
                    .filter(d -> filterPeriodData(d.getCreated(), request.getCreatedFrom(), request.getCreatedTo()))
                    .collect(Collectors.toSet());
            documentList.addAll(docByData);
        }

        return new ArrayList<>(documentList);
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return documents.stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    private String generateUniqueId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }

    private boolean filterPeriodData(Instant created, Instant createdFrom, Instant createdTo) {
        return !created.isBefore(createdFrom) && !created.isAfter(createdTo);
    }

    private boolean filterByTitlePrefixes(String title, List<String> titlePrefixes) {
        if (title == null || titlePrefixes.isEmpty()) {
            return true;
        }

        return titlePrefixes.stream().anyMatch(title::startsWith);
    }

    private boolean filterByContainsContents(String content, List<String> containsContents) {
        if (content == null || containsContents.isEmpty()) {
            return true;
        }

        return containsContents.stream().anyMatch(c -> content.toLowerCase(Locale.ROOT)
                .contains(c.toLowerCase(Locale.ROOT)));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}