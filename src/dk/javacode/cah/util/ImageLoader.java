package dk.javacode.cah.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	public static String ROOT_FOLDER = null;

	public ImageLoader() {
	}
	
	
	public static BufferedImage load(String location) throws IOException {
		return ImageIO.read(new File(ROOT_FOLDER, location));
	}

}
