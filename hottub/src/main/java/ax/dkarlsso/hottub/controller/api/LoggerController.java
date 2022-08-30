package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.LogEntry;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Basic controller for REST. Utilized by AWS Alexa, and hopefully a proper frontend if I ever prioritize it
 */
@RestController
@RequestMapping("/api")
public class LoggerController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.LogsApi {

    @Override
    public ResponseEntity<LogEntry> getLogs() {
        Integer linesPerError = 25;

        final File logFile = getLogfile();
        return ResponseEntity.ok(new LogEntry().rows(readFile(logFile, linesPerError, 600)));
    }

    // Ugly hack like everything else, will probably implement option to select which of the logs that is wanted to be used
    private File getLogfile() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                Appender<ILoggingEvent> appender = index.next();
                if(appender instanceof FileAppender) {
                    FileAppender fileAppender = (FileAppender) appender;
                    if (fileAppender.getFile().contains("debug.log")) {
                        return new File(fileAppender.getFile());
                    }
                }
            }
        }
        return null;
    }

    private List<String> readFile(final File file, int linesPerError, int totalAmountLines) {
        final List<String> logLines = new ArrayList<>();
        int linePerErrorCounter = 0;
        try {
            if(file != null) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.contains("INFO") || line.contains("WARNING") || line.contains("ERROR")) {
                        linePerErrorCounter = 0;
                    }
                    else {
                        linePerErrorCounter++;
                    }

                    if(linePerErrorCounter < linesPerError) {
                        logLines.add(0, line);
                    }
                    if (totalAmountLines < logLines.size()) {
                        return logLines;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logLines;
    }

}
