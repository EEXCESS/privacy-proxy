package eu.eexcess.insa.crypto;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
	public static String hmac_sha256(String utf8input, String key) throws InvalidKeyException {
		try {
			Mac hmac = Mac.getInstance("HmacSHA256");
			Key hMacKey = new SecretKeySpec(key.getBytes(),"SHA-256");
			hmac.init(hMacKey);
			hmac.update(utf8input.getBytes());
			return digest2String(hmac.doFinal());
		} catch(NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	    }
	}

	public static String digest2String(byte[] messageDigest) {
		return digest2String(messageDigest, 16);
	}

	public static String digest2String(byte[] messageDigest, int radix) {
		BigInteger number = new BigInteger(1,messageDigest);
	    String md5 = number.toString(radix);
	    if (md5.length() % 2 != 0) {
	    	// Pad to an even number of characters
	    	return "0"+md5;
	    } else {
	    	return md5;
	    }
	}
}