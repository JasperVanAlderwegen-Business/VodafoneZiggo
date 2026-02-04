package nl.vodafoneziggo.external.reqres;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReqresUsersResponse {
    private Integer page;

    @JsonProperty("total_pages")
    private Integer totalPages;
    
    private List<ReqresUser> data;
}
