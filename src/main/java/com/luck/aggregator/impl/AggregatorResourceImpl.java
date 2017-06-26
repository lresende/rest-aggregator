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
import java.util.Map;
import java.util.UUID;


import com.luck.aggregator.Aggregator;
import com.luck.aggregator.AggregatorResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.oasisopen.sca.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oasisopen.sca.annotation.AllowsPassByReference;

@Path("/services/aggregator")
@AllowsPassByReference
public class AggregatorResourceImpl implements AggregatorResource {
    private static final Logger logger = LoggerFactory.getLogger(AggregatorResourceImpl.class);

    private static final boolean DEBUG = true;

    @Reference
    @AllowsPassByReference
    private Aggregator aggregatorService;

    @Override
    public Response aggregateCSV(InMultiPart multiParts,
                                 @QueryParam("separator") @DefaultValue(",") String separator,
                                 @QueryParam("keyColumn") String keyColumn,
                                 @QueryParam("valueColumn") String valueColumn) {

        logger.info("Aggregating CSV : key column(" + keyColumn + ") / value column (" + valueColumn + ")");

        if(StringUtils.isEmpty(keyColumn)) {
            throw new WebApplicationException(
                 Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Missing argument 'keyColumn'")
                    .build());
        }

        if(StringUtils.isEmpty(valueColumn)) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Missing argument 'valueColumn'")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Map<String, Long> result = null;
        try {
            while(multiParts.hasNext()) {
                InPart part = (InPart) multiParts.next();
                logger.info("Part content type: " + part.getContentType());

                InputStreamReader inputStreamReader = new InputStreamReader(part.getInputStream());
                result = aggregatorService.aggregate(inputStreamReader, keyColumn, valueColumn);


                inputStreamReader.close();
            }
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("Error processing file part: " + e.getMessage(), e);
            }
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage() + "\n")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }


        if(result == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("Internal error processing result: Result is NULL");
            }
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal error processing result: Result is NULL" + "\n")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } else {
            return processResponseMap(result);
        }
    }

    private Response processResponseMap(Map<String, Long> resultMap) {
        StringWriter writer = new StringWriter();

        resultMap.forEach( (k,v) -> writer.append(k + ":" + v).append("\n"));

        if(logger.isDebugEnabled()) {
            logger.debug(writer.toString());
        }

        return Response.ok()
                .entity(writer.toString())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    private void writeTempFile(InPart part) {

        OutputStream out = null;
        byte[] bytes = null;
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
}
