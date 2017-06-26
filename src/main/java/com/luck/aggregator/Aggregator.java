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

import org.oasisopen.sca.annotation.Remotable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

@Remotable
public interface Aggregator {
    /**
     * Return the supported Mime Type
     * @return
     */
    String getMimeType();


    /**
     * Aggregate values from a provided value column into a provided key column value
     * @param sourceReader the data reader
     * @param keyColumn column containing the key value to use
     * @param valueColumn column containing the value to aggregate
     * @return a map containing all keys and aggregated values
     * @throws IOException
     */
    Map<String, Long> aggregate(Reader sourceReader, String keyColumn, String valueColumn) throws IOException;
}
