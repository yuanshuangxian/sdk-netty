package com.fqserver.lang.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.fqserver.lang.util.Encoding;


public class Rsa {

    public static final String ISO_8859_1 = Encoding.ISO_8859_1.name();
    public static final String UTF_8 = Encoding.UTF_8.name();

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final int KEYSIZE = 512;

    // 512 bit
    public static final String DEF_RSA_MODULUS_512 = "6912060769967909665747229577748486878584118041454730994790740440917251733833292356102546466418655717490140587808347980414374545347276788402037864864794123";
    public static final String DEF_RSA_PRIVATE_EXPONENT_512 = "6364259624977853318891390732106702224917244228077312547395481185075144138039461420260483980363741724467902776024236460630716647921826962535465529587263233";
    public static final String DEF_RSA_PUBLIC_EXPONENT_512 = "65537";

    public static RSAPrivateKey DefPriKey512 = getPrivateKey(DEF_RSA_MODULUS_512,
                                                             DEF_RSA_PRIVATE_EXPONENT_512);

    public static RSAPublicKey DefPubKey512 = getPublicKey(DEF_RSA_MODULUS_512,
                                                           DEF_RSA_PUBLIC_EXPONENT_512);

    public static HashMap<String, Object> vgoGetKeys() throws NoSuchAlgorithmException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(KEYSIZE);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    public static RSAPublicKey getPublicKey(final String modulus, final String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RSAPrivateKey getPrivateKey(final String modulus, final String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(final String cleartext, final Key key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        return new String(encrypt(cleartext.getBytes(UTF_8), key), ISO_8859_1);
    }

    public static String decrypt(final String encrypted, final Key key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        return new String(decrypt(encrypted.getBytes(ISO_8859_1), key), UTF_8);
    }

    public static byte[] encrypt(final byte[] in, final Key key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        if (!(key instanceof RSAKey)) {
            throw new InvalidKeyException("param key must be RSAPublicKey or RSAPrivateKey: " + key);
        }
        int keyLen = ((RSAKey) key).getModulus().bitLength() / 8 - 11;
        return digest(in, key, Cipher.ENCRYPT_MODE, keyLen);
    }

    public static void encrypt(final InputStream in, final OutputStream out, final Key key)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        if (!(key instanceof RSAKey)) {
            throw new InvalidKeyException("param key must be RSAPublicKey or RSAPrivateKey: " + key);
        }
        int keyLen = ((RSAKey) key).getModulus().bitLength() / 8 - 11;
        digest(in, out, key, Cipher.ENCRYPT_MODE, keyLen);
    }

    public static byte[] decrypt(final byte[] in, final Key key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        if (!(key instanceof RSAKey)) {
            throw new InvalidKeyException("param key must be RSAPublicKey or RSAPrivateKey: " + key);
        }
        int keyLen = ((RSAKey) key).getModulus().bitLength() / 8;
        return digest(in, key, Cipher.DECRYPT_MODE, keyLen);
    }

    public static void decrypt(final InputStream in, final OutputStream out, final Key key)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        if (!(key instanceof RSAKey)) {
            throw new InvalidKeyException("param key must be RSAPublicKey or RSAPrivateKey: " + key);
        }
        int keyLen = ((RSAKey) key).getModulus().bitLength() / 8;
        digest(in, out, key, Cipher.DECRYPT_MODE, keyLen);
    }

    private static byte[] digest(final byte[] in,
                                 final Key key,
                                 final int cipherMode,
                                 final int keyLen) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException,
            InvalidKeyException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(in.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(in);
        try {
            digest(bis, bos, key, cipherMode, keyLen);
            b = bos.toByteArray();
        }
        finally {
            bis.close();

            bos.flush();
            bos.close();
        }
        return b;
    }

    private static void digest(final InputStream in,
                               final OutputStream out,
                               final Key key,
                               final int cipherMode,
                               final int keyLen) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException,
            InvalidKeyException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(cipherMode, key);

        final byte[] buf = new byte[keyLen];
        int n = -1;
        while ((n = in.read(buf, 0, keyLen)) != -1) {
            out.write(cipher.doFinal(buf, 0, n));
        }
    }

    public static String wrapKey(final String strKey, final Key publicKey)
            throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException {
        return new String(wrapKey(strKey.getBytes(ISO_8859_1), publicKey), ISO_8859_1);
    }

    /**
     * 加密秘钥
     * 
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     */
    public static byte[] wrapKey(final Key key, final Key publicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] wrappedKey = cipher.wrap(key);
        return wrappedKey;
    }

    /**
     * 加密秘钥
     * 
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     */
    public static byte[] wrapKey(final byte[] keyBytes, final Key publicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException {
        Key key = new SecretKeySpec(keyBytes, "RSA");
        return wrapKey(key, publicKey);
    }

    /**
     * 解密秘钥
     * 
     * @param wrapedKeyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public static String unwrapKey(final String wrapedKey, final Key privateKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException {
        byte[] b = unwrapKey(wrapedKey.getBytes(ISO_8859_1), privateKey);
        return new String(b, ISO_8859_1);
    }

    /**
     * 解密秘钥
     * 
     * @param wrapedKeyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] unwrapKey(final byte[] wrapedKeyBytes, final Key privateKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        Key key = cipher.unwrap(wrapedKeyBytes, "RSA", Cipher.SECRET_KEY);
        return key.getEncoded();
    }

}