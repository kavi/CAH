package dk.javacode.crypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptHelper {

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public CryptHelper() {
		super();
	}

	public CryptHelper(File publicKeyFile, File privateKeyFile) throws FileNotFoundException, NoSuchAlgorithmException,
			InvalidKeySpecException, URISyntaxException, IOException {
		super();
		this.publicKey = RsaKeyReader.loadPublicKey(publicKeyFile);
		this.privateKey = RsaKeyReader.loadPrivateKey(privateKeyFile);
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public static String base64Encode(byte[] data) {
		return new String(Base64.encodeBase64(data));
	}

	public static byte[] base64Decode(String encodedData) {
		return Base64.decodeBase64(encodedData);
	}

	public String encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		rsa.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] ciphertext = rsa.doFinal(text.getBytes());
		return base64Encode(ciphertext);
	}

	public String decrypt(String base64cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] cipherText = base64Decode(base64cipherText);
		Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		rsa.init(Cipher.DECRYPT_MODE, privateKey);
		String cleartext = new String(rsa.doFinal(cipherText));
		return cleartext;
	}

	public static String md5Sum(String orig, int iterations) {
		String md5 = orig;
		for (int i = 0; i < iterations; i++) {
			md5 = DigestUtils.md5Hex(md5);
		}
		return md5;
	}

	public static String encodePassword(String plain) {
		return CryptHelper.md5Sum(plain, 100);
	}

}
