package com.github.e13mort.stf.console.commands.devices;

import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.row.ContentRow;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class TablePrinter {

    private static final String NUMBER_HEADER = "#";
    private final V2_AsciiTable table;
    private int rowCounter = 1;

    public TablePrinter(Collection<String> columnNames) {
        table = prepareTable(columnNames);
    }

    public V2_AsciiTable addDevice(Collection<String> strings) throws Exception {
        table.addRow(addFirstElement(strings, String.valueOf(rowCounter++)));
        table.addRule();
        return table;
    }

    public void print(PrintStream to) {
        V2_AsciiTableRenderer renderer = new V2_AsciiTableRenderer();
        renderer.setWidth(new WidthLongestLine());
        RenderedTable render = renderer.render(table);
        to.println(render.toString());
    }

    private V2_AsciiTable prepareTable(Collection<String> columnNames) {
        V2_AsciiTable table = new V2_AsciiTable();
        table.addStrongRule();
        List<String> headerContent = addFirstElement(columnNames, NUMBER_HEADER);
        printHeader(table, headerContent);
        table.addStrongRule();
        return table;
    }

    private void printHeader(V2_AsciiTable table, Collection<String> columnNames) {
        ContentRow header = table.addRow(columnNames);
        char[] chars = new char[columnNames.size()];
        Arrays.fill(chars, 'c');
        header.setAlignment(chars);
    }

    private List<String> addFirstElement(Collection<String> target, String element) {
        final ArrayList<String> content = new ArrayList<>(target);
        content.add(0, element);
        return content;
    }
}
