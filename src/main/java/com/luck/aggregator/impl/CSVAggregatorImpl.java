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

import com.google.common.util.concurrent.AtomicLongMap;
import com.luck.aggregator.Aggregator;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class CSVAggregatorImpl implements Aggregator {
    private static final Logger logger = LoggerFactory.getLogger(CSVAggregatorImpl.class);

    private static final String MIME_TYPE = "application/csv";

    public CSVAggregatorImpl() {

    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }


    @Override
    public Map<String, Long> aggregate(Reader sourceReader, String keyColumn, String valueColumn) throws IOException {
        AtomicLongMap<String> resultMap = AtomicLongMap.create();

        AggregatorContext context = new AggregatorContext(resultMap, keyColumn, valueColumn);
        CsvParser parser = createParser(context);
        List<String[]> allRows = parser.parseAll(sourceReader);

        return resultMap.asMap();
    }

    private CsvParser createParser(AggregatorContext context ) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setLineSeparatorDetectionEnabled(true);
        settings.setProcessor(new AggregatorRowProcessor(context));
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);

        return parser;
    }


    class AggregatorContext {
        private final AtomicLongMap<String> resultMap;
        private final String columnKey;
        private final String columnValue;

        public AggregatorContext(AtomicLongMap<String> resultMap, String columnKey, String columnValue) {
            this.resultMap = resultMap;
            this.columnKey = columnKey;
            this.columnValue = columnValue;
        }

        public AtomicLongMap<String> getResultMap() {
            return resultMap;
        }

        public String getColumnKey() {
            return columnKey;
        }

        public String getColumnValue() {
            return columnValue;
        }
    }

    class AggregatorRowProcessor implements RowProcessor {
        private final AggregatorContext aggregatorContext;
        private int columnKeyIndex;
        private int columnValueIndex;

        public AggregatorRowProcessor(AggregatorContext aggregatorContext) {
            this.aggregatorContext = aggregatorContext;
        }

        @Override
        public void processStarted(ParsingContext context) {
            this.columnKeyIndex = context.indexOf(this.aggregatorContext.getColumnKey());
            this.columnValueIndex = context.indexOf(this.aggregatorContext.getColumnValue());
        }

        @Override
        public void rowProcessed(String[] row, ParsingContext context) {
            String key = row[this.columnKeyIndex];
            Double value = Double.parseDouble(row[this.columnValueIndex]);

            if(logger.isDebugEnabled()) {
                logger.debug("Processing [" + key + "] " + value );
            }

            long mapValue = this.aggregatorContext.getResultMap()
                    .getAndAdd(row[this.columnKeyIndex],
                            Long.parseLong(row[this.columnValueIndex]));
        }

        @Override
        public void processEnded(ParsingContext context) {
            Map<String, Long> result = aggregatorContext.resultMap.asMap();

            if(logger.isDebugEnabled()) {
                logger.debug(">>>> Aggregated totals");
                result.forEach( (k,v) -> logger.debug(k + ":" + v));
            }
        }
    }
}
