/*
 * Copyright 2017 Luciano Resende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.luck.launcher;

import org.apache.tuscany.sca.node.Contribution;
        import org.apache.tuscany.sca.node.ContributionLocationHelper;
        import org.apache.tuscany.sca.node.Node;
        import org.apache.tuscany.sca.node.NodeFactory;

public class ApplicationLauncher {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting ...");
        String contribution = ContributionLocationHelper.getContributionLocation("application.composite");
        Node node = NodeFactory.newInstance().createNode("application.composite",
                new Contribution("file-aggregator-application", contribution));
        node.start();
        System.out.println("Aggregator Service is ready at http://localhost:8080/services/aggregator/csv !!!");
        System.in.read();
        System.out.println("Stopping ...");
        node.stop();
        System.out.println();
    }
}
