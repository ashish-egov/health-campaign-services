package org.egov.household.household.member.validators;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.Error;
import org.egov.common.validator.Validator;
import org.egov.household.web.models.HouseholdMember;
import org.egov.household.web.models.HouseholdMemberBulkRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.egov.common.utils.CommonUtils.getIdToObjMap;
import static org.egov.common.utils.CommonUtils.notHavingErrors;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.common.utils.ValidatorUtils.getErrorForUniqueEntity;

@Component
@Order(value = 2)
@Slf4j
public class UniqueEntityValidator implements Validator<HouseholdMemberBulkRequest, HouseholdMember> {

    @Override
    public Map<HouseholdMember, List<Error>> validate(HouseholdMemberBulkRequest memberBulkRequest) {
        Map<HouseholdMember, List<Error>> errorDetailsMap = new HashMap<>();
        List<HouseholdMember> householdMembers = memberBulkRequest.getHouseholdMembers()
                        .stream().filter(notHavingErrors()).collect(Collectors.toList());
        if (!householdMembers.isEmpty()) {
            Map<String, HouseholdMember> iMap = getIdToObjMap(householdMembers);
            if (iMap.keySet().size() != householdMembers.size()) {
                List<String> duplicates = iMap.keySet().stream().filter(id ->
                        householdMembers.stream()
                                .filter(householdMember -> householdMember.getId().equals(id)).count() > 1
                ).collect(Collectors.toList());
                for (String key : duplicates) {
                    Error error = getErrorForUniqueEntity();
                    populateErrorDetails(iMap.get(key), error, errorDetailsMap);
                }
            }
        }
        return errorDetailsMap;
    }
}
