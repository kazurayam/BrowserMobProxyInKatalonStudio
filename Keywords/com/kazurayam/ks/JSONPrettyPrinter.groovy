package com.kazurayam.ks

class JSONPrettyPrinter {

	/**
	 * Read a character stream from the InputStream, 
	 * which is supposed to be a large un-indented JSON string,
	 * pretty-print it, 
	 * and write the character stream immediately into the OutputStream
	 * without buffering the stream into a String variable.
	 * 
	 * This method is useful to perform "pretty-print" on a super large JSON file 
	 * (some GIGA bytes).
	 * This method does not require a large memory of GIGA-bytes.
	 * 
	 * @param unformattedJSON
	 * @param prettyPrintedJSON
	 * @throws IOException
	 */
	static void prettyPrintJSON(InputStream unformattedJSON,
			OutputStream prettyPrintedJSON) {
		InputStream is = new InputStreamReader(unformattedJSON, "utf-8");
		OutputStream os = new OutputStreamWriter(prettyPrintedJSON,"utf-8"); 
		prettyPrintJSON(is, os);
		is.close();
		os.close();
	}

	static void prettyPrintJSON(Reader unformattedJSON, Writer prettyPrintedJSON) {
		BufferedReader br = new BufferedReader(unformattedJSON)
		PrintWriter pw = new PrintWriter(new BufferedWriter(prettyPrintedJSON));
		//
		StringBuilder sb = new StringBuilder();
		int indentLevel = 0;
		boolean inQuote = false;
		String line;
		while ((line = br.readLine()) != null) {
			for (char ch : line.toCharArray()) {
				switch (ch) {
					case '"':
					// switch the quoting status
						inQuote = !inQuote;
						sb.append(ch);
						break;
					case ' ':
					case '\t':
					// For space and tab: ignore the space if it is not being quoted.
						if (inQuote) {
							sb.append(ch);
						}
						break;
					case '{':
					case '[':
					// Starting a new block: increase the indent level
						sb.append(ch);
						indentLevel++;
						indetLineAppendNL(indentLevel, sb);
						break;
					case '}':
					case ']':
					// Ending a new block; decrease the indent level
						indentLevel--;
						indetLineAppendNL(indentLevel, sb);
						sb.append(ch);
						break;
					case ',':
					// Ending a json item; create a new line after
						sb.append(ch);
						if (!inQuote) {
							indetLineAppendNL(indentLevel, sb);
						}
						break;
					default:
						sb.append(ch);
				}
			}
		}
		pw.println(sb.toString());
		pw.flush();
		pw.close();
		br.close();
	}


	/**
	 * Print a new line with indentation ion at the beginning of the new line.
	 * Append a NewLine char at the end.
	 * 
	 * @param indentLevel
	 * @param stringBuilder
	 */
	private static void indetLineAppendNL(int indentLevel, StringBuilder stringBuilder) {
		stringBuilder.append("\n");
		for (int i = 0; i < indentLevel; i++) {
			// Assuming indentation using 4 spaces
			stringBuilder.append("    ");
		}
	}
}
