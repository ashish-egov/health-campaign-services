package org.egov.common.models.referralmanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.models.core.EgovOfflineSearchModel;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralSearch extends EgovOfflineSearchModel {

    @JsonProperty("projectBeneficiaryId")
    private List<String> projectBeneficiaryId;

    @JsonProperty("projectBeneficiaryClientReferenceId")
    private List<String> projectBeneficiaryClientReferenceId;

    @JsonProperty("sideEffectId")
    private List<String> sideEffectId;

    @JsonProperty("sideEffectClientReferenceId")
    private List<String> sideEffectClientReferenceId;

    @JsonProperty("referrerId")
    private List<String> referrerId;

    @JsonProperty("recipientId")
    private List<String> recipientId;
}
