package org.azodi.prj;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSVOutput {

    private final CSVPrinter printer;

    public CSVOutput(String path, List<String> headers) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        String[] headerNames = new String[headers.size()];
        printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(headerNames)));
    }

    public void addItem(List<String> items) throws IOException {
        printer.printRecord(items);
    }

    public void close() throws IOException {
        printer.flush();
        printer.close();
    }
}
