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

package org.ballerinalang.cli.bbgen.components;

import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.balType;

/**
 * Class for storing specific parameter details of a Java method used for Ballerina bridge code generation.
 */
public class JParameter {

    private String type;
    private String shortTypeName;
    private String fieldName;
    private boolean notLast = true;
    public static final Set<String> javaClasses = new HashSet<>();

    JParameter(Parameter p) {

        Class paramType = p.getType();
        this.type = paramType.getName();
        if (paramType.getClassLoader() == "".getClass().getClassLoader() && !paramType.isPrimitive()) {
            if (paramType.isArray()) {
                if (!paramType.getComponentType().isPrimitive()) {
                    javaClasses.add(paramType.getComponentType().getName());
                }
            } else {
                javaClasses.add(paramType.getCanonicalName());
            }
        }
        this.shortTypeName = balType(p.getType().getSimpleName());
        this.fieldName = p.getName();
    }

    void setLastParam() {

        this.notLast = false;
    }
}
