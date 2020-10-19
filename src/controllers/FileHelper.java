package controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

	public List<Integer> readBytes(final String path) throws Exception {
		final FileInputStream in = new FileInputStream(path);
		final List<Integer> list = new ArrayList<>();
		int c;

		while ((c = in.read()) != -1) {
			list.add(c);
		}

		in.close();

		return list;
	}

	public void writeBytes(final String path, final List<Integer> bytes) throws Exception {
		final FileOutputStream out;
		out = new FileOutputStream(path);

		for (final int b : bytes) {
			out.write(b);
		}

		out.close();
	}

}
