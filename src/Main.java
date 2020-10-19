import controllers.Coder;
import controllers.FileHelper;
import model.TaskType;
import model.exceptions.EncodeException;
import model.exceptions.InvalidChoiceException;
import model.exceptions.TextInputException;
import view.ConsoleView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args)  {
		final ConsoleView consoleView = new ConsoleView();
		final Coder coder = new Coder();
		final FileHelper fileHelper = new FileHelper();
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		final String imagePath;
		final List<Integer> imageBytes;
		final TaskType taskType;
		final int degree;

		try {
			taskType = consoleView.chooseTaskType();
		} catch (final InvalidChoiceException e) {
			consoleView.println("Invalid choice.");
			return;
		}

		try {
			degree = Integer.parseInt(consoleView.readFromCLI("Please enter bits degree: "));
		} catch (final Exception e) {
			consoleView.println("Error reading text.");
			return;
		}

		if (degree < 1 || degree > 8) {
			consoleView.println("Incorrect bits degree.");
			return;
		}

		try {
			consoleView.print("Please enter the file path: ");

			imagePath = bufferedReader.readLine();
			imageBytes = fileHelper.readBytes(imagePath);
		} catch (final Exception e) {
			consoleView.println("Error reading image.");
			return;
		}

		switch (taskType) {
			case ENCODE:
				final String textToEncode;

				try {
					textToEncode = consoleView.readFromCLI("Please enter the text to encode: ");
				} catch (final TextInputException e) {
					consoleView.println("Error reading text.");
					return;
				}

				final List<Integer> textBytes = Arrays.stream(textToEncode.split(""))
						.map(s -> (int) s.charAt(0))
						.collect(Collectors.toList());

				try {
					coder.encode(imageBytes, textBytes, degree);
					consoleView.println("Encoded successfully.");
				} catch (final EncodeException e) {
					consoleView.println(e.getMessage());
				}

				break;

			case DECODE:
				final int symbolsToRead;

				try {
					symbolsToRead = Integer.parseInt(
							consoleView.readFromCLI("Please enter the length of the encoded message: ")
					);
				} catch (final Exception e) {
					consoleView.println("Error reading the length of the message.");
					return;
				}

				consoleView.println("Decoded text: " + coder.decode(imageBytes, symbolsToRead, degree));
				break;

			case PSNR:
				final BufferedImage bi;

				try {
					bi = ImageIO.read(new File(imagePath));
				} catch (final IOException e) {
					consoleView.println("Error reading image.");
					return;
				}

				try {
					coder.analyzePsnr(bi.getWidth(), bi.getHeight(), imageBytes, degree);
				} catch (final EncodeException e) {
					consoleView.print(e.getMessage());
				} catch (final Exception e) {
					consoleView.println("Error reading image.");
				}
		}
	}

}
