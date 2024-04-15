package org.egov.common.models.individual;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.models.core.EgovOfflineSearchModel;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* A representation of an Individual.
*/
    @ApiModel(description = "A representation of an Individual.")
@Validated


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualSearch extends EgovOfflineSearchModel {
    @JsonProperty("individualId")
    private List<String> individualId = null;

    @JsonProperty("name")
    @Valid
    private Name name = null;

    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateOfBirth = null;

    @JsonProperty("gender")
    @Valid
    private Gender gender = null;

    @JsonProperty("mobileNumber")
    private List<String> mobileNumber = null;

    @JsonProperty("socialCategory")
    private String socialCategory = null;

    @JsonProperty("wardCode")
    private String wardCode = null;

    @JsonProperty("individualName")
    private String individualName = null;

    @JsonProperty("createdFrom")
    private BigDecimal createdFrom = null;

    @JsonProperty("createdTo")
    private BigDecimal createdTo = null;

    @JsonProperty("identifier")
    @Valid
    private Identifier identifier = null;

    @JsonProperty("boundaryCode")
    private String boundaryCode = null;

    @JsonProperty("roleCodes")
    private List<String> roleCodes = null;

    @JsonProperty("username")
    private List<String> username;

    @JsonProperty("userId")
    private List<Long> userId;

    @JsonProperty("userUuid")
    @Size(min = 1)
    private List<String> userUuid;
}

