// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/lang.'object;
import ballerina/java;

# Represents a ballerina task listener, which can be used to schedule and execute tasks periodically.
public type Listener object {
    *'object:Listener;
    boolean started = false;

    private TimerConfiguration|AppointmentConfiguration listenerConfiguration;

    # Initializes the `task:Listener` object. This may panic if the initialization is failed due to a configuration
    # error.
    #
    # + configurations - A `task:TimerConfiguration` or `task:AppointmentConfiguration` record to define the
    # `task:Listener` behavior
    public function __init(TimerConfiguration|AppointmentConfiguration configurations) {
        if (configurations is TimerConfiguration) {
            if (configurations["initialDelayInMillis"] == ()) {
                configurations.initialDelayInMillis = configurations.intervalInMillis;
            }
        }
        self.listenerConfiguration = configurations;
        var result = initExternal(self);
        if (result is ListenerError) {
            panic result;
        }
    }

    # Attaches the given `service` to the `task:Listener`. This may panic if the service attachment is fails.
    #
    # + s - Service to attach to the listener
    # + name - Name of the service
    # + return - Returns nil if attaching the service is successful
    public function __attach(service s, string? name = ()) returns error? {
        // ignore param 'name'
        var result = attachExternal(self, s);
        if (result is error) {
            panic result;
        }
    }

    # Detaches the given `service` from the `task:Listener`.
    #
    # + s - Service to be detached from the listener
    # + return - Returns nil if detaching the service is successful
    public function __detach(service s) returns error? {
        return detachExternal(self, s);
    }

    # Starts dispatching the services attached to the `task:Listener`. This may panic if the service dispatching causes
    # any error.
    #
    # + return - Returns nil if strating the services is successful
    public function __start() returns error? {
        var result = startExternal(self);
        if (result is error) {
            panic result;
        }
        lock {
            self.started = true;
        }
    }

    # Gracefully stops the `task:Listener` and the attached services. It will wait if there are any tasks still to be
    # completed. This may panic if the stopping causes any error.
    #
    # + return - Returns nil if stopping the listener is successful
    public function __gracefulStop() returns error? {
        var result = stopExternal(self);
        if (result is error) {
            panic result;
        }
        lock {
            self.started = false;
        }
    }

    # Stops the `task:Listener` and the attached services immediately. This will cancel any ongoing tasks. This may
    # panic if the stopping causes any error.
    #
    # + return - Returns nil if the stopping the listener is successful
    public function __immediateStop() returns error? {
        var result = stopExternal(self);
        if (result is error) {
            panic result;
        }
        lock {
            self.started = false;
        }
    }

    function isStarted() returns boolean {
        return self.started;
    }

    # Pauses the `task:Listener` and the attached services.
    #
    # + return - Returns `task:ListenerError` if an error is occurred while resuming or nil Otherwise
    public function pause() returns ListenerError? {
        return pauseExternal(self);
    }

    # Resumes a paused `task:Listener`. Calling this on an already running `task:Listener` will not cause any error.
    #
    # + return - Returns `task:ListenerError` when an error occurred while pausing or nil Otherwise
    public function resume() returns ListenerError? {
        return resumeExternal(self);
    }
};

function pauseExternal(Listener task) returns ListenerError? = @java:Method {
    name: "pause",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function resumeExternal(Listener task) returns ListenerError? = @java:Method {
    name: "resume",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function stopExternal(Listener task) returns ListenerError? = @java:Method {
    name: "stop",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function startExternal(Listener task) returns ListenerError? = @java:Method {
    name: "start",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function initExternal(Listener task) returns ListenerError? = @java:Method {
    name: "init",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function detachExternal(Listener task, service attachedService) returns ListenerError? = @java:Method {
    name: "detach",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;

function attachExternal(Listener task, service s, any... attachments) returns ListenerError? = @java:Method {
    name: "attach",
    class: "org.ballerinalang.stdlib.task.actions.TaskActions"
} external;
