package swanhack.EveryOrg;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

@RestController
public class EveryOrg {
    private final String API_KEY = "pk_live_bf2e7a1bcfdbbd8ef657be28b4ca5743";
    private final String BASE_URL = "https://partners.every.org/v0.2";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/everyorg/{search}")
    public ResponseEntity<List<Charity>> getEveryOrgInfo(@PathVariable("search") String searchQuery)
            throws IOException, InterruptedException{

//        searchQuery = searchQuery.replaceAll("\\s+", "%20");
//curl "https://partners.every.org/v0.2/search/animals?apiKey=myPublicApiKey"
        String searchQueryEncoded = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        String searchUrl = String.format("%s/search/%s?apiKey=%s", BASE_URL, searchQueryEncoded, API_KEY);
        System.out.println("URL: " + searchUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());
        JsonNode dataNode = root.path("nonprofits");

        System.out.println(response.body());

        List<Charity> charities = new ArrayList<>();

        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {

                String ein = node.path("ein").asText();
                String name = node.path("name").asText();
                String description = node.path("description").asText();
                String logoURL = node.path("logoUrl").asText();
                String location =  node.path("locationAddress").asText();
                String website = node.path("websiteUrl").asText();
                String everOrgUrl = node.path("profileUrl").asText();


                if (!ein.isEmpty() && !name.isEmpty()) {
                    charities.add(new Charity(ein, name, description, logoURL, location, website, everOrgUrl));
                }
            }
        }


        return new ResponseEntity<>(charities, HttpStatus.OK);
    }
}
