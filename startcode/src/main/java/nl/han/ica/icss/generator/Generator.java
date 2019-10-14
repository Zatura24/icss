package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;

public class Generator {

	public String generate(AST ast) {
        StringBuilder stringBuilder = new StringBuilder();

		for (ASTNode node:
			 ast.root.getChildren()) {
			stringBuilder.append(generate(node));
		}

		return stringBuilder.toString();
	}

	private String generate(ASTNode node) {
		StringBuilder stringBuilder = new StringBuilder();

		if (node instanceof Stylerule) {
			for (Selector selector :
					((Stylerule) node).selectors) {
					stringBuilder.append(selector.toString()).append(" ");
			}

			stringBuilder.append("{\r\n");

			for (ASTNode declaration :
					((Stylerule) node).body) {
				if (declaration instanceof Declaration) {
					stringBuilder.append("\t").append(((Declaration) declaration).property.name).append(": ");
					stringBuilder.append(((Declaration) declaration).expression.toString()).append(";\r\n");
				}
			}

			stringBuilder.append("}\r\n\r\n");
		}

		return stringBuilder.toString();
	}
}
