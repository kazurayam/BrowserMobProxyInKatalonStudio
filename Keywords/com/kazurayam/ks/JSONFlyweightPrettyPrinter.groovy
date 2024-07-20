package com.kazurayam.ks

/**
 * This utility class pretty-prints a JSON. 
 * It inserts NewLine characters, inserts line indentations, 
 * strips redundant white spaces, so that the JSON becomes better readable for human.
 *
 * This method reads a character stream from the InputStream as the 1st argument,
 * which is supposed to be a large JSON string,
 * pretty-print it,
 * and write the character stream immediately into the OutputStream as the 2nd argument.
 * 
 * This class does pretty-printing without buffering the input character stream 
 * into a variable of type java.lang.String; therefore it is flyweighted.
 *
 * This method requires minimum memory to run regardless how large the input JSON is.
 *
 * This method is useful to perform "pretty-print" on a super large JSON file
 * of hundreds of mega-bytes.
 *
 */
class JSONFlyweightPrettyPrinter {

	static void prettyPrint(InputStream unformattedJSON, OutputStream prettyPrintedJSON) {
		Reader reader = new InputStreamReader(unformattedJSON, "utf-8");
		Writer writer = new OutputStreamWriter(prettyPrintedJSON,"utf-8");
		prettyPrint(reader, writer);
		reader.close();
		writer.close();
	}

	static void prettyPrint(Reader unformattedJSON, Writer prettyPrintedJSON) {
		BufferedReader br = new BufferedReader(unformattedJSON)
		PrintWriter pw = new PrintWriter(new BufferedWriter(prettyPrintedJSON));
		//
		StringBuilder sb = new StringBuilder();
		int indentLevel = 0;
		boolean inQuote = false;
		String line;
		// loop over all input lines,
		while ((line = br.readLine()) != null) {
			// loop over all characters in a line
			for (char ch : line.toCharArray()) {
				// pretty print it
				switch (ch) {
					case '"':
					// switch the quoting status
						sb.append(ch);
						inQuote = !inQuote;
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
						newLineAndIndent(indentLevel, sb);
						break;
					case '}':
					case ']':
					// Ending a new block; decrease the indent level
						indentLevel--;
						newLineAndIndent(indentLevel, sb);
						sb.append(ch);
						break;
					case ',':
					// Ending a JSON item; create a new line after
						sb.append(ch);
						if (!inQuote) {
							newLineAndIndent(indentLevel, sb);
						}
						break;
					default:
						sb.append(ch);
				}
			}
		}
		pw.print(sb.toString());
		pw.flush();
		pw.close();
		br.close();
	}


	/**
	 * Print a new line with indentation at the beginning of the new line.
	 * Append a NewLine char at the end.
	 * 
	 * @param indentLevel
	 * @param stringBuilder
	 */
	private static void newLineAndIndent(int indentLevel, StringBuilder stringBuilder) {
		stringBuilder.append(System.lineSeparator());
		for (int i = 0; i < indentLevel; i++) {
			// Assuming indentation using 4 spaces
			stringBuilder.append("    ");
		}
	}
}
