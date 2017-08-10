package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.IStringConverter;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.adapter.filters.StringsFilterParser;

public class FilterDescriptionConverterImpl implements IStringConverter<StringsFilterDescription> {

    private StringsFilterParser parser = new StringsFilterParser();

    @Override
    public StringsFilterDescription convert(String value) {
        return parser.parse(value);
    }
}
