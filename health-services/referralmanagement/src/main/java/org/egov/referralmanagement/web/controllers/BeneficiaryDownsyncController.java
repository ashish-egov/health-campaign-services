package org.egov.referralmanagement.web.controllers;

import javax.validation.Valid;

import org.egov.common.models.referralmanagement.beneficiarydownsync.Downsync;
import org.egov.common.models.referralmanagement.beneficiarydownsync.DownsyncRequest;
import org.egov.common.models.referralmanagement.beneficiarydownsync.DownsyncResponse;
import org.egov.common.utils.ResponseInfoFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/beneficiary-downsync")
@Validated
public class BeneficiaryDownsyncController {


    @PostMapping(value = "/v1/_get")
    public ResponseEntity<String> getBeneficaryData (@ApiParam(value = "Capture details of Side Effect", required = true) @Valid @RequestBody DownsyncRequest request) {

    	Downsync.builder().
    	downsyncCriteria(request.getDownsyncCriteria())
    	.build();
        DownsyncResponse response = DownsyncResponse.builder()
                .downsync(new Downsync())
                .responseInfo(ResponseInfoFactory
                        .createResponseInfo(request.getRequestInfo(), true))
                .build();
        
        long offset = request.getDownsyncCriteria().getOffset();
        long limit = request.getDownsyncCriteria().getLimit();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("{\n"
        		+ "  \"ResponseInfo\": {\n"
        		+ "    \"apiId\": \"string\",\n"
        		+ "    \"ver\": \"string\",\n"
        		+ "    \"ts\": 0,\n"
        		+ "    \"resMsgId\": \"string\",\n"
        		+ "    \"msgId\": \"string\",\n"
        		+ "    \"status\": \"SUCCESSFUL\"\n"
        		+ "  },\n"
        		+ "  \"Downsync\": {\n"
        		+ "    \"DownsyncCriteria\": {\n"
        		+ "      \"locality\": \"string\",\n"
        		+ "      \"tenantId\": \"string\",\n"
        		+ "      \"offset\": " + offset + ",\n"
        		+ "      \"limit\": " + limit + ",\n"
        		+ "      \"lastSyncedTime\": 0,\n"
        		+ "      \"includeDeleted\": false,\n"
        		+ "      \"totalCount\": \"1\"\n"
        		+ "    },\n"
        		+ "    \"Households\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"memberCount\": 4,\n"
        		+ "        \"address\": {\n"
        		+ "          \"id\": \"string\",\n"
        		+ "          \"tenantId\": \"tenantA\",\n"
        		+ "          \"doorNo\": \"string\",\n"
        		+ "          \"latitude\": 90,\n"
        		+ "          \"longitude\": 180,\n"
        		+ "          \"locationAccuracy\": 10000,\n"
        		+ "          \"type\": \"string\",\n"
        		+ "          \"addressLine1\": \"string\",\n"
        		+ "          \"addressLine2\": \"string\",\n"
        		+ "          \"landmark\": \"string\",\n"
        		+ "          \"city\": \"string\",\n"
        		+ "          \"pincode\": \"string\",\n"
        		+ "          \"buildingName\": \"string\",\n"
        		+ "          \"street\": \"string\",\n"
        		+ "          \"locality\": {\n"
        		+ "            \"code\": \"string\",\n"
        		+ "            \"name\": \"string\",\n"
        		+ "            \"label\": \"string\",\n"
        		+ "            \"latitude\": \"string\",\n"
        		+ "            \"longitude\": \"string\",\n"
        		+ "            \"children\": [\n"
        		+ "              \"string\"\n"
        		+ "            ],\n"
        		+ "            \"materializedPath\": \"string\"\n"
        		+ "          }\n"
        		+ "        },\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"HouseholdMembers\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"householdId\": \"string\",\n"
        		+ "        \"householdClientReferenceId\": \"string\",\n"
        		+ "        \"individualId\": \"string\",\n"
        		+ "        \"individualClientReferenceId\": \"string\",\n"
        		+ "        \"isHeadOfHousehold\": false,\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"Individuals\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"userId\": \"string\",\n"
        		+ "        \"name\": {\n"
        		+ "          \"givenName\": \"string\",\n"
        		+ "          \"familyName\": \"string\",\n"
        		+ "          \"otherNames\": \"string\"\n"
        		+ "        },\n"
        		+ "        \"dateOfBirth\": \"14/10/2022\",\n"
        		+ "        \"gender\": \"MALE\",\n"
        		+ "        \"bloodGroup\": \"str\",\n"
        		+ "        \"mobileNumber\": \"string\",\n"
        		+ "        \"altContactNumber\": \"string\",\n"
        		+ "        \"email\": \"user@example.com\",\n"
        		+ "        \"address\": [\n"
        		+ "          {\n"
        		+ "            \"id\": \"string\",\n"
        		+ "            \"tenantId\": \"tenantA\",\n"
        		+ "            \"doorNo\": \"string\",\n"
        		+ "            \"latitude\": 90,\n"
        		+ "            \"longitude\": 180,\n"
        		+ "            \"locationAccuracy\": 10000,\n"
        		+ "            \"type\": \"string\",\n"
        		+ "            \"addressLine1\": \"string\",\n"
        		+ "            \"addressLine2\": \"string\",\n"
        		+ "            \"landmark\": \"string\",\n"
        		+ "            \"city\": \"string\",\n"
        		+ "            \"pincode\": \"string\",\n"
        		+ "            \"buildingName\": \"string\",\n"
        		+ "            \"street\": \"string\",\n"
        		+ "            \"locality\": {\n"
        		+ "              \"code\": \"string\",\n"
        		+ "              \"name\": \"string\",\n"
        		+ "              \"label\": \"string\",\n"
        		+ "              \"latitude\": \"string\",\n"
        		+ "              \"longitude\": \"string\",\n"
        		+ "              \"children\": [\n"
        		+ "                \"string\"\n"
        		+ "              ],\n"
        		+ "              \"materializedPath\": \"string\"\n"
        		+ "            }\n"
        		+ "          }\n"
        		+ "        ],\n"
        		+ "        \"fatherName\": \"string\",\n"
        		+ "        \"husbandName\": \"string\",\n"
        		+ "        \"identifiers\": [\n"
        		+ "          {\n"
        		+ "            \"identifierType\": \"SYSTEM_GENERATED\",\n"
        		+ "            \"identifierId\": \"ABCD-1212\"\n"
        		+ "          }\n"
        		+ "        ],\n"
        		+ "        \"skills\": [\n"
        		+ "          {\n"
        		+ "            \"id\": \"string\",\n"
        		+ "            \"type\": \"string\",\n"
        		+ "            \"level\": \"string\",\n"
        		+ "            \"experience\": \"string\"\n"
        		+ "          }\n"
        		+ "        ],\n"
        		+ "        \"photo\": \"string\",\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"ProjectBeneficiaries\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"projectId\": \"string\",\n"
        		+ "        \"beneficiaryId\": \"string\",\n"
        		+ "        \"beneficiaryClientReferenceId\": \"string\",\n"
        		+ "        \"dateOfRegistration\": 1663218161,\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"Tasks\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"projectId\": \"string\",\n"
        		+ "        \"projectBeneficiaryId\": \"R-ID-1\",\n"
        		+ "        \"projectBeneficiaryClientReferenceId\": \"R-ID-1\",\n"
        		+ "        \"resources\": [\n"
        		+ "          {\n"
        		+ "            \"id\": \"string\",\n"
        		+ "            \"tenantId\": \"tenantA\",\n"
        		+ "            \"productVariantId\": \"ID-1\",\n"
        		+ "            \"quantity\": 0,\n"
        		+ "            \"isDelivered\": true,\n"
        		+ "            \"deliveryComment\": \"string\",\n"
        		+ "            \"isDeleted\": true,\n"
        		+ "            \"auditDetails\": {\n"
        		+ "              \"createdBy\": \"string\",\n"
        		+ "              \"lastModifiedBy\": \"string\",\n"
        		+ "              \"createdTime\": 0,\n"
        		+ "              \"lastModifiedTime\": 0\n"
        		+ "            }\n"
        		+ "          }\n"
        		+ "        ],\n"
        		+ "        \"plannedStartDate\": 0,\n"
        		+ "        \"plannedEndDate\": 0,\n"
        		+ "        \"actualStartDate\": 0,\n"
        		+ "        \"actualEndDate\": 0,\n"
        		+ "        \"createdBy\": \"UUID\",\n"
        		+ "        \"createdDate\": 1663218161,\n"
        		+ "        \"address\": {\n"
        		+ "          \"id\": \"string\",\n"
        		+ "          \"tenantId\": \"tenantA\",\n"
        		+ "          \"doorNo\": \"string\",\n"
        		+ "          \"latitude\": 90,\n"
        		+ "          \"longitude\": 180,\n"
        		+ "          \"locationAccuracy\": 10000,\n"
        		+ "          \"type\": \"string\",\n"
        		+ "          \"addressLine1\": \"string\",\n"
        		+ "          \"addressLine2\": \"string\",\n"
        		+ "          \"landmark\": \"string\",\n"
        		+ "          \"city\": \"string\",\n"
        		+ "          \"pincode\": \"string\",\n"
        		+ "          \"buildingName\": \"string\",\n"
        		+ "          \"street\": \"string\",\n"
        		+ "          \"locality\": {\n"
        		+ "            \"code\": \"string\",\n"
        		+ "            \"name\": \"string\",\n"
        		+ "            \"label\": \"string\",\n"
        		+ "            \"latitude\": \"string\",\n"
        		+ "            \"longitude\": \"string\",\n"
        		+ "            \"children\": [\n"
        		+ "              \"string\"\n"
        		+ "            ],\n"
        		+ "            \"materializedPath\": \"string\"\n"
        		+ "          }\n"
        		+ "        },\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        },\n"
        		+ "        \"status\": \"DELIVERED\"\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"SideEffects\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"taskId\": \"string\",\n"
        		+ "        \"taskClientReferenceId\": \"R-ID-1\",\n"
        		+ "        \"projectBeneficiaryId\": \"string\",\n"
        		+ "        \"projectBeneficiaryClientReferenceId\": \"string\",\n"
        		+ "        \"symptoms\": [\n"
        		+ "          \"string\"\n"
        		+ "        ],\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        },\n"
        		+ "        \"clientAuditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ],\n"
        		+ "    \"Referrals\": [\n"
        		+ "      {\n"
        		+ "        \"id\": \"string\",\n"
        		+ "        \"clientReferenceId\": \"string\",\n"
        		+ "        \"tenantId\": \"tenantA\",\n"
        		+ "        \"projectBeneficiaryId\": \"string\",\n"
        		+ "        \"projectBeneficiaryClientReferenceId\": \"string\",\n"
        		+ "        \"referrerId\": \"string\",\n"
        		+ "        \"recipientId\": \"string\",\n"
        		+ "        \"recipientType\": \"string\",\n"
        		+ "        \"reasons\": [\n"
        		+ "          \"string\"\n"
        		+ "        ],\n"
        		+ "        \"sideEffect\": {\n"
        		+ "          \"id\": \"string\",\n"
        		+ "          \"clientReferenceId\": \"string\",\n"
        		+ "          \"tenantId\": \"tenantA\",\n"
        		+ "          \"taskId\": \"string\",\n"
        		+ "          \"taskClientReferenceId\": \"R-ID-1\",\n"
        		+ "          \"projectBeneficiaryId\": \"string\",\n"
        		+ "          \"projectBeneficiaryClientReferenceId\": \"string\",\n"
        		+ "          \"symptoms\": [\n"
        		+ "            \"string\"\n"
        		+ "          ],\n"
        		+ "          \"additionalFields\": {\n"
        		+ "            \"schema\": \"HOUSEHOLD\",\n"
        		+ "            \"version\": 2,\n"
        		+ "            \"fields\": [\n"
        		+ "              {\n"
        		+ "                \"key\": \"height\",\n"
        		+ "                \"value\": \"180\"\n"
        		+ "              }\n"
        		+ "            ]\n"
        		+ "          },\n"
        		+ "          \"isDeleted\": true,\n"
        		+ "          \"rowVersion\": 0,\n"
        		+ "          \"auditDetails\": {\n"
        		+ "            \"createdBy\": \"string\",\n"
        		+ "            \"lastModifiedBy\": \"string\",\n"
        		+ "            \"createdTime\": 0,\n"
        		+ "            \"lastModifiedTime\": 0\n"
        		+ "          },\n"
        		+ "          \"clientAuditDetails\": {\n"
        		+ "            \"createdBy\": \"string\",\n"
        		+ "            \"lastModifiedBy\": \"string\",\n"
        		+ "            \"createdTime\": 0,\n"
        		+ "            \"lastModifiedTime\": 0\n"
        		+ "          }\n"
        		+ "        },\n"
        		+ "        \"additionalFields\": {\n"
        		+ "          \"schema\": \"HOUSEHOLD\",\n"
        		+ "          \"version\": 2,\n"
        		+ "          \"fields\": [\n"
        		+ "            {\n"
        		+ "              \"key\": \"height\",\n"
        		+ "              \"value\": \"180\"\n"
        		+ "            }\n"
        		+ "          ]\n"
        		+ "        },\n"
        		+ "        \"isDeleted\": true,\n"
        		+ "        \"rowVersion\": 0,\n"
        		+ "        \"auditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        },\n"
        		+ "        \"clientAuditDetails\": {\n"
        		+ "          \"createdBy\": \"string\",\n"
        		+ "          \"lastModifiedBy\": \"string\",\n"
        		+ "          \"createdTime\": 0,\n"
        		+ "          \"lastModifiedTime\": 0\n"
        		+ "        }\n"
        		+ "      }\n"
        		+ "    ]\n"
        		+ "  }\n"
        		+ "}");
    }
}
