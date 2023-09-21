package org.egov.adrm.web.controllers;

import io.swagger.annotations.ApiParam;
import org.egov.adrm.config.AdrmConfiguration;
import org.egov.adrm.service.AdverseEventService;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.common.models.adrm.adverseevent.AdverseEvent;
import org.egov.common.models.adrm.adverseevent.AdverseEventBulkRequest;
import org.egov.common.models.adrm.adverseevent.AdverseEventBulkResponse;
import org.egov.common.models.adrm.adverseevent.AdverseEventRequest;
import org.egov.common.models.adrm.adverseevent.AdverseEventResponse;
import org.egov.common.models.adrm.adverseevent.AdverseEventSearchRequest;
import org.egov.common.producer.Producer;
import org.egov.common.utils.ResponseInfoFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping("/adverse_event")
@Validated
public class AdverseEventApiController {

    private final HttpServletRequest httpServletRequest;

    private final AdverseEventService adverseEventService;

    private final Producer producer;

    private final AdrmConfiguration adrmConfiguration;

    public AdverseEventApiController(
            HttpServletRequest httpServletRequest,
            AdverseEventService adverseEventService,
            Producer producer,
            AdrmConfiguration adrmConfiguration
    ) {
        this.httpServletRequest = httpServletRequest;
        this.adverseEventService = adverseEventService;
        this.producer = producer;
        this.adrmConfiguration = adrmConfiguration;
    }

    @RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<AdverseEventResponse> adverseEventV1CreatePost(@ApiParam(value = "Capture details of Adverse Event", required = true) @Valid @RequestBody AdverseEventRequest request) {

        AdverseEvent adverseEvent = adverseEventService.create(request);
        AdverseEventResponse response = AdverseEventResponse.builder()
                .adverseEvent(adverseEvent)
                .responseInfo(ResponseInfoFactory
                        .createResponseInfo(request.getRequestInfo(), true))
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }



    @RequestMapping(value = "/v1/bulk/_create", method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> adverseEventBulkV1CreatePost(@ApiParam(value = "Capture details of Adverse Event", required = true) @Valid @RequestBody AdverseEventBulkRequest request) {
        request.getRequestInfo().setApiId(httpServletRequest.getRequestURI());
        adverseEventService.putInCache(request.getAdverseEvents());
        producer.push(adrmConfiguration.getCreateAdverseEventBulkTopic(), request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true));
    }

    @RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<AdverseEventBulkResponse> adverseEventV1SearchPost(@ApiParam(value = "Adverse Event Search.", required = true) @Valid @RequestBody AdverseEventSearchRequest request,
                                                                             @NotNull @Min(0) @Max(1000) @ApiParam(value = "Pagination - limit records in response", required = true) @Valid @RequestParam(value = "limit", required = true) Integer limit,
                                                                             @NotNull @Min(0) @ApiParam(value = "Pagination - offset from which records should be returned in response", required = true) @Valid @RequestParam(value = "offset", required = true) Integer offset,
                                                                             @NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,
                                                                             @ApiParam(value = "epoch of the time since when the changes on the object should be picked up. Search results from this parameter should include both newly created objects since this time as well as any modified objects since this time. This criterion is included to help polling clients to get the changes in system since a last time they synchronized with the platform. ") @Valid @RequestParam(value = "lastChangedSince", required = false) Long lastChangedSince,
                                                                             @ApiParam(value = "Used in search APIs to specify if (soft) deleted records should be included in search results.", defaultValue = "false") @Valid @RequestParam(value = "includeDeleted", required = false, defaultValue = "false") Boolean includeDeleted) throws Exception {

        List<AdverseEvent> adverseEvents = adverseEventService.search(request, limit, offset, tenantId, lastChangedSince, includeDeleted);
        AdverseEventBulkResponse response = AdverseEventBulkResponse.builder().responseInfo(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true)).adverseEvents(adverseEvents).build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<AdverseEventResponse> adverseEventV1UpdatePost(@ApiParam(value = "Capture details of Existing adverse event", required = true) @Valid @RequestBody AdverseEventRequest request) {
        AdverseEvent adverseEvent = adverseEventService.update(request);

        AdverseEventResponse response = AdverseEventResponse.builder()
                .adverseEvent(adverseEvent)
                .responseInfo(ResponseInfoFactory
                        .createResponseInfo(request.getRequestInfo(), true))
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

    }

    @RequestMapping(value = "/v1/bulk/_update", method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> adverseEventV1BulkUpdatePost(@ApiParam(value = "Capture details of Existing adverse event", required = true) @Valid @RequestBody AdverseEventBulkRequest request) {
        request.getRequestInfo().setApiId(httpServletRequest.getRequestURI());
        producer.push(adrmConfiguration.getUpdateAdverseEventBulkTopic(), request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true));
    }

    @RequestMapping(value = "/v1/_delete", method = RequestMethod.POST)
    public ResponseEntity<AdverseEventResponse> adverseEventV1DeletePost(@ApiParam(value = "Capture details of Existing adverse event", required = true) @Valid @RequestBody AdverseEventRequest request) {
        AdverseEvent adverseEvent = adverseEventService.delete(request);

        AdverseEventResponse response = AdverseEventResponse.builder()
                .adverseEvent(adverseEvent)
                .responseInfo(ResponseInfoFactory
                        .createResponseInfo(request.getRequestInfo(), true))
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

    }

    @RequestMapping(value = "/v1/bulk/_delete", method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> adverseEventV1BulkDeletePost(@ApiParam(value = "Capture details of Existing adverse event", required = true) @Valid @RequestBody AdverseEventBulkRequest request) {
        request.getRequestInfo().setApiId(httpServletRequest.getRequestURI());
        producer.push(adrmConfiguration.getDeleteAdverseEventBulkTopic(), request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true));
    }
}
