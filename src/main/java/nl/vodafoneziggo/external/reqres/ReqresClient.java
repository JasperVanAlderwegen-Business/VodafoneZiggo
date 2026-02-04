package nl.vodafoneziggo.external.reqres;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class ReqresClient {
    @Getter
    private final RestTemplate restTemplate;
    private final String BASE_URL;
    private final String apiKey;

    @Autowired
    public ReqresClient(
            @Value("${reqres.api.url}") String baseUrl,
            @Value("${reqres.api.key:}") String apiKey
    ) {
        this(new RestTemplate(), baseUrl, apiKey);
    }

    /**
     * Constructs a new instance of {@code ReqresClient} for interacting with the Reqres API. This constructor exists mainly for testing purposes
     *
     * @param restTemplate the RestTemplate instance used for making HTTP requests.
     * @param baseUrl      the base URL of the Reqres API.
     * @param apiKey       the API key to authenticate requests (nullable or optional if API does not require authentication).
     */
    public ReqresClient(RestTemplate restTemplate, String baseUrl, String apiKey) {
        this.restTemplate = restTemplate;
        this.BASE_URL = baseUrl;
        this.apiKey = apiKey;
    }

    public Optional<ReqresUser> findUserByEmail(String email) {
        int page = 1;

        while (true) {
            ReqresUsersResponse response = fetchPage(page);

            if (response != null && response.getData() != null) {
                Optional<ReqresUser> found = response.getData().stream()
                        .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                        .findFirst();

                if (found.isPresent()) {
                    return found;
                }
            }

            Integer totalPages = response != null ? response.getTotalPages() : null;
            if (totalPages == null || page >= totalPages) {
                return Optional.empty();
            }

            page++;
        }
    }

    private ReqresUsersResponse fetchPage(int page) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey); // set to your key via config

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<ReqresUsersResponse> response = restTemplate.exchange(
                    BASE_URL + "?page={page}",
                    HttpMethod.GET,
                    entity,
                    ReqresUsersResponse.class,
                    page
            );

            return response.getBody();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(502),
                    "Failed to validate email against external service",
                    ex
            );
        }
    }
}
