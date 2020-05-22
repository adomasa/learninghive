package distributed.monolith.learninghive.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

@Component
public class FileLogger implements Logger {
	@Value("${logging.logFile}")
	private String fileName;

	private BufferedWriter writer;

	@Override
	public void log(String message) {
		try {
			if (writer == null) {
				writer = Files.newBufferedWriter(Paths.get(fileName),
						StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}

			writer.write(String.format("%s: %s\n", new Timestamp(System.currentTimeMillis()), message));
			writer.flush();
		} catch (IOException e) {
			System.out.println(String.format("Failed to log message to file: %s",  e.getMessage()));
		}
	}
}
