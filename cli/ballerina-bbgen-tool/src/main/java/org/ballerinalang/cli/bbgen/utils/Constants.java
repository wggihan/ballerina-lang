/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.cli.bbgen.utils;

/**
 * Class for storing constants related to BBGen tool.
 */
public class Constants {

    public static final String ACCESS_FIELD = "access";
    public static final String ACCESS_FIELD_INTEROP_TYPE = "@java:FieldGet";
    public static final String BAL_FILE_EXTENSION = ".bal";
    public static final String BBGEN_CLASS_TEMPLATE_NAME = "bridge_class";
    public static final String COMPONENT_IDENTIFIER = "bbgen";
    public static final String CONSTRUCTOR_INTEROP_TYPE = "@java:Constructor";
    public static final String METHOD_INTEROP_TYPE = "@java:Method";
    public static final String MUTATE_FIELD = "mutate";
    public static final String MUTATE_FIELD_INTEROP_TYPE = "@java:FieldSet";
    static final String MUSTACHE_FILE_EXTENSION = ".mustache";
    static final String TEMPLATES_DIR_PATH_KEY = "templates.dir.path";
    public static final String DEFAULT_TEMPLATE_DIR = "/templates";
    public static final String USER_DIR = "user.dir";
    public static final String JAVA_UTILS_MODULE = "java_utils";
    public static String[] BALLERINA_RESERVED_WORDS = {"import", "as", "public", "private", "external", "final",
            "service", "resource", "function", "object", "record", "annotation", "parameter", "transformer",
            "worker", "listener", "remote", "xmlns", "returns", "version", "channel", "abstract", "client", "const",
            "typeof", "source", "on", "int", "byte", "float", "decimal", "boolean", "string", "error", "map", "json",
            "xml", "table", "stream", "any", "typedesc", "type", "future", "anydata", "handle", "var", "new", "__init",
            "if", "match", "else", "foreach", "while", "continue", "break", "fork", "join", "some", "all", "try",
            "catch", "finally", "throw", "panic", "trap", "return", "transaction", "abort", "retry", "onretry",
            "retries", "committed", "aborted", "with", "in", "lock", "untaint", "start", "but", "check", "checkpanic",
            "primarykey", "is", "flush", "wait", "default", "from", "select", "where"};
}
