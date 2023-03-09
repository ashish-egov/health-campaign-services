package org.egov.transformer.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.transformer.Constants;
import org.egov.transformer.config.TransformerProperties;
import org.egov.transformer.enums.Operation;
import org.egov.transformer.models.downstream.StockIndexV1;
import org.egov.transformer.models.upstream.Stock;
import org.egov.transformer.producer.Producer;
import org.egov.transformer.service.transformer.Transformer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class StockTransformationService implements TransformationService<Stock>{
    protected final StockTransformationService.StockIndexV1Transformer transformer;

    protected final Producer producer;

    protected final TransformerProperties properties;

    protected StockTransformationService(StockIndexV1Transformer transformer,
                                         Producer producer,
                                         TransformerProperties properties) {
        this.transformer = transformer;
        this.producer = producer;
        this.properties = properties;
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

        StockIndexV1Transformer(ProjectService projectService) {
            this.projectService = projectService;
        }

        @Override
        public List<StockIndexV1> transform(Stock stock) {
            Map<String, String> boundaryLabelToNameMap = null;
            if (stock.getReferenceIdType().equals(Constants.PROJECT)) {
                boundaryLabelToNameMap = projectService
                        .getBoundaryLabelToNameMap(stock.getReferenceId(), stock.getTenantId());
            }
            return Collections.singletonList(StockIndexV1.builder()
                    .id(stock.getId())
                    .productVariant(stock.getProductVariantId())
                    .facilityId(stock.getFacilityId())
                    .physicalCount(stock.getQuantity())
                    .eventType(stock.getTransactionType())
                    .eventTimeStamp(stock.getAuditDetails().getLastModifiedTime())
                    //audit details
                    .createdTime(stock.getAuditDetails().getCreatedTime())
                    .createdBy(stock.getAuditDetails().getCreatedBy())
                    .lastModifiedTime(stock.getAuditDetails().getLastModifiedTime())
                    .lastModifiedBy(stock.getAuditDetails().getLastModifiedBy())
                    //longitude latitude details
                    .longitude(null)
                    .latitude(null)
                    //boundary details
                    .province(boundaryLabelToNameMap != null ? boundaryLabelToNameMap.get("Province") : null)
                    .district(boundaryLabelToNameMap != null ? boundaryLabelToNameMap.get("District") : null)
                    .administrativeProvince(boundaryLabelToNameMap != null ?
                            boundaryLabelToNameMap.get("AdministrativeProvince") : null)
                    .locality(boundaryLabelToNameMap != null ? boundaryLabelToNameMap.get("Locality") : null)
                    .village(boundaryLabelToNameMap != null ? boundaryLabelToNameMap.get("Village") : null)
                    .build());
        }
    }
}