// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import ballerina/grpc;
import ballerina/io;

HelloWorldBlockingClient helloWorldBlockingEp = new ({
    url:"http://localhost:9110"
});

function testUnaryBlockingClient(string name) returns (string) {
    (string, grpc:Headers)|error unionResp = helloWorldBlockingEp->hello(name);
    if (unionResp is error) {
        return "Error from Connector: " + unionResp.reason() + " - " + <string>unionResp.detail().message;
    } else {
        io:println("Client Got Response : ");
        string result;
        (result, _) = unionResp;
        io:println(result);
        return "Client got response: " + result;
    }
}

public type HelloWorldBlockingClient client object {

    private grpc:Client grpcClient = new;

    function __init(grpc:ClientEndpointConfig config) {
        // initialize client endpoint.
        grpc:Client c = new;
        c.init(config);
        error? result = c.initStub("blocking", DESCRIPTOR_KEY, getDescriptorMap());
        if (result is error) {
            panic result;
        } else {
            self.grpcClient = c;
        }
    }

    remote function hello(string req, grpc:Headers? headers = ()) returns ((string, grpc:Headers)|error) {
        var unionResp = check self.grpcClient->blockingExecute("HelloWorld/hello", req, headers = headers);
        any result;
        grpc:Headers resHeaders;
        (result, resHeaders) = unionResp;
        return (string.create(result), resHeaders);
    }
};

public type helloWorldClient client object {

    private grpc:Client grpcClient = new;

    function __init(grpc:ClientEndpointConfig config) {
        // initialize client endpoint.
        grpc:Client c = new;
        c.init(config);
        error? result = c.initStub("non-blocking", DESCRIPTOR_KEY, getDescriptorMap());
        if (result is error) {
            panic result;
        } else {
            self.grpcClient = c;
        }
    }

    remote function hello(string req, service msgListener, grpc:Headers? headers = ()) returns (error?) {
        return self.grpcClient->nonBlockingExecute("HelloWorld/hello", req, msgListener, headers = headers);
    }
};
const string DESCRIPTOR_KEY = "HelloWorld.proto";
function getDescriptorMap() returns map<any> {
    return {
        "HelloWorld.proto":
        "0A1668656C6C6F576F726C64537472696E672E70726F746F1A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F32510A0A48656C6C6F576F726C6412430A0568656C6C6F121C2E676F6F676C652E70726F746F6275662E537472696E6756616C75651A1C2E676F6F676C652E70726F746F6275662E537472696E6756616C7565620670726F746F33"
        ,
        "google/protobuf/wrappers.proto":
        "0A0E77726170706572732E70726F746F120F676F6F676C652E70726F746F62756622230A0B446F75626C6556616C756512140A0576616C7565180120012801520576616C756522220A0A466C6F617456616C756512140A0576616C7565180120012802520576616C756522220A0A496E74363456616C756512140A0576616C7565180120012803520576616C756522230A0B55496E74363456616C756512140A0576616C7565180120012804520576616C756522220A0A496E74333256616C756512140A0576616C7565180120012805520576616C756522230A0B55496E74333256616C756512140A0576616C756518012001280D520576616C756522210A09426F6F6C56616C756512140A0576616C7565180120012808520576616C756522230A0B537472696E6756616C756512140A0576616C7565180120012809520576616C756522220A0A427974657356616C756512140A0576616C756518012001280C520576616C756542570A13636F6D2E676F6F676C652E70726F746F627566420D577261707065727350726F746F50015A057479706573F80101A20203475042AA021E476F6F676C652E50726F746F6275662E57656C6C4B6E6F776E5479706573620670726F746F33"
    };
}

