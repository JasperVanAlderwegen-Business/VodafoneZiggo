package nl.vodafoneziggo.external.reqres;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents the response structure returned by the Reqres API
 * when querying user data. This class encapsulates paginated information
 * about users, including the current page, total number of pages, and
 * the list of user details.
 * <p>
 * The fields in this class are mapped to their corresponding JSON properties
 * using Jackson annotations to handle cases where property names differ
 * between the API and Java conventions.
 * <p>
 * This class is primarily utilized in the context of interactions with
 * the {@code ReqresClient} for processing API responses.
 */
@Data
public class ReqresUsersResponse {
    private Integer page;

    @JsonProperty("total_pages")
    private Integer totalPages;

    private List<ReqresUser> data;
}
