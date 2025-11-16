package swanhack.EveryOrg;

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

public class EveryOrg {
    private final String API_KEY = "pk_live_bf2e7a1bcfdbbd8ef657be28b4ca5743";
    private final String BASE_URL = "https://partners.every.org/v0.2/";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("everyorg/{search}")
    public ResponseEntity<List<Charity>> getEveryOrgInfo(@PathVariable("search") String searchQuery)
            throws IOException, InterruptedException{

//        searchQuery = searchQuery.replaceAll("\\s+", "%20");

        String searchUrl = String.format("%s?search=%s?apiKey=", BASE_URL, searchQuery,API_KEY);
        System.out.println("URL: " + searchUrl);

        return null;
    }
}
