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
package com.luck.aggregator.impl;

import java.io.*;
import java.util.UUID;


import com.luck.aggregator.Resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oasisopen.sca.annotation.AllowsPassByReference;

@Path("/services/aggregator")
@AllowsPassByReference
public class ResourceImpl implements Resource {
    private static final Logger logger = LoggerFactory.getLogger(ResourceImpl.class);

    public ResourceImpl() {

    }

    @POST
    @Path("/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response aggregateCSV(InMultiPart multiParts,
                                 @QueryParam("separator") @DefaultValue(",") String separator,
                                 @QueryParam("keyColumn") String keyColumn,
                                 @QueryParam("valueColumn") String valueColumn) {

        logger.info("Aggregating CSV : key column(" + keyColumn + ") / value column (" + valueColumn + ")");

        OutputStream out = null;
        byte[] bytes = null;
        while(multiParts.hasNext()) {
            InPart part = (InPart) multiParts.next();
            logger.info("Part content type: " + part.getContentType());
            try {
                File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".csv");
                logger.info("Writting file to: " + tempFile.getAbsolutePath());

                InputStream inputStream= part.getInputStream();
                out = new FileOutputStream(tempFile);
                int read=0;
                bytes = new byte[1024];
                while((read = inputStream.read(bytes))!= -1) {
                    out.write(bytes, 0, read);
                }
                inputStream.close();
                out.flush();
                out.close();
            } catch(IOException ioe) {
                logger.debug("Error processing file part: " + ioe.getMessage(), ioe);
            }
        }

        return Response.ok().build();
    }
}
