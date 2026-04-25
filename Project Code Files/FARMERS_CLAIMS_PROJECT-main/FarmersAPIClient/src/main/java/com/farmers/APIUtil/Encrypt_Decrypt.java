package com.farmers.APIUtil;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class Encrypt_Decrypt {
	
	private static final String UNICODE_FORMAT = "UTF-8";
	private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	
	private KeySpec ks;
	private SecretKeyFactory skf;
	private Cipher cipher;
	byte[] arraybytes;
	private String encryptionkey;
	private String myencryptionscheme;
	SecretKey key;
	
	public Encrypt_Decrypt() throws Exception {
		encryptionkey = "usernamepasswordencrypts";
		myencryptionscheme = DESEDE_ENCRYPTION_SCHEME;
		arraybytes = encryptionkey.getBytes(UNICODE_FORMAT);
		ks = new DESedeKeySpec(arraybytes);
		skf = SecretKeyFactory.getInstance(myencryptionscheme);
		cipher = Cipher.getInstance(myencryptionscheme);
		key = skf.generateSecret(ks);
	}
	
	public String encrypt(String unencryptedString) {
		String encryptedString = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plaintext = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] encryptedtext = cipher.doFinal(plaintext);
			encryptedString = new String(Base64.getEncoder().encodeToString(encryptedtext));
					//Base64.getEncoder().encodeToString(encryptedString);
		}
		catch (Exception e) {
			System.out.println("Exception Occured : "+e);
			e.printStackTrace();
		}
		return 	encryptedString;
	}
	
	public String decrypt(String encryptedString) {
		
		String decryptedtext = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedtext = Base64.getDecoder().decode(encryptedString);
			byte[] plaintext = cipher.doFinal(encryptedtext);
			decryptedtext = new String(plaintext);
 		}
		catch (Exception e) {
			System.out.println("Exception Occured : "+e);
			e.printStackTrace();
		}
		return decryptedtext;
	}

 	public static void main(String[] args) {
	Encrypt_Decrypt td;
	try {
		td = new Encrypt_Decrypt();
		String target = "5mhOkcAb9K1AjGjEc+AkBU8Z+1w4FHGjPwoaHawCWSHFR9WbK/Giuw==";
		//String encrypted = td.encrypt(target);
		String decrypted = td.decrypt(target);
		
		System.out.println("original string = "+target);
		//System.out.println("encrypted string = "+encrypted);
		System.out.println("decrypted string = "+decrypted);
	} catch (Exception e) {
		System.err.println("Exception occured : " + e);
		e.printStackTrace();
	}
		}  

}
