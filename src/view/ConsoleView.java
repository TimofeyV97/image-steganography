package view;

import model.TaskType;
import model.exceptions.InvalidChoiceException;
import model.exceptions.TextInputException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleView {

	private final BufferedReader bufferedReader;

	public ConsoleView() {
		this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	}

	public TaskType chooseTaskType() throws InvalidChoiceException {
		print("Please enter task:\n1. Encode\n2. Decode\n3. Analyze PSNR\n(1/2/3): ");
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

			case 3:
				return TaskType.PSNR;

			default:
				throw new InvalidChoiceException();
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

}
