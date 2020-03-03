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

package org.ballerinalang.cli.bbgen;

import org.ballerinalang.cli.bbgen.components.JClass;
import org.ballerinalang.cli.bbgen.exceptions.BBGenException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ballerinalang.cli.bbgen.components.JParameter.javaClasses;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.createDirectory;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.getClassNamesInJar;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.getModuleName;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isPublicClass;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.writeOutputFile;
import static org.ballerinalang.cli.bbgen.utils.Constants.BAL_FILE_EXTENSION;
import static org.ballerinalang.cli.bbgen.utils.Constants.BBGEN_CLASS_TEMPLATE_NAME;
import static org.ballerinalang.cli.bbgen.utils.Constants.DEFAULT_TEMPLATE_DIR;
import static org.ballerinalang.cli.bbgen.utils.Constants.JAVA_UTILS_MODULE;
import static org.ballerinalang.cli.bbgen.utils.Constants.USER_DIR;

/**
 * Class for generating Ballerina bridge code.
 */
public class BridgeCodeGenerator {

    private String jarPathString;
    private String outputPath;
    private List<String> stdClasses;
    private List<String> dependentJars;
    private List<String> packageNames;
    private String mvnDependency;
    private Path modulePath;
    private Path stdJavaModulePath;
    private static final PrintStream errStream = System.err;
    private static final PrintStream outStream = System.out;
    private static Path userDir = Paths.get(System.getProperty(USER_DIR));

    public void bindingsFromJar(String jarPath) throws BBGenException {

        this.jarPathString = jarPath;
        this.dependentJars.add(this.jarPathString);
        URLClassLoader classLoader = getClassLoader(this.dependentJars);
        String moduleName = getModuleName(jarPathString);
        if (outputPath == null) {
            modulePath = Paths.get(userDir.toString(), moduleName);
            stdJavaModulePath = Paths.get(userDir.toString(), moduleName);
        } else {
            modulePath = Paths.get(outputPath, moduleName);
            stdJavaModulePath = Paths.get(outputPath, moduleName);
        }
        List<String> classes;
        try {
            classes = getClassNamesInJar(jarPathString);
            if (packageNames != null) {
                filterClasses(packageNames, classes);
            }
            if (classes != null) {
                generateBindings(classes, classLoader, modulePath);
                generateBindings(new ArrayList<>(javaClasses), classLoader, stdJavaModulePath);
                outStream.println("Generated bindings for: " + moduleName);
            }
        } catch (IOException e) {
            errStream.println(e);
        } finally {
            try {
                assert classLoader != null;
                classLoader.close();
            } catch (IOException e) {
                errStream.println(e);
            }
        }
    }

    private void filterClasses(List<String> packages, List<String> classes) {

        boolean remove = true;
        for (String className : classes) {
            for (String packageName : packages) {
                if (className.startsWith(packageName)) {
                    remove = false;
                    break;
                }
            }
            if (remove) {
                classes.remove(className);
            }
        }
    }

    public void bindingsFromMvn(String mvnDependency) {

        this.mvnDependency = mvnDependency;
    }

    public void stdJavaBindings(List<String> stdClasses) throws BBGenException {

        this.stdClasses = stdClasses;
        if (outputPath == null) {
            stdJavaModulePath = Paths.get(userDir.toString(), JAVA_UTILS_MODULE);
        } else {
            stdJavaModulePath = Paths.get(outputPath, JAVA_UTILS_MODULE);
        }
        if (this.stdClasses != null) {
            generateBindings(this.stdClasses, this.getClass().getClassLoader(), stdJavaModulePath);
            outStream.println("Generated bindings for: " + JAVA_UTILS_MODULE);
        }
    }

    public void setOutputPath(String outputPath) {

        this.outputPath = outputPath;
    }

    private URLClassLoader getClassLoader(List<String> jarPaths) throws BBGenException {

        URLClassLoader classLoader;
        List<URL> urls = new ArrayList<>();
        try {
            for (String path : jarPaths) {
                urls.add(FileSystems.getDefault().getPath(path).toFile().toURI().toURL());
            }
            classLoader = (URLClassLoader) AccessController.doPrivileged((PrivilegedAction) ()
                    -> new URLClassLoader(urls.toArray(new URL[urls.size()])));
        } catch (MalformedURLException e) {
            throw new BBGenException("Error while processing the jar path: ", e);
        }
        return classLoader;
    }

    public void setDependentJars(String[] jarPaths) {

        Collections.addAll(this.dependentJars, jarPaths);
    }

    public void setPackageNames(String[] packageNames) {

        Collections.addAll(this.packageNames, packageNames);
    }

    public void generateBindings(List<String> classList, ClassLoader classLoader, Path modulePath)
            throws BBGenException {

        createDirectory(modulePath.toString());
        for (String c : classList) {
            try {
                if (classLoader != null) {
                    Class classInstance = classLoader.loadClass(c);
                    if (classInstance != null && isPublicClass(classInstance) && !classInstance.isEnum()) {
                        JClass jClass = new JClass(classInstance);
                        String outputFile = Paths.get(modulePath.toString(), jClass.packageName).toString();
                        createDirectory(outputFile);
                        String filePath = Paths.get(outputFile, jClass.shortClassName + BAL_FILE_EXTENSION).toString();
                        writeOutputFile(jClass, DEFAULT_TEMPLATE_DIR, BBGEN_CLASS_TEMPLATE_NAME, filePath);
                    }
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                System.err.println("Bindings for class " + c + " could not be created:\n" + e);
            } catch (BBGenException e) {
                throw new BBGenException("Error while generating Ballerina bridge code: " + e);
            }
        }
    }
}
