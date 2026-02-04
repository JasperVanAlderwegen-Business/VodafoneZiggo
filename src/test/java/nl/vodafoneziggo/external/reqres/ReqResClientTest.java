package nl.vodafoneziggo.external.reqres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ReqresClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private ReqresClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        client = new ReqresClient(restTemplate, "https://reqres.in/api/users", "");
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void findUserByEmail_whenFoundOnSecondPage_returnsUser_andFetchesTwoPages() {
        // Arrange
        server.expect(requestTo("https://reqres.in/api/users?page=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "page": 1,
                          "total_pages": 2,
                          "data": [
                            { "id": 1, "email": "first@example.com", "first_name": "First", "last_name": "User" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://reqres.in/api/users?page=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "page": 2,
                          "total_pages": 2,
                          "data": [
                            { "id": 2, "email": "target@example.com", "first_name": "Target", "last_name": "User" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        // Act
        Optional<ReqresUser> result = client.findUserByEmail("TARGET@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2);
        assertThat(result.get().getEmail()).isEqualTo("target@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("Target");
        assertThat(result.get().getLastName()).isEqualTo("User");

        server.verify();
    }

    @Test
    void findUserByEmail_whenNotFound_returnsEmpty_andStopsAfterLastPage() {
        // Arrange: 2 pages, no matching email on either
        server.expect(requestTo("https://reqres.in/api/users?page=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "page": 1,
                          "total_pages": 2,
                          "data": [
                            { "id": 1, "email": "a@example.com", "first_name": "A", "last_name": "User" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://reqres.in/api/users?page=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "page": 2,
                          "total_pages": 2,
                          "data": [
                            { "id": 2, "email": "b@example.com", "first_name": "B", "last_name": "User" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        // Act
        Optional<ReqresUser> result = client.findUserByEmail("missing@example.com");

        // Assert
        assertThat(result).isEmpty();
        server.verify();
    }

    @Test
    void findUserByEmail_whenTotalPagesMissing_returnsEmpty_afterFirstPage() {
        // Arrange: total_pages is absent -> client should stop and return empty
        server.expect(requestTo("https://reqres.in/api/users?page=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "page": 1,
                          "data": [
                            { "id": 1, "email": "a@example.com", "first_name": "A", "last_name": "User" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        // Act
        Optional<ReqresUser> result = client.findUserByEmail("missing@example.com");

        // Assert
        assertThat(result).isEmpty();
        server.verify();
    }


}
