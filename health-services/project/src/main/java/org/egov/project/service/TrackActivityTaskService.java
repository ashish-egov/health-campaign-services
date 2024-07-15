package org.egov.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.egov.common.data.query.exception.QueryBuilderException;
import org.egov.common.ds.Tuple;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.ErrorDetails;
import org.egov.common.models.core.SearchResponse;
import org.egov.common.models.project.Task;
import org.egov.common.models.project.TaskBulkRequest;
import org.egov.common.models.project.TaskRequest;
import org.egov.common.models.project.TaskSearch;
import org.egov.common.models.project.irs.LocationPoint;
import org.egov.common.models.project.irs.LocationPointBulkRequest;
import org.egov.common.service.IdGenService;
import org.egov.common.utils.CommonUtils;
import org.egov.common.validator.Validator;
import org.egov.project.config.ProjectConfiguration;
import org.egov.project.repository.LocationPointRepository;
import org.egov.project.repository.TrackActivityTaskRepository;
import org.egov.project.service.enrichment.ProjectTaskEnrichmentService;
import org.egov.project.validator.task.PtExistentEntityValidator;
import org.egov.project.validator.task.PtIsDeletedValidator;
import org.egov.project.validator.task.PtNonExistentEntityValidator;
import org.egov.project.validator.task.PtNullIdValidator;
import org.egov.project.validator.task.PtProjectIdValidator;
import org.egov.project.validator.task.PtRowVersionValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import static org.egov.common.utils.CommonUtils.getIdFieldName;
import static org.egov.common.utils.CommonUtils.getIdMethod;
import static org.egov.common.utils.CommonUtils.handleErrors;
import static org.egov.common.utils.CommonUtils.havingTenantId;
import static org.egov.common.utils.CommonUtils.includeDeleted;
import static org.egov.common.utils.CommonUtils.isSearchByIdOnly;
import static org.egov.common.utils.CommonUtils.lastChangedSince;
import static org.egov.common.utils.CommonUtils.notHavingErrors;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.project.Constants.SET_TASKS;
import static org.egov.project.Constants.VALIDATION_ERROR;

@Service
@Slf4j
public class TrackActivityTaskService {

    private final IdGenService idGenService;

    private final TrackActivityTaskRepository trackActivityTaskRepository;

    private final LocationPointRepository locationPointRepository;

    private final ServiceRequestClient serviceRequestClient;

    private final ProjectConfiguration projectConfiguration;

    private final ProjectTaskEnrichmentService enrichmentService;

    private final List<Validator<TaskBulkRequest, Task>> validators;

    private final Predicate<Validator<TaskBulkRequest, Task>> isApplicableForCreate = validator ->
            validator.getClass().equals(PtProjectIdValidator.class)
                    || validator.getClass().equals(PtExistentEntityValidator.class);

    private final Predicate<Validator<TaskBulkRequest, Task>> isApplicableForUpdate = validator ->
            validator.getClass().equals(PtProjectIdValidator.class)
                    || validator.getClass().equals(PtNullIdValidator.class)
                    || validator.getClass().equals(PtIsDeletedValidator.class)
                    || validator.getClass().equals(PtNonExistentEntityValidator.class)
                    || validator.getClass().equals(PtRowVersionValidator.class);

    private final Predicate<Validator<TaskBulkRequest, Task>> isApplicableForDelete = validator ->
            validator.getClass().equals(PtNullIdValidator.class)
                    || validator.getClass().equals(PtNonExistentEntityValidator.class);

    @Autowired
    public TrackActivityTaskService(
            IdGenService idGenService,
            TrackActivityTaskRepository trackActivityTaskRepository,
            LocationPointRepository locationPointRepository,
            ServiceRequestClient serviceRequestClient,
            ProjectConfiguration projectConfiguration,
            ProjectTaskEnrichmentService enrichmentService,
            List<Validator<TaskBulkRequest, Task>> validators
    ) {
        this.idGenService = idGenService;
        this.trackActivityTaskRepository = trackActivityTaskRepository;
        this.locationPointRepository = locationPointRepository;
        this.serviceRequestClient = serviceRequestClient;
        this.projectConfiguration = projectConfiguration;
        this.enrichmentService = enrichmentService;
        this.validators = validators;
    }

    public Task create(TaskRequest request) {
        log.info("received request to create tasks");
        TaskBulkRequest bulkRequest = TaskBulkRequest.builder().requestInfo(request.getRequestInfo())
                .tasks(Collections.singletonList(request.getTask())).build();
        log.info("creating bulk request");
        List<Task> tasks = create(bulkRequest, false);
        return tasks.get(0);
    }

    public List<Task> create(TaskBulkRequest request, boolean isBulk) {
        log.info("received request to create bulk location tracking tasks");
        Tuple<List<Task>, Map<Task, ErrorDetails>> tuple = validate(validators, isApplicableForCreate, request, isBulk);
        Map<Task, ErrorDetails> errorDetailsMap = tuple.getY();
        List<Task> validTasks = tuple.getX();
        try {
            if (!validTasks.isEmpty()) {
                log.info("processing {} valid entities", validTasks.size());
                enrichmentService.create(validTasks, request);
                trackActivityTaskRepository.save(validTasks, projectConfiguration.getCreateTrackActivityTaskTopic());
                log.info("successfully created location tracking tasks");
            }
        } catch (Exception exception) {
            log.error("error occurred while creating location tracking tasks: {}", ExceptionUtils.getStackTrace(exception));
            populateErrorDetails(request, errorDetailsMap, validTasks, exception, SET_TASKS);
        }

        handleErrors(errorDetailsMap, isBulk, VALIDATION_ERROR);

        return validTasks;
    }

