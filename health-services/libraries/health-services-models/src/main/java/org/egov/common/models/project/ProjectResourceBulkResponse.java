package org.egov.common.models.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
* ProjectResourceResponse
*/
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-12-02T17:32:25.406+05:30")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResourceBulkResponse {

    @JsonProperty("ResponseInfo")
    @NotNull
    @Valid
    private ResponseInfo responseInfo = null;

    @JsonProperty("ProjectResources")
    @NotNull
    @Valid
    private List<ProjectResource> projectResource = new ArrayList<>();

    public ProjectResourceBulkResponse addProjectResourceItem(ProjectResource projectResourceItem) {
        this.projectResource.add(projectResourceItem);
        return this;
    }
}

