package org.poa1024.maskedlogs.processor;

public class LogProcessorWithInitializationException implements LogProcessor {

    private final Exception exception;
    private final Class<?> errorSource;

    public LogProcessorWithInitializationException(Class<?> errorSource, Exception exception) {
        this.exception = exception;
        this.errorSource = errorSource;
    }

    @Override
    public String process(String log) {
        return "<<<" + errorSource.getSimpleName() + ":" + exception.getMessage() + ">>> " + log;
    }
}
