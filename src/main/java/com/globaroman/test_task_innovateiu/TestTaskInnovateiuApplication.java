package com.globaroman.test_task_innovateiu;

import com.globaroman.test_task_innovateiu.service.DocumentManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class TestTaskInnovateiuApplication {

    public static void main(String[] args) {

        SpringApplication.run(TestTaskInnovateiuApplication.class, args);

        DocumentManager documentManager = new DocumentManager();
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .name("Green")
                .id("1").build();
        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .name("Black")
                .id("2").build();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Function")
                .author(author1)
                .content("adventures in the galaxy")
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Poem")
                .author(author1)
                .content("creation of famous authors")
                .created(LocalDate.parse("2022-10-10")
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant())
                .build();

        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Biography")
                .author(author2)
                .content("Life of famous women")
                .created(LocalDate.parse("2020-11-10")
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant())
                .build();

        DocumentManager.Document docSave = documentManager.save(doc1);
        DocumentManager.Document docSave2 = documentManager.save(doc2);
        DocumentManager.Document docSave3 = documentManager.save(doc3);
        List<String> authorIds = new ArrayList<>();
        authorIds.add(author1.getId());
        authorIds.add(author2.getId());


//        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
//                .authorIds(authorIds).build();
        DocumentManager.SearchRequest request = null;
        List<DocumentManager.Document> search = documentManager.search(request);

        search.forEach(System.out::println);



    }

}
