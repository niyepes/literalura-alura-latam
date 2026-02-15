package com.alura.literalura.client;

import com.alura.literalura.client.dto.GutendexLibroDTO;
import com.alura.literalura.client.dto.GutendexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Component
public class GutendexClient {

    private static final String BASE = "https://gutendex.com/books";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GutendexClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Realiza una búsqueda por título y retorna el primer resultado si existe.
     */
    public Optional<GutendexLibroDTO> searchFirstByTitle(String title) {
        try {
            String q = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String uri = BASE + "/?search=" + q;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                GutendexResponse gr = objectMapper.readValue(response.body(), GutendexResponse.class);
                if (gr != null && gr.results != null && !gr.results.isEmpty()) {
                    return Optional.of(gr.results.get(0));
                }
            } else {
                System.err.println("Gutendex API returned status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error calling Gutendex API: " + e.getMessage());
        }
        return Optional.empty();
    }
}
