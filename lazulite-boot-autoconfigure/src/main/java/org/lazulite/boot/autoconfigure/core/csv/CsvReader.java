/*
 * Copyright 2016. junfu
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
 */

package org.lazulite.boot.autoconfigure.core.csv;

import au.com.bytecode.opencsv.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Created by junfu on 2016/5/6.
 */
public class CsvReader implements AutoCloseable, Iterable<String[]> {
    private final BufferedReader m_bufferedReader;
    private String m_currentLine;
    private final CSVParser m_csvParser;

    public CsvReader(Reader reader) {
        this(reader, ',', '\"', '\\');
    }

    public CsvReader(Reader reader, char separator, char quotechar, char escape) {
        this(reader, separator, quotechar, escape, false);
    }

    public CsvReader(Reader reader, char separator, char quotechar, char escape, boolean strictQuotes) {
        this(reader, separator, quotechar, escape, strictQuotes, true);
    }

    public CsvReader(Reader reader, char separator, char quotechar, char escape, boolean strictQuotes, boolean ignoreLeadingWhiteSpace) {
        this.m_currentLine = null;
        this.m_bufferedReader = new BufferedReader(reader);
        this.m_csvParser = new CSVParser(separator, quotechar, escape, strictQuotes, ignoreLeadingWhiteSpace);
    }

    String[] readNext() {
        String[] result = null;

        do {
            if(this.m_currentLine == null && !this.getNextLine()) {
                return result;
            }

            try {
                String[] ex = this.m_csvParser.parseLineMulti(this.m_currentLine);
                if(ex.length > 0) {
                    if(result == null) {
                        result = ex;
                    } else {
                        String[] t = new String[result.length + ex.length];
                        System.arraycopy(result, 0, t, 0, result.length);
                        System.arraycopy(ex, 0, t, result.length, ex.length);
                        result = t;
                    }
                }
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        } while(this.m_csvParser.isPending());

        this.m_currentLine = null;
        return result;
    }

    boolean getNextLine() {
        if(this.m_currentLine == null) {
            try {
                this.m_currentLine = this.m_bufferedReader.readLine();
            } catch (IOException var2) {
                throw new RuntimeException(var2);
            }
        }

        return this.m_currentLine != null;
    }

    public void close() throws IOException {
        this.m_bufferedReader.close();
    }

    public Iterator<String[]> iterator() {
        return new Iterator() {
            public boolean hasNext() {
                return CsvReader.this.getNextLine();
            }

            public String[] next() {
                return CsvReader.this.readNext();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
