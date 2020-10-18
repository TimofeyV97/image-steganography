package controllers;

import model.exceptions.EncodeException;
import java.util.ArrayList;
import java.util.List;

public class Coder {

	protected static final Integer BITS = 8;

	protected static final Integer BITS_PER_CHAR = 16;

	private final FileHelper fileHelper;

	public Coder() {
		this.fileHelper = new FileHelper();
	}

	public void encode(final List<Integer> imageBytes, final List<Integer> textBytes, final int degree) throws EncodeException {
		final List<Integer> result = new ArrayList<>(imageBytes.subList(0, 54));
		final List<Integer> bytes = imageBytes.subList(54, imageBytes.size());
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

				System.out.printf("Encoding %c = %s, bit = %d\n", textByte, Integer.toBinaryString(textByte), bitFromByte);

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
			fileHelper.writeBytes("output.bmp", result);
		} catch (final Exception e) {
			throw new EncodeException("Error writing the output.");
		}
	}

	public String decode(final List<Integer> imageBytes, final int encodedMessageLength,  final int degree) {
		final StringBuilder result = new StringBuilder();
		final List<Integer> bytes = imageBytes.subList(54, imageBytes.size());
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

					System.out.printf("Decoded char %c = %d\n", decodedChar, decodedByte);

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
