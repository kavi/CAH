package dk.javacode.cah.rest.admin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.pdf.PdfTextBox;
import dk.javacode.cah.util.ImageLoader;

public class PdfStreamRepresentation extends OutputRepresentation {
	private List<WhiteCard> whitecards;
	private List<BlackCard> blackcards;
	private int rowsPerPage;

	public PdfStreamRepresentation(List<WhiteCard> whitecards, List<BlackCard> blackcards, int rowsPerPage) {
		super(MediaType.APPLICATION_PDF);
		this.whitecards = whitecards;
		this.blackcards = blackcards;
		this.rowsPerPage = rowsPerPage;
	}

	@Override
	public void write(OutputStream out) {
		try {
			doWrite(out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void doWrite(OutputStream out) throws IOException, COSVisitorException {

		PDDocument document = new PDDocument();
		PDXObjectImage image = loadImage(document, "logo.png");
		PDXObjectImage invertImageSmall = loadImage(document, "logo_invert_small.png");
		PDXObjectImage invertImage = loadImage(document, "logo_invert.png");

		PDFont font = PDType1Font.HELVETICA_BOLD;

		int defaultFontSize = 11;
		int smallSize = 9;
		int maxTextSize = 100;
		if (rowsPerPage == 5) {
			maxTextSize = 130;
		} else if (rowsPerPage == 4) {
			maxTextSize = 200;
		}
		
		int pageHeight = (int) PDPage.PAGE_SIZE_A4.getHeight();
		int topMargin = 42;
		int bottomMargin = 62;
		int top = pageHeight - topMargin;
		int height = (top - bottomMargin) / rowsPerPage;
		int i = 0;
		
		int sideMargin = 40;
		int pageWidth = (int) PDPage.PAGE_SIZE_A4.getWidth();
		int maxWidth = (pageWidth - sideMargin);
		int width = ((maxWidth - sideMargin) / 4);

		while (i < whitecards.size()) {
			PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			for (int y = top; y - height >= bottomMargin; y -= height) {
				for (int x = sideMargin; x + width <= maxWidth; x += width) {
					String text = whitecards.size() > i ? whitecards.get(i).getText() : "";
					PdfTextBox textBox = new PdfTextBox(x, y, x + width, y - height);
					textBox.setBackgroundColor(Color.WHITE);
					textBox.setTextColor(Color.BLACK);
					textBox.setBorderColor(Color.BLACK);
					textBox.setMargin(11);
					int fontSize = text.length() < maxTextSize ? defaultFontSize : smallSize; 
					textBox.draw(text, font, contentStream, fontSize);

					// throw new IOException( "Image type not supported:" +
					// image );
					// contentStream.drawImage(ximage, x + 20, y - 40);
					float imgscale = 1.4f;
					contentStream.drawXObject(image, x + 7, y - height, (imgscale * 80), (imgscale * 20));
					i++;
				}
			}
			contentStream.close();

			document.addPage(page);
		}

		i = 0;
		while (i < blackcards.size()) {
			PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			for (int y = top; y - height >= bottomMargin; y -= height) {
				for (int x = sideMargin; x + width <= maxWidth; x += width) {
					String text = blackcards.size() > i ? blackcards.get(i).getText() : "";
					text = text.replaceAll("__", "________");
					PdfTextBox textBox = new PdfTextBox(x, y, x + width, y - height);
					textBox.setBackgroundColor(Color.BLACK);
					textBox.setTextColor(Color.WHITE);
					textBox.setBorderColor(Color.WHITE);
					textBox.setMargin(11);
					int fontSize = text.length() < maxTextSize ? defaultFontSize : smallSize; 
					textBox.draw(text, font, contentStream, fontSize);

					Integer pick = blackcards.size() > i ? blackcards.get(i).getCardsToPick() : 0;
					String picktext = pick > 1 ? "Pick " + pick : "";

					PdfTextBox pickBox = new PdfTextBox(x + 60, y - height + 30, x + width - 5, y - height + 5);
					pickBox.setFill(false);
					pickBox.setTextColor(Color.WHITE);
					pickBox.setBorder(false);
					pickBox.setMargin(11);
					pickBox.draw(picktext, font, contentStream, 9);

					Integer draw = blackcards.size() > i ? blackcards.get(i).getCardsToDraw() : 0;
					String drawtext = draw != 0 ? "Draw " + draw : "";

					PdfTextBox drawBox = new PdfTextBox(x + 60, y - height + 45, x + width - 5, y - height + 32);
					drawBox.setFill(false);
					drawBox.setTextColor(Color.WHITE);
					drawBox.setBorder(false);
					drawBox.setMargin(11);
					drawBox.draw(drawtext, font, contentStream, 9);

					if ("".equals(picktext)) {
						float imgscale = 1.4f;
						contentStream.drawXObject(invertImage, x + 7, y - height + 5, (imgscale * 70), (imgscale * 20));
					} else {
						float imgscale = 1.4f;
						contentStream.drawXObject(invertImageSmall, x + 7, y - height + 5, (imgscale * 20),
								(imgscale * 20));
					}

					i++;
				}
			}
			contentStream.close();

			document.addPage(page);
		}

		document.save(out);

		document.close();
	}

	private PDXObjectImage loadImage(PDDocument document, String location) throws IOException {
		BufferedImage awtImage = ImageLoader.load(location);
		PDXObjectImage ximage = new PDPixelMap(document, awtImage);
		return ximage;
	}

}