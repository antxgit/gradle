/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.launcher.daemon.client;

import org.gradle.api.specs.Spec;
import org.gradle.initialization.BuildClientMetaData;
import org.gradle.initialization.GradleLauncherAction;
import org.gradle.launcher.daemon.context.DaemonContext;
import org.gradle.launcher.daemon.protocol.Build;
import org.gradle.launcher.daemon.protocol.BuildAndStop;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.logging.internal.OutputEventListener;
import org.gradle.messaging.remote.internal.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class SingleUseDaemonClient extends DaemonClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleUseDaemonClient.class);

    public SingleUseDaemonClient(DaemonConnector connector, BuildClientMetaData clientMetaData, OutputEventListener outputEventListener, Spec<DaemonContext> compatibilitySpec, InputStream buildStandardInput) {
        super(connector, clientMetaData, outputEventListener, compatibilitySpec, buildStandardInput);
    }

    @Override
    public <T> T execute(GradleLauncherAction<T> action, BuildActionParameters parameters) {
        LOGGER.warn("Note: in order to honour the org.gradle.jvmargs and/or org.gradle.java.home values specified for this build, it is necessary to fork a new JVM.");
        LOGGER.warn("In order to avoid the slowdown associated with this extra process, you might want to consider running Gradle with the daemon enabled.");
        Build build = new BuildAndStop(action, parameters);

        DaemonConnection daemonConnection = connector.createConnection();
        Connection<Object> connection = daemonConnection.getConnection();

        return (T) executeBuild(build, connection);
    }
}
