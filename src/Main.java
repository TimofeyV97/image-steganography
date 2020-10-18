import controllers.Coder;
import model.TaskType;
import model.exceptions.EncodeException;
import model.exceptions.FileParseException;
import model.exceptions.InvalidChoiceException;
import model.exceptions.TextInputException;
import view.ConsoleView;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args)  {
		final ConsoleView consoleView = new ConsoleView();
		final Coder coder = new Coder();
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
			imageBytes = consoleView.readImage();
		} catch (final FileParseException e) {
			consoleView.println("Error reading image.");
			return;
		}

		if (taskType == TaskType.ENCODE) {
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
				System.out.println(e.getMessage());
			}
		} else {
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
		}
	}

}
