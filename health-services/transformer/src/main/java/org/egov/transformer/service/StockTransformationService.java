package org.egov.transformer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.User;
import org.egov.common.models.facility.AdditionalFields;
import org.egov.common.models.facility.Facility;
import org.egov.common.models.facility.Field;
import org.egov.common.models.project.Project;
import org.egov.common.models.stock.Stock;
import org.egov.transformer.Constants;
import org.egov.transformer.config.TransformerProperties;
import org.egov.transformer.enums.Operation;
import org.egov.transformer.models.downstream.StockIndexV1;
import org.egov.transformer.producer.Producer;
import org.egov.transformer.service.transformer.Transformer;
import org.egov.transformer.utils.CommonUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.egov.transformer.Constants.DISTRICT_WAREHOUSE;
import static org.egov.transformer.Constants.FACILITY_TARGET_KEY;
import static org.egov.transformer.Constants.PROJECT;
import static org.egov.transformer.Constants.SATELLITE_WAREHOUSE;
import static org.egov.transformer.Constants.TYPE_KEY;
import static org.egov.transformer.Constants.WAREHOUSE;

@Slf4j
public abstract class StockTransformationService implements TransformationService<Stock> {
    protected final StockTransformationService.StockIndexV1Transformer transformer;

    protected final Producer producer;

    protected final TransformerProperties properties;
    protected final CommonUtils commonUtils;

    protected StockTransformationService(StockIndexV1Transformer transformer,
                                         Producer producer,
                                         TransformerProperties properties, CommonUtils commonUtils) {
        this.transformer = transformer;
        this.producer = producer;
        this.properties = properties;
        this.commonUtils = commonUtils;
    }

    @Override
    public void transform(List<Stock> payloadList) {
        log.info("transforming for ids {}", payloadList.stream()
                .map(Stock::getId).collect(Collectors.toList()));
        List<StockIndexV1> transformedPayloadList = payloadList.stream()
                .map(transformer::transform)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("transformation successful");
        producer.push(getTopic(),
                transformedPayloadList);
    }

    @Override
    public Operation getOperation() {
        return Operation.STOCK;
    }

    public abstract String getTopic();

