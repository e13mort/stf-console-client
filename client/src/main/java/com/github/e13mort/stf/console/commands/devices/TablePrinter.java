package com.github.e13mort.stf.console.commands.devices;

import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.row.ContentRow;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

class TablePrinter {

    private final V2_AsciiTable table;

    public TablePrinter(Collection<String> columnNames) {
        table = prepareTable(columnNames);
    }

    public V2_AsciiTable addDevice(Collection<String> strings) throws Exception {
        table.addRow(strings);
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
        printHeader(table, columnNames);
        table.addStrongRule();
        return table;
    }

    private void printHeader(V2_AsciiTable table, Collection<String> columnNames) {
        ContentRow header = table.addRow(columnNames);
        char[] chars = new char[columnNames.size()];
        Arrays.fill(chars, 'c');
        header.setAlignment(chars);
    }
}
