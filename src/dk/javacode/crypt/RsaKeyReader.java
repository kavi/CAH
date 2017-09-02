package dk.javacode.crypt;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class RsaKeyReader {
	public static final String ALGORITHM = "RSA";
	public static final String ENCODING = "UTF8";

	public static PublicKey loadPublicKey(File publicKeyUrl) throws URISyntaxException, FileNotFoundException,
			IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = getKeyBytes(publicKeyUrl);
		return loadPublicKey(keyBytes);
	}

	public static PublicKey loadPublicKeyBase64(File publicKeyUrl) throws URISyntaxException, FileNotFoundException,
			IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = getKeyBytesFromBase64(publicKeyUrl);
		return loadPublicKey(keyBytes);
	}

	public static PrivateKey loadPrivateKey(File privateKeyUrl) throws URISyntaxException, FileNotFoundException,
			IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return loadPrivateKey(getKeyBytes(privateKeyUrl));
	}

	public static PrivateKey loadPrivateKeyBase64(File privateKeyUrl) throws URISyntaxException, FileNotFoundException,
			IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return loadPrivateKey(getKeyBytesFromBase64(privateKeyUrl));
	}

	private static PublicKey loadPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
		return kf.generatePublic(spec);
	}

	private static PrivateKey loadPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
		return kf.generatePrivate(spec);
	}

	private static byte[] getKeyBytes(File file) throws URISyntaxException, FileNotFoundException, IOException {
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			byte[] keyBytes = new byte[(int) file.length()];
			dis.readFully(keyBytes);
			return keyBytes;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(dis);
		}
	}

	private static byte[] getKeyBytesFromBase64(File file) throws URISyntaxException, FileNotFoundException,
			IOException {
		FileInputStream dis = null;
		try {
			dis = new FileInputStream(file);
			List<String> lines = IOUtils.readLines(dis);
			StringBuilder sb = new StringBuilder();
			for (String l : lines) {
				sb.append(l);
			}
			byte[] keyBytes = CryptHelper.base64Decode(sb.toString());
			return keyBytes;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(dis);
		}
	}
}
