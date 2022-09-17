package org.poa1024.maskedlogs.pattern;

import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface PatternSupplier extends Supplier<Stream<Pattern>> {
}
