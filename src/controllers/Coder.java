package controllers;

import model.exceptions.EncodeException;
import org.jfree.data.xy.XYSeries;
import view.ConsoleView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Coder {

	private static final Integer BITS = 8;

	private static final Integer BITS_PER_CHAR = 16;

	private static final String OUTPUT_FILE_PATH = "output.bmp";

	private final ConsoleView consoleView;

	private final FileHelper fileHelper;

	public Coder() {
		this.consoleView = new ConsoleView();
		this.fileHelper = new FileHelper();
	}

	public void encode(final List<Integer> imageBytes, final List<Integer> textBytes, final int degree) throws EncodeException {
		final List<Integer> result = new ArrayList<>(imageBytes.subList(0, 54));
		final List<Integer> bytes = new ArrayList<>(imageBytes.subList(54, imageBytes.size()));
		int encodedBits = 0;
		int textBytesIndex = 0;
		int imageBytesIndex = 0;

		if (!canEncode(bytes, textBytes)) {
			throw new EncodeException("Can't encode: too long text.");
		}

		while (textBytesIndex < textBytes.size()) {
			final int textByte = textBytes.get(textBytesIndex);
			int byteFromImage = bytes.get(imageBytesIndex);

			if (encodedBits == BITS_PER_CHAR) {
				textBytesIndex++;
				encodedBits = 0;
			}

			for (int i = degree - 1; i >= 0; i--) {
				final int bitFromByte = getBit(textByte, (BITS_PER_CHAR - encodedBits - 1));

				System.out.printf("Encoding '%c' = %s, bit = %d\n", textByte, Integer.toBinaryString(textByte), bitFromByte);

				byteFromImage = setBit(byteFromImage, bitFromByte, i);
				encodedBits++;

				if (encodedBits == BITS_PER_CHAR) {
					textBytesIndex++;
					encodedBits = 0;
					break;
				}
			}

			bytes.set(imageBytesIndex, byteFromImage);
			imageBytesIndex++;
		}

		result.addAll(bytes);

		try {
			consoleView.println("Writing bytes to " + OUTPUT_FILE_PATH + "...");
			fileHelper.writeBytes(OUTPUT_FILE_PATH, result);
		} catch (final Exception e) {
			throw new EncodeException("Error writing the output.");
		}
	}

	public String decode(final List<Integer> imageBytes, final int encodedMessageLength,  final int degree) {
		final StringBuilder result = new StringBuilder();
		final List<Integer> bytes = new ArrayList<>(imageBytes.subList(54, imageBytes.size()));
		int decodedByte = 0;
		int imageBytesIndex = 0;
		int charsDecoded = 0;
		int bitsRead = 0;

		while (charsDecoded < encodedMessageLength) {
			int byteFromImage = bytes.get(imageBytesIndex);

			if (bitsRead == BITS_PER_CHAR) {
				result.append((char) decodedByte);
				charsDecoded++;
				bitsRead = 0;
			}

			for (int i = 0; i < degree; i++) {
				decodedByte = setBit(decodedByte, getBit(byteFromImage, (degree - i - 1)), BITS_PER_CHAR - bitsRead - 1);
				bitsRead++;

				if (bitsRead == BITS_PER_CHAR) {
					final char decodedChar = (char) decodedByte;

					System.out.printf("Decoded char '%c' = %d\n", decodedChar, decodedByte);

					result.append(decodedChar);
					charsDecoded++;
					bitsRead = 0;
					break;
				}
			}

			imageBytesIndex++;
		}

		return result.toString();
	}

	public void analyzePsnr(
			final int imageWidth,
			final int imageHeight,
			final List<Integer> imageBytes,
			final int degree
	) throws Exception {
		final XYSeries series = new XYSeries("dependency");

		for (int i = 1; i <= 3; i++) {
			final StringBuilder textToTest = new StringBuilder("Test");

			for (int j = 0; j < i; j++) {
				textToTest.append(" Test Test Test Test Test Test");
			}

			System.out.printf("Test %d. Encoding text '%s'\n", i, textToTest.toString());

			final List<Integer> textBytes = Arrays.stream(textToTest.toString().split(""))
					.map(s -> (int) s.charAt(0))
					.collect(Collectors.toList());

			encode(imageBytes, textBytes, degree);

			final List<Integer> encodedImageBytes = fileHelper.readBytes("output.bmp");
			final double Psnr = calculatePsnr(imageWidth, imageHeight, imageBytes, encodedImageBytes);
			final int textLength = textToTest.length();

			series.add(textLength, Psnr);
			System.out.printf("PSNR value for test %d = %f. Text length = %d\n", i, Psnr, textLength);
		}

		final Plot example = new Plot("PSNR/Message length", series);

		example.setSize(900, 400);
		example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		example.setVisible(true);
	}

	private double calculatePsnr(
			final int width,
			final int height,
			final List<Integer> imageBytes,
			final List<Integer> encodedImageBytes
	) {
		final List<Integer> bytes = imageBytes.subList(54, imageBytes.size());
		final List<Integer> encodedBytes = encodedImageBytes.subList(54, imageBytes.size());
		final int offset = 3;
		double sum = 0;

		for (int i = 0; i < width * height; i++) {
			sum += pow((bytes.get(i * offset) - encodedBytes.get(i * offset)), 2)
					+ pow((bytes.get(i * offset + 1) - encodedBytes.get(i * offset + 1)), 2)
					+ pow((bytes.get(i * offset + 2) - encodedBytes.get(i * offset + 2)), 2);
		}

		double MSE = sum / (width * height * 3 * 3);

		return 10 * log10(pow(255, 2) / MSE);
	}

	private int getBit(final int number, final int position) {
		return (number >> position) & 1;
	}

	private int setBit(int myByte, final int bit, final int position) {
		return bit == 1 ? (myByte | (1 << position)) : (myByte & ~(1 << position));
	}

	private boolean canEncode(final List<Integer> imageBytes, final List<Integer> textBytes) {
		return imageBytes.size() * BITS >= textBytes.size() * BITS_PER_CHAR;
	}

}