    @Component
    static class StockIndexV1Transformer implements
            Transformer<Stock, StockIndexV1> {

        private final ProjectService projectService;

        private final FacilityService facilityService;
        private final TransformerProperties properties;
        private final CommonUtils commonUtils;
        private UserService userService;
        private final ObjectMapper objectMapper;

        StockIndexV1Transformer(ProjectService projectService, FacilityService facilityService,
                                TransformerProperties properties, CommonUtils commonUtils, UserService userService, ObjectMapper objectMapper) {
            this.projectService = projectService;
            this.facilityService = facilityService;
            this.properties = properties;
            this.commonUtils = commonUtils;
            this.userService = userService;
            this.objectMapper = objectMapper;
        }

        @Override
        public List<StockIndexV1> transform(Stock stock) {
            Map<String, String> boundaryLabelToNameMap = new HashMap<>();
            String tenantId = stock.getTenantId();
            String projectId = stock.getReferenceId();
            Project project = projectService.getProject(projectId,tenantId);
            String projectTypeId = project.getProjectTypeId();
            JsonNode mdmsBoundaryData = projectService.fetchBoundaryData(tenantId, null,projectTypeId);
            List<JsonNode> boundaryLevelVsLabel = StreamSupport
                    .stream(mdmsBoundaryData.get(Constants.BOUNDARY_HIERARCHY).spliterator(), false).collect(Collectors.toList());
            Facility facility = facilityService.findFacilityById(stock.getFacilityId(), stock.getTenantId());
            Facility transactingFacility = facilityService.findFacilityById(stock.getTransactingPartyId(), stock.getTenantId());
            if (facility.getAddress().getLocality() != null && facility.getAddress().getLocality().getCode() != null) {
                boundaryLabelToNameMap = projectService
                        .getBoundaryLabelToNameMap(facility.getAddress().getLocality().getCode(), stock.getTenantId());
            } else {
                if (stock.getReferenceIdType().equals(PROJECT)) {
                    boundaryLabelToNameMap = projectService
                            .getBoundaryLabelToNameMapByProjectId(stock.getReferenceId(), stock.getTenantId());
                }
            }
            Map<String, String> finalBoundaryLabelToNameMap = boundaryLabelToNameMap;
            String facilityLevel = facility != null ? getFacilityLevel(facility) : null;
            String transactingFacilityLevel = transactingFacility != null ? getFacilityLevel(transactingFacility) : null;
            Long facilityTarget = getFacilityTarget(facility);

            String facilityType = WAREHOUSE;
            String transactingFacilityType = WAREHOUSE;

            facilityType = getType(facilityType, facility);
            transactingFacilityType = transactingFacility != null ? getType(transactingFacilityType, transactingFacility) : transactingFacilityType;

            List<User> users = userService.getUsers(stock.getTenantId(), stock.getAuditDetails().getCreatedBy());
            String syncedTimeStamp = commonUtils.getTimeStampFromEpoch(stock.getAuditDetails().getCreatedTime());

            StockIndexV1 stockIndexV1 = StockIndexV1.builder()
                    .id(stock.getId())
                    .clientReferenceId(stock.getClientReferenceId())
                    .tenantId(stock.getTenantId())
                    .productVariant(stock.getProductVariantId())
                    .facilityId(stock.getFacilityId())
                    .facilityName(facility.getName())
                    .transactingFacilityId(stock.getTransactingPartyId())
                    .userName(userService.getUserName(users, stock.getAuditDetails().getCreatedBy()))
                    .role(userService.getStaffRole(stock.getTenantId(), users))
                    .transactingFacilityName(transactingFacility != null ? transactingFacility.getName() : stock.getTransactingPartyId())
                    .facilityType(facilityType)
                    .transactingFacilityType(transactingFacilityType)
                    .physicalCount(stock.getQuantity())
                    .eventType(stock.getTransactionType())
                    .reason(stock.getTransactionReason())
                    .eventTimeStamp(stock.getDateOfEntry() != null ?
                            stock.getDateOfEntry() : stock.getAuditDetails().getLastModifiedTime())
                    .createdTime(stock.getClientAuditDetails().getCreatedTime())
                    .dateOfEntry(stock.getDateOfEntry())
                    .createdBy(stock.getAuditDetails().getCreatedBy())
                    .lastModifiedTime(stock.getClientAuditDetails().getLastModifiedTime())
                    .lastModifiedBy(stock.getAuditDetails().getLastModifiedBy())
                    .longitude(facility.getAddress() != null ? facility.getAddress().getLongitude() : null)
                    .latitude(facility.getAddress() != null ? facility.getAddress().getLatitude() : null)
                    .additionalFields(stock.getAdditionalFields())
                    .clientAuditDetails(stock.getClientAuditDetails())
                    .syncedTimeStamp(syncedTimeStamp)
                    .syncedTime(stock.getAuditDetails().getCreatedTime())
                    .facilityLevel(facilityLevel)
                    .transactingFacilityLevel(transactingFacilityLevel)
                    .facilityTarget(facilityTarget)
                    .build();
            if (stockIndexV1.getBoundaryHierarchy() == null) {
                ObjectNode boundaryHierarchy = objectMapper.createObjectNode();
                stockIndexV1.setBoundaryHierarchy(boundaryHierarchy);
            }
            boundaryLevelVsLabel.forEach(node -> {
                if (node.get(Constants.LEVEL).asInt() > 1) {
                    stockIndexV1.getBoundaryHierarchy().put(node.get(Constants.INDEX_LABEL).asText(),finalBoundaryLabelToNameMap.get(node.get(Constants.LABEL).asText()) == null ? null : finalBoundaryLabelToNameMap.get(node.get(Constants.LABEL).asText()));
                }
            });
            return Collections.singletonList(stockIndexV1);
        }

        private String getType(String transactingFacilityType, Facility transactingFacility) {
            AdditionalFields transactingFacilityAdditionalFields = transactingFacility.getAdditionalFields();
            if (transactingFacilityAdditionalFields != null) {
                List<Field> fields = transactingFacilityAdditionalFields.getFields();
                Optional<Field> field = fields.stream().filter(field1 -> TYPE_KEY.equalsIgnoreCase(field1.getKey())).findFirst();
                if (field.isPresent() && field.get().getValue() != null) {
                    transactingFacilityType = field.get().getValue();
                }
            }
            return transactingFacilityType;
        }

        private Long getFacilityTarget(Facility facility) {
            AdditionalFields facilityAdditionalFields = facility.getAdditionalFields();
            if (facilityAdditionalFields != null) {
                List<Field> fields = facilityAdditionalFields.getFields();
                Optional<Field> field = fields.stream().filter(field1 -> FACILITY_TARGET_KEY.equalsIgnoreCase(field1.getKey())).findFirst();
                if (field.isPresent() && field.get().getValue() != null) {
                    return Long.valueOf(field.get().getValue());
                }
            }
            return null;
        }

        private String getFacilityLevel(Facility facility) {
            String facilityUsage = facility.getUsage();
            if (facilityUsage != null) {
                return WAREHOUSE.equalsIgnoreCase(facility.getUsage()) ?
                        (facility.getIsPermanent() ? DISTRICT_WAREHOUSE : SATELLITE_WAREHOUSE) : null;
            }
            return null;
        }
    }
}
