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
package com.luck.aggregator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.model.multipart.InMultiPart;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface AggregatorResource {

    @POST
    @Path("/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response aggregateCSV(InMultiPart multiParts,
                                 @QueryParam("separator") @DefaultValue(",") String separator,
                                 @QueryParam("keyColumn") String keyColumn,
                                 @QueryParam("valueColumn") String valueColumn);
}
