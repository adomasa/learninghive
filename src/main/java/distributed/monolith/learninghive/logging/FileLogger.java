package distributed.monolith.learninghive.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
				writer = new BufferedWriter(new FileWriter(fileName, true));
			}

			writer.write(String.format("%s: %s\n", new Timestamp(System.currentTimeMillis()), message));
			writer.flush();

		} catch (IOException e) {
			System.out.printf("Failed to log message to file");
			e.printStackTrace();
		}
	}
}