    public Task update(TaskRequest request) {
        log.info("received request to update location tracking tasks");
        TaskBulkRequest bulkRequest = TaskBulkRequest.builder().requestInfo(request.getRequestInfo())
                .tasks(Collections.singletonList(request.getTask())).build();
        log.info("creating bulk request");
        return update(bulkRequest, false).get(0);
    }

    public List<Task> update(TaskBulkRequest request, boolean isBulk) {
        log.info("received request to update bulk location tracking tasks");
        Tuple<List<Task>, Map<Task, ErrorDetails>> tuple = validate(validators,
                isApplicableForUpdate, request,
                isBulk);
        Map<Task, ErrorDetails> errorDetailsMap = tuple.getY();
        List<Task> validTasks = tuple.getX();
        try {
            if (!validTasks.isEmpty()) {
                log.info("processing {} valid entities", validTasks.size());
                enrichmentService.update(validTasks, request);
                trackActivityTaskRepository.save(validTasks, projectConfiguration.getUpdateTrackActivityTaskTopic());
                log.info("successfully updated bulk location tracking tasks");
            }
        } catch (Exception exception) {
            log.error("error occurred while updating location tracking tasks", ExceptionUtils.getStackTrace(exception));
            populateErrorDetails(request, errorDetailsMap, validTasks, exception, SET_TASKS);
        }

        handleErrors(errorDetailsMap, isBulk, VALIDATION_ERROR);

        return validTasks;
    }

    public Task delete(TaskRequest request) {
        log.info("received request to delete a project task");
        TaskBulkRequest bulkRequest = TaskBulkRequest.builder().requestInfo(request.getRequestInfo())
                .tasks(Collections.singletonList(request.getTask())).build();
        log.info("creating bulk request");
        return delete(bulkRequest, false).get(0);
    }

    public List<Task> delete(TaskBulkRequest request, boolean isBulk) {
        Tuple<List<Task>, Map<Task, ErrorDetails>> tuple = validate(validators,
                isApplicableForDelete, request,
                isBulk);
        Map<Task, ErrorDetails> errorDetailsMap = tuple.getY();
        List<Task> validTasks = tuple.getX();
        try {
            if (!validTasks.isEmpty()) {
                log.info("processing {} valid entities", validTasks.size());
                enrichmentService.delete(validTasks, request);
                trackActivityTaskRepository.save(validTasks, projectConfiguration.getDeleteTrackActivityTaskTopic());
            }
        } catch (Exception exception) {
            log.error("error occurred while deleting entities: {}", ExceptionUtils.getStackTrace(exception));
            populateErrorDetails(request, errorDetailsMap, validTasks, exception, SET_TASKS);
        }

        handleErrors(errorDetailsMap, isBulk, VALIDATION_ERROR);
        return validTasks;
    }

    private Tuple<List<Task>, Map<Task, ErrorDetails>> validate(List<Validator<TaskBulkRequest, Task>> validators,
                                                                Predicate<Validator<TaskBulkRequest, Task>> applicableValidators,
                                                                TaskBulkRequest request, boolean isBulk) {
        log.info("validating request");
        Map<Task, ErrorDetails> errorDetailsMap = CommonUtils.validate(validators,
                applicableValidators, request,
                SET_TASKS);
        if (!errorDetailsMap.isEmpty() && !isBulk) {
            throw new CustomException(VALIDATION_ERROR, errorDetailsMap.values().toString());
        }
        List<Task> validTasks = request.getTasks().stream()
                .filter(notHavingErrors()).collect(Collectors.toList());
        return new Tuple<>(validTasks, errorDetailsMap);
    }

    public SearchResponse<Task> search(TaskSearch taskSearch, Integer limit, Integer offset, String tenantId,
                                       Long lastChangedSince, Boolean includeDeleted) {

        log.info("received request to search project task");

        String idFieldName = getIdFieldName(taskSearch);
        if (isSearchByIdOnly(taskSearch, idFieldName)) {
            log.info("searching project task by id");
            List<String> ids = (List<String>) ReflectionUtils.invokeMethod(getIdMethod(Collections
                            .singletonList(taskSearch)),
                    taskSearch);
            log.info("fetching location tracking tasks with ids: {}", ids);
            SearchResponse<Task> searchResponse = trackActivityTaskRepository.findById(ids,
                    idFieldName, includeDeleted);
            return SearchResponse.<Task>builder().response(searchResponse.getResponse().stream()
                    .filter(lastChangedSince(lastChangedSince))
                    .filter(havingTenantId(tenantId))
                    .filter(includeDeleted(includeDeleted))
                    .collect(Collectors.toList())).totalCount(searchResponse.getTotalCount()).build();
        }

        try {
            log.info("searching project beneficiaries using criteria");
            return trackActivityTaskRepository.find(taskSearch, limit, offset,
                    tenantId, lastChangedSince, includeDeleted);
        } catch (QueryBuilderException e) {
            log.error("error in building query", ExceptionUtils.getStackTrace(e));
            throw new CustomException("ERROR_IN_QUERY", e.getMessage());
        }
    }

    public void putInCache(List<Task> tasks) {
        log.info("putting {} location tracking tasks in cache", tasks.size());
        trackActivityTaskRepository.putInCache(tasks);
        log.info("successfully put location tracking tasks in cache");
    }

    public List<LocationPoint> createLocationPoints(LocationPointBulkRequest request, boolean bulk) {
        List<LocationPoint> validEntities = request.getLocationPoints();

        try {
            if (!validEntities.isEmpty()) {
                log.info("processing {} valid entities", validEntities.size());
                locationPointRepository.save(validEntities, projectConfiguration.getCreateTrackActivityTaskLocationPointTopic());
            }
        } catch (Exception exception) {
            log.error("error occurred while deleting entities: {}", ExceptionUtils.getStackTrace(exception));
        }

        return validEntities;
    }
}