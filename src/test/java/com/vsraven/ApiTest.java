package com.vsraven;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiTest {

    // Made using requests to avoid adding test related dependencies

    private static final String BASE = "http://localhost:7070/api/v1";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== API Tests ===\n");

        bookCrudTests();
        bookValidationTests();
        customerCrudTests();
        customerValidationTests();
        contentTypeTests();

        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
        if (failed > 0) System.exit(1);
    }

    // --- Book CRUD ---

    static void bookCrudTests() throws Exception {
        // POST - create
        var createRes = post("/books", """
                {"title": "Test Book", "publicationYear": 2024, "edition": 1, "authorName": "Test Author"}""");
        check("Book POST returns 201", createRes.statusCode() == 201);
        var book = mapper.readTree(createRes.body());
        var bookId = book.get("id").asText();
        check("Book POST returns id", !bookId.isBlank());
        check("Book POST returns title", book.get("title").asText().equals("Test Book"));

        // GET all
        var listRes = get("/books");
        check("Book GET all returns 200", listRes.statusCode() == 200);

        // GET one
        var getRes = get("/books/" + bookId);
        check("Book GET one returns 200", getRes.statusCode() == 200);
        check("Book GET one returns correct book", mapper.readTree(getRes.body()).get("id").asText().equals(bookId));

        // PATCH
        var patchRes = patch("/books/" + bookId, """
                {"title": "Updated Book"}""");
        check("Book PATCH returns 200", patchRes.statusCode() == 200);
        check("Book PATCH updates title", mapper.readTree(patchRes.body()).get("title").asText().equals("Updated Book"));

        // DELETE
        var deleteRes = delete("/books/" + bookId);
        check("Book DELETE returns 204", deleteRes.statusCode() == 204);

        // GET after delete
        var getDeletedRes = get("/books/" + bookId);
        check("Book GET deleted returns 404", getDeletedRes.statusCode() == 404);
    }

    // --- Book Validation ---

    static void bookValidationTests() throws Exception {
        // Missing required fields
        var res = post("/books", """
                {"title": "", "publicationYear": 0, "edition": 0, "authorName": ""}""");
        check("Book POST invalid returns 400", res.statusCode() == 400);
        var errors = mapper.readTree(res.body());
        check("Book validation returns errors array", errors.isArray() && errors.size() > 0);

        // PATCH non-existent
        var patchRes = patch("/books/non-existent-id", """
                {"title": "Nope"}""");
        check("Book PATCH non-existent returns 404", patchRes.statusCode() == 404);

        // DELETE non-existent
        var deleteRes = delete("/books/non-existent-id");
        check("Book DELETE non-existent returns 404", deleteRes.statusCode() == 404);
    }

    // --- Customer CRUD ---

    static void customerCrudTests() throws Exception {
        // POST
        var createRes = post("/customers", """
                {"firstName": "John", "lastName": "Doe", "email": "john@example.com", "gender": "M"}""");
        check("Customer POST returns 201", createRes.statusCode() == 201);
        var customer = mapper.readTree(createRes.body());
        var customerId = customer.get("id").asText();
        check("Customer POST returns id", !customerId.isBlank());

        // GET all
        var listRes = get("/customers");
        check("Customer GET all returns 200", listRes.statusCode() == 200);

        // GET one
        var getRes = get("/customers/" + customerId);
        check("Customer GET one returns 200", getRes.statusCode() == 200);

        // PATCH
        var patchRes = patch("/customers/" + customerId, """
                {"firstName": "Jane"}""");
        check("Customer PATCH returns 200", patchRes.statusCode() == 200);
        check("Customer PATCH updates firstName", mapper.readTree(patchRes.body()).get("firstName").asText().equals("Jane"));

        // DELETE
        var deleteRes = delete("/customers/" + customerId);
        check("Customer DELETE returns 204", deleteRes.statusCode() == 204);

        // GET after delete
        var getDeletedRes = get("/customers/" + customerId);
        check("Customer GET deleted returns 404", getDeletedRes.statusCode() == 404);
    }

    // --- Customer Validation ---

    static void customerValidationTests() throws Exception {
        // Missing required fields
        var res = post("/customers", """
                {"firstName": "", "lastName": "", "email": "", "gender": null}""");
        check("Customer POST empty fields returns 400", res.statusCode() == 400);

        // Invalid email
        var emailRes = post("/customers", """
                {"firstName": "John", "lastName": "Doe", "email": "not-an-email", "gender": "M"}""");
        check("Customer POST invalid email returns 400", emailRes.statusCode() == 400);
        var errors = mapper.readTree(emailRes.body());
        check("Customer invalid email error message", errors.toString().contains("email must be a valid email address"));

        // Invalid gender
        var genderRes = post("/customers", """
                {"firstName": "John", "lastName": "Doe", "email": "john@example.com", "gender": "X"}""");
        check("Customer POST invalid gender returns 400", genderRes.statusCode() == 400);
        var genderErrors = mapper.readTree(genderRes.body());
        check("Customer invalid gender error message", genderErrors.toString().contains("gender is required"));

        // PATCH with invalid email
        var createRes = post("/customers", """
                {"firstName": "Test", "lastName": "User", "email": "test@test.com", "gender": "F"}""");
        var id = mapper.readTree(createRes.body()).get("id").asText();
        var patchRes = patch("/customers/" + id, """
                {"email": "invalid"}""");
        check("Customer PATCH invalid email returns 400", patchRes.statusCode() == 400);

        // Cleanup
        delete("/customers/" + id);
    }

    // --- Content-Type ---

    static void contentTypeTests() throws Exception {
        // POST without Content-Type
        var req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/books"))
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"title": "Test"}"""))
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        check("POST without Content-Type returns 415", res.statusCode() == 415);

        // POST with wrong Content-Type
        var req2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/books"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"title": "Test"}"""))
                .build();
        var res2 = client.send(req2, HttpResponse.BodyHandlers.ofString());
        check("POST with text/plain returns 415", res2.statusCode() == 415);
    }

    // --- Helpers ---

    static HttpResponse<String> get(String path) throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(BASE + path)).GET().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    static HttpResponse<String> post(String path, String json) throws Exception {
        var req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    static HttpResponse<String> patch(String path, String json) throws Exception {
        var req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    static HttpResponse<String> delete(String path) throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(BASE + path)).DELETE().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name);
            failed++;
        }
    }
}
