package view;

import controllers.FileHelper;
import model.TaskType;
import model.exceptions.FileParseException;
import model.exceptions.InvalidChoiceException;
import model.exceptions.TextInputException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class ConsoleView {

	private final BufferedReader bufferedReader;

	private final FileHelper fileHelper;

	public ConsoleView() {
		this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		this.fileHelper = new FileHelper();
	}

	public TaskType chooseTaskType() throws InvalidChoiceException {
		print("Please enter task:\n1. Encode\n2. Decode\n(1/2): ");
		final int type;

		try {
			type = Integer.parseInt(bufferedReader.readLine());
		} catch (final Exception exception) {
			throw new InvalidChoiceException();
		}

		switch (type) {
			case 1:
				return TaskType.ENCODE;

			case 2:
				return TaskType.DECODE;

			default:
				throw new InvalidChoiceException();
		}
	}

	public List<Integer> readImage() throws FileParseException {
		print("Please enter the file path: ");

		try {
			return fileHelper.readBytes(bufferedReader.readLine());
		} catch (final Exception exception) {
			throw new FileParseException();
		}
	}

	public String readFromCLI(final String prompt) throws TextInputException {
		System.out.print(prompt);

		try {
			return bufferedReader.readLine();
		} catch (final Exception exception) {
			throw new TextInputException();
		}
	}

	public void print(final String str) {
		System.out.print(str);
	}

	public void println(final String str) {
		System.out.println(str);
	}

	public void printf(final String str) {

	}

}
