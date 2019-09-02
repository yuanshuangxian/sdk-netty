package com.fqserver.lang.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fqserver.lang.codec.Base64;
import com.fqserver.lang.util.Chars;



public class Aes {

    public static final String UTF_8 = "UTF-8";
    public static final int BUFFER_SIZE = 1024;

    public static final String MSG_AES_PASSWORD = "xyzG7FBook9OverZ";

    public static final String ALGORITHM_128 = "AES/ECB/PKCS5Padding";

    private static final int AN_KEY_MAX = Chars.ALPHA_NUM.length - 1;
    private static final int VC_KEY_MAX = Chars.VISIBAL_CHAR.length - 1;

    /**
     * 生成纯字母和数字的AES对称秘钥 128bit
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String genAlphaNumKey() throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        final StringBuilder key = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int s = ThreadLocalRandom.current().nextInt(AN_KEY_MAX);
            key.append(Chars.ALPHA_NUM[s]);
        }
        return key.toString();
    }

    /**
     * 生成可见字符的AES对称秘钥 128bit
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String genVisibleCharKey() throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        final StringBuilder key = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int s = ThreadLocalRandom.current().nextInt(VC_KEY_MAX);
            key.append(Chars.VISIBAL_CHAR[s]);
        }
        return key.toString();
    }

    /**
     * 生成AES对称秘钥
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String genStringKey() throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        return new String(genKey().getEncoded());
    }

    /**
     * 生成AES对称秘钥 默认是128bit
     * 
     * @throws NoSuchAlgorithmException
     */
    public static Key genKey() throws NoSuchAlgorithmException {

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(new SecureRandom());

        return keygen.generateKey();
    }

    public static byte[] encrypt(final byte[] key, final String cleartext)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        return encrypt(key, cleartext.getBytes(UTF_8));
    }

    public static byte[] encrypt(final byte[] key, final byte[] in) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        return digest(key, in, Cipher.ENCRYPT_MODE);
    }

    public static void encrypt(final byte[] key, final InputStream in, final OutputStream out)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        digest(key, in, out, Cipher.ENCRYPT_MODE);
    }

    private static byte[] digest(final byte[] key, final byte[] in, final int mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(in.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(in);
        try {
            digest(key, bis, bos, mode);
            b = bos.toByteArray();
        }
        finally {
            bis.close();

            bos.flush();
            bos.close();
        }
        return b;
    }

    private static void digest(final byte[] key,
                               final InputStream in,
                               final OutputStream out,
                               final int mode) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        /* AES算法 */
        SecretKey k = new SecretKeySpec(key, "AES");// 获得密钥
        /* 获得一个私钥加密类Cipher，DESede-》AES算法，ECB是加密模式，PKCS5Padding是填充方式 */
        Cipher cipher = Cipher.getInstance(ALGORITHM_128);
        cipher.init(mode, k); // 设置工作模式为加密模式，给出密钥

        final byte[] buf = new byte[BUFFER_SIZE];
        int n = -1;
        while ((n = in.read(buf, 0, BUFFER_SIZE)) != -1) {
            out.write(cipher.update(buf, 0, n)); // 正式执行加密操作
        }
        out.write(cipher.doFinal()); // 非常重要, 最后的数据需要doFinal写入, 否则数据不完整
    }

    public static byte[] decrypt(final byte[] key, final String encrypted)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        return decrypt(key, encrypted.getBytes(UTF_8));
    }

    public static byte[] decrypt(final byte[] key, final byte[] in) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        return digest(key, in, Cipher.DECRYPT_MODE);
    }

    public static void decrypt(final byte[] key, final InputStream in, final OutputStream out)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        digest(key, in, out, Cipher.DECRYPT_MODE);
    }

    public static String encryptBase64(final String key, final String cleartext)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] rawKey = key.getBytes();

        byte[] b = encrypt(rawKey, cleartext.getBytes(UTF_8));
        return Base64.encodeToString(b);
    }

    public static String decryptBase64(final String key, final String encrypted)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] rawKey = key.getBytes();
        byte[] enc = Base64.decode(encrypted.getBytes(UTF_8));

        byte[] b = decrypt(rawKey, enc);
        return new String(b, UTF_8);
    }

    public static byte[] encrypt(final String key, final byte[] in) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        byte[] rawKey = key.getBytes();
        return encrypt(rawKey, in);
    }

    public static byte[] decrypt(final String key, final byte[] in) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        byte[] rawKey = key.getBytes();
        return decrypt(rawKey, in);
    }
}