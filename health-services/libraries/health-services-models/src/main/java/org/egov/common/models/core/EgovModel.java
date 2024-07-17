package org.egov.common.models.core;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.tracer.model.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EgovModel {

    @JsonProperty("id")
    @Size(min = 2, max = 64)
    protected String id;

    @JsonProperty("tenantId")
    @NotNull
    @Size(min = 2, max = 1000)
    protected String tenantId;

    @JsonProperty("status")
    protected String status;

    @JsonProperty("source")
    protected String source;  //TODO what are the various sources and needs comments

    @JsonProperty("rowVersion")
    protected Integer rowVersion;

    @JsonProperty("applicationId") //needs comments
    protected String applicationId;

    @JsonProperty("hasErrors")
    @Builder.Default
    protected Boolean hasErrors = Boolean.FALSE; //TODO is this health specific or will this become general.

    @JsonProperty("additionalFields")
    @Valid
    protected AdditionalFields additionalFields;

    @JsonProperty("auditDetails")
    @Valid
    protected AuditDetails auditDetails;

}
