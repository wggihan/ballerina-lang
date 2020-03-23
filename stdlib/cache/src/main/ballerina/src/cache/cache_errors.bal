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

import ballerina/log;

# Record type to hold the details of an error.
#
# + message - Specific error message of the error.
# + cause - Any other error, which causes this error.
public type Detail record {
    string message;
    error cause?;
};

# Represents the reason for the Cache error.
public const CACHE_ERROR = "{ballerina/cache}Error";

# Represents the Cache error type with details. This will be returned if an error occurred while doing the cache
# operations.
public type Error error<CACHE_ERROR, Detail>;

# Log and prepare the `error` as an `Error`.
#
# + message - Error message
# + err - `error` instance
# + return - Prepared `Error` instance
function prepareError(string message, error? err = ()) returns Error {
    log:printError(message, err);
    Error cacheError;
    if (err is error) {
        cacheError = error(CACHE_ERROR, message = message, cause = err);
    } else {
        cacheError = error(CACHE_ERROR, message = message);
    }
    return cacheError;
}

# Prepare the `error` as an `Error`.
#
# + message - Error message
# + err - `error` instance
# + return - Prepared `Error` instance
function prepareErrorWithDebugLog(string message, error? err = ()) returns Error {
    //log:printDebug(message);
    Error cacheError;
    if (err is error) {
        cacheError = error(CACHE_ERROR, message = message, cause = err);
    } else {
        cacheError = error(CACHE_ERROR, message = message);
    }
    return cacheError;
}
