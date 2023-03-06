package org.egov.transformer.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.transformer.config.TransformerProperties;
import org.egov.transformer.enums.Operation;
import org.egov.transformer.models.downstream.ProjectTaskCreateIndexV1;
import org.egov.transformer.models.upstream.Task;
import org.egov.transformer.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProjectTaskCreateIndexV1TransformationService implements TransformationService<Task> {

    private final ProjectTaskCreateIndexV1Transformer transformer;

    private final Producer producer;

    private final TransformerProperties properties;



    @Autowired
    public ProjectTaskCreateIndexV1TransformationService(ProjectTaskCreateIndexV1Transformer transformer,
                                                         Producer producer, TransformerProperties properties) {
        this.transformer = transformer;
        this.producer = producer;
        this.properties = properties;
    }

    @Override
    public void transform(List<Task> payloadList) {
        log.info("transforming for ids {}", payloadList.stream()
                .map(Task::getId).collect(Collectors.toList()));
        List<ProjectTaskCreateIndexV1> transformedPayloadList = payloadList.stream()
                .map(transformer::transform)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("transformation successful");
        producer.push(properties.getTransformerProducerBulkCreateProjectTaskIndexV1Topic(),
                transformedPayloadList);
    }

    @Override
    public Operation getOperation() {
        return Operation.CREATE;
    }

    @Component
    static class ProjectTaskCreateIndexV1Transformer implements
            Transformer<Task, ProjectTaskCreateIndexV1> {
        private final ProjectService projectService;

        @Autowired
        ProjectTaskCreateIndexV1Transformer(ProjectService projectService) {
            this.projectService = projectService;
        }

        @Override
        public List<ProjectTaskCreateIndexV1> transform(Task task) {
            Map<String, String> boundaryLabelToNameMap = projectService
                    .getBoundaryLabelToNameMap(task.getProjectId(), task.getTenantId());
            log.info("boundary labels {}", boundaryLabelToNameMap.toString());
            return task.getResources().stream().map(r ->
                    ProjectTaskCreateIndexV1.builder()
                            .id(task.getId())
                            .taskType("DELIVERY")
                            .userId(task.getAuditDetails().getCreatedBy())
                            .projectId(task.getProjectId())
                            .startDate(task.getActualStartDate())
                            .endDate(task.getActualEndDate())
                            .productVariant(r.getProductVariantId())
                            .isDelivered(r.getIsDelivered())
                            .quantity(r.getQuantity())
                            .deliveredTo("HOUSEHOLD")
                            .deliveryComments(r.getDeliveryComment())
                            .province(boundaryLabelToNameMap.get("Province"))
                            .district(boundaryLabelToNameMap.get("District"))
                            .administrativeProvince(boundaryLabelToNameMap.get("AdministrativeProvince"))
                            .locality(boundaryLabelToNameMap.get("Locality"))
                            .village(boundaryLabelToNameMap.get("Village"))
                            .latitude(task.getAddress().getLatitude())
                            .longitude(task.getAddress().getLongitude())
                            .createdTime(task.getAuditDetails().getCreatedTime())
                            .build()
            ).collect(Collectors.toList());
        }
    }
}