package org.egov.common.models.project.irs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.egov.common.models.core.EgovOfflineModel;
import org.egov.common.models.project.TaskAction;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAction extends EgovOfflineModel {

    @JsonProperty("projectId")
    @Size(min = 2, max = 64)
    @NotNull
    private String projectId;

    @JsonProperty("latitude")
    @DecimalMin("-90")
    @DecimalMax("90")
    private Double latitude;

    @JsonProperty("longitude")
    @DecimalMin("-180")
    @DecimalMax("180")
    private Double longitude;

    @JsonProperty("locationAccuracy")
    @DecimalMin("0")
    private Double locationAccuracy;

    @JsonProperty("boundaryCode")
    private String boundaryCode;

    @JsonProperty("action")
    @NotNull
    private TaskAction action;

    @JsonProperty("beneficiaryTag")
    @Size(min = 2, max = 64)
    private String beneficiaryTag;

    @JsonProperty("resourceTag")
    @Size(min = 2, max = 64)
    private String resourceTag;

    @JsonProperty("isDeleted")
    @Builder.Default
    private Boolean isDeleted = false;

}
