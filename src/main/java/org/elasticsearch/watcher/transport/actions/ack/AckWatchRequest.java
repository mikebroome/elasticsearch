/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.watcher.transport.actions.ack;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ValidateActions;
import org.elasticsearch.action.support.master.MasterNodeOperationRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.watcher.support.Validation;

import java.io.IOException;

/**
 * A ack watch request to ack a watch by name (id)
 */
public class AckWatchRequest extends MasterNodeOperationRequest<AckWatchRequest> {

    private static final TimeValue DEFAULT_TIMEOUT = TimeValue.timeValueSeconds(10);

    private String watchId;
    private String[] actionIds = Strings.EMPTY_ARRAY;

    public AckWatchRequest() {
        this(null);
    }

    public AckWatchRequest(String watchId, String... actionIds) {
        this.watchId = watchId;
        this.actionIds = actionIds;
        masterNodeTimeout(DEFAULT_TIMEOUT);
    }

    /**
     * @return The id of the watch to be acked
     */
    public String getWatchId() {
        return watchId;
    }

    /**
     * @param actionIds The ids of the actions to be acked
     */
    public void setActionIds(String... actionIds) {
        this.actionIds = actionIds;
    }

    /**
     * @return The ids of the actions to be acked
     */
    public String[] getActionIds() {
        return actionIds;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (watchId == null){
            validationException = ValidateActions.addValidationError("watch id is missing", validationException);
        }
        Validation.Error error = Validation.watchId(watchId);
        if (error != null) {
            validationException = ValidateActions.addValidationError(error.message(), validationException);
        }
        for (String actionId : actionIds) {
            error = Validation.actionId(actionId);
            if (error != null) {
                validationException = ValidateActions.addValidationError(error.message(), validationException);
            }
        }
        return validationException;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        watchId = in.readString();
        actionIds = in.readStringArray();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(watchId);
        out.writeStringArray(actionIds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ack [").append(watchId).append("]");
        if (actionIds.length > 0) {
            sb.append("[");
            for (int i = 0; i < actionIds.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(actionIds[i]);
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
