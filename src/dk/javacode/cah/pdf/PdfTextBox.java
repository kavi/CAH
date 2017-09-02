package dk.javacode.cah.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PdfTextBox {

	private int margin = 8;
	private float x1, x2, y1, y2;

	private Color borderColor = Color.BLACK;
	private Color backgroundColor = Color.WHITE;
	private Color textColor = Color.BLACK;

	private boolean border = true;
	private boolean fill = true;

	public PdfTextBox(float x1, float y1, float x2, float y2) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public void draw(String text, PDFont font, PDPageContentStream contentStream, float fontSize) throws IOException {
		if (fill) {
			contentStream.setNonStrokingColor(backgroundColor);
			contentStream.fillRect(x1, y1, x2 - x1, y2 - y1);
		}

		contentStream.setNonStrokingColor(textColor);

		float fontHeight = fontSize * font.getFontBoundingBox().getHeight() / 1000f;

		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		float y = y1 - ((margin / 7.5f) * fontHeight);
		float x = x1 + margin;
		contentStream.moveTextPositionByAmount(x, y);
		List<String> lines = textToLines(text, (x2 - x1 - (1.8f * margin)), font, fontSize);
		if ((lines.size() * fontHeight) > (y1 - y2)) {
			Logger.getLogger(PdfTextBox.class).warn("Too many lines: " + lines.size() + ", y1:" + y1 + ", y2: " + y2 + ", fontHeight: " + fontHeight + ", text: " + text);
		}
		for (String s : lines) {
			contentStream.drawString(s);
			contentStream.moveTextPositionByAmount(0, -fontHeight);
		}
		contentStream.endText();

		if (border) {
			contentStream.setStrokingColor(borderColor);
			contentStream.drawPolygon(new float[] { x1, x2, x2, x1 }, new float[] { y1, y1, y2, y2 });
		}
		// contentStream.addRect(x1, y1, x2 - x1, y1 - y2);
		// contentStream.dr
	}

	public List<String> textToLines(String text, float width, PDFont font, float fontSize) throws IOException {
		String[] words = text.split("\\s");
		List<String> lines = new LinkedList<String>();
		width = (width * (1000f / fontSize));
		float spacewidth = font.getSpaceWidth();

		String line = "";
		for (String word : words) {

			float currentwidth = 0;
			while (word.length() > 0) {
				float wordwidth = font.getStringWidth(word);
				currentwidth = font.getStringWidth(line);
				String space = "";
				int idx = word.indexOf("-");

				String part1 = word;
				String part2 = "";
				if (idx > 0) {
					part1 = word.substring(0, idx + 1);
					part2 = word.substring(idx + 1);
				}
				float part1width = font.getStringWidth(part1);

				if (line.length() > 0) {
					space = " ";
				}
				if (currentwidth + wordwidth + spacewidth < width) {
					line += space + word;
					word = "";
				} else if (currentwidth + spacewidth + part1width < width) {
					line += space + part1;
					lines.add(line);
					line = "";
					word = part2;
				} else if (line.length() == 0) {
					line = word;
					lines.add(line);
					line = "";
					word = "";
				} else {
					lines.add(line);
					line = "";
					// word = "";
				}
			}

			// if (line.length() == 0) {
			// if (currentwidth + wordwidth + spacewidth < width) {
			// line = word;
			// } else {
			// boolean added = false;
			// if (word.contains("-")) {
			// int idx = word.indexOf("-");
			// String part1 = word.substring(0, idx + 1);
			// String part2 = word.substring(idx + 1);
			// float part1width = font.getStringWidth(part1);
			// if (currentwidth + part1width + spacewidth < width) {
			// line += part1;
			// lines.add(line);
			// line = part2;
			// added = true;
			// }
			// }
			// if (!added) {
			// lines.add(line);
			// line = word;
			// }
			// }
			// } else if (currentwidth + wordwidth + spacewidth < width) {
			// line += " " + word;
			// } else {
			// if (word.contains("-")) {
			// System.out.println(word);
			// int idx = word.indexOf("-");
			// String part1 = word.substring(0, idx + 1);
			// String part2 = word.substring(idx + 1);
			// float part1width = font.getStringWidth(part1);
			// if (currentwidth + part1width + spacewidth < width) {
			// line += " " + part1;
			// lines.add(line);
			// line = part2;
			// } else {
			// lines.add(line);
			// currentwidth = 0;
			// line = part1;
			// lines.add(line);
			// line = part2;
			// }
			// } else {
			// lines.add(line);
			// line = word;
			// }
			// }
		}
		lines.add(line);

		return lines;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

}
