package com.nwld.defi.tools.util;


import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

    public static String sha256Encrypt(String src) {
        return sha256EncryptHex(src);
    }

    public static String sha256EncryptBase64(String src) {
        String des = null;
        byte[] bt = src.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            des = Base64.encodeToString(md.digest(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return des;
    }

    public static String sha256EncryptHex(String src) {
        String des = null;
        byte[] bt = src.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            des = toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return des;
    }

    private final static String HEX = "0123456789ABCDEF";
    private static final int keyLenght = 16;
    private static final String defaultV = "0";

    /**
     * 加密
     *
     * @param key 密钥
     * @param src 加密文本
     * @return
     */
    public static String encrypt(String key, String src) {
        return encryptBySha256Hex(sha256Encrypt(key), src);
    }

    public static String encryptKey(String key, String src) {
        try {
            // /src = Base64.encodeToString(src.getBytes(), Base64.DEFAULT);
            byte[] rawKey = toMakekey(key, keyLenght, defaultV).getBytes();// key.getBytes();
            byte[] result = encrypt(rawKey, src.getBytes("utf-8"));
            // result = Base64.encode(result, Base64.DEFAULT);
            return toHex(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 加密
     *
     * @param key 密钥
     * @param src 加密文本
     * @return
     */
    public static String encryptBySha256Hex(String key, String src) {
        try {
            byte[] rawKey = toByte(key);
            byte[] result = encrypt(rawKey, src.getBytes("utf-8"));
            // result = Base64.encode(result, Base64.DEFAULT);
            return toHex(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 加密
     *
     * @param key 密钥
     * @param src 加密文本
     * @return
     */
    public static String encrypt2Java(String key, String src) throws Exception {
        // /src = Base64.encodeToString(src.getBytes(), Base64.DEFAULT);
        byte[] rawKey = toMakekey(key, keyLenght, defaultV).getBytes();// key.getBytes();
        byte[] result = encrypt2Java(rawKey, src.getBytes("utf-8"));
        // result = Base64.encode(result, Base64.DEFAULT);
        return toHex(result);
    }

    /**
     * 解密
     *
     * @param key       密钥
     * @param encrypted 待揭秘文本
     * @return
     */
    public static String decrypt(String key, String encrypted) {
        return decryptByShaHex(sha256Encrypt(key), encrypted);
    }

    public static String decryptKey(String key, String encrypted) {
        try {
            byte[] rawKey = toMakekey(key, keyLenght, defaultV).getBytes();// key.getBytes();
            byte[] enc = toByte(encrypted);
            // enc = Base64.decode(enc, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            // /result = Base64.decode(result, Base64.DEFAULT);
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decryptByShaHex(String key, String encrypted) {
        LogUtil.e("decryptByShaHex", "key=" + key + ",encrypted=" + encrypted);
        try {
            byte[] rawKey = toByte(key);// key.getBytes();
            byte[] enc = toByte(encrypted);
            // enc = Base64.decode(enc, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            // /result = Base64.decode(result, Base64.DEFAULT);
            String dec = new String(result, "utf-8");
            LogUtil.e("dec", dec);
            return dec;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 密钥key ,默认补的数字，补全16位数，以保证安全补全至少16位长度,android和ios对接通过
     *
     * @param str
     * @param strLength
     * @param val
     * @return
     */
    private static String toMakekey(String str, int strLength, String val) {

        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(str).append(val);
                str = buffer.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    /**
     * 真正的加密过程
     * 1.通过密钥得到一个密钥专用的对象SecretKeySpec
     * 2.Cipher 加密算法，加密模式和填充方式三部分或指定加密算 (可以只用写算法然后用默认的其他方式)Cipher.getInstance("AES");
     *
     * @param key
     * @param src
     * @return
     */
    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    /**
     * 真正的加密过程
     *
     * @param key
     * @param src
     * @return
     */
    private static byte[] encrypt2Java(byte[] key, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    /**
     * 真正的解密过程
     *
     * @param key
     * @param encrypted
     * @return
     */
    private static byte[] decrypt(byte[] key, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) throws Exception {
        return toHex(txt.getBytes("utf-8"));
    }

    public static String fromHex(String hex) {
        if (!StringUtil.isEmpty(hex) && (hex.toLowerCase().startsWith("0x"))) {
            hex = hex.substring(2);
        }
        try {
            return new String(toByte(hex));
        } catch (Exception e) {
            e.printStackTrace();
            return hex;
        }
    }


    /**
     * 把16进制转化为字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] toByte(String hexString) throws Exception {
        if (!StringUtil.isEmpty(hexString) && (hexString.toLowerCase().startsWith("0x"))) {
            hexString = hexString.substring(2);
        }
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }


    /**
     * 二进制转字符,转成了16进制
     * 0123456789abcdefg
     *
     * @param buf
     * @return
     */
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    public static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    public static Cipher initAESCipher(String sKey, int cipherMode) {
        // 创建Key gen
        // KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        try {
            /*
             * keyGenerator = KeyGenerator.getInstance("AES");
             * keyGenerator.init(128, new SecureRandom(sKey.getBytes()));
             * SecretKey secretKey = keyGenerator.generateKey(); byte[]
             * codeFormat = secretKey.getEncoded(); SecretKeySpec key = new
             * SecretKeySpec(codeFormat, "AES"); cipher =
             * Cipher.getInstance("AES"); //初始化 cipher.init(cipherMode, key);
             */
            byte[] rawKey = toMakekey(sKey, keyLenght, defaultV).getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    /**
     * 对文件进行AES加密
     *
     * @param sourceFile
     * @param sKey
     * @return
     */
    public static File encryptFile(File sourceFile, String toFile, String dir, String sKey) {
        // 新建临时加密文件
        File encrypfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            encrypfile = new File(dir + toFile);
            outputStream = new FileOutputStream(encrypfile);
            Cipher cipher = initAESCipher(sKey, Cipher.ENCRYPT_MODE);
            // 以加密流写入文件
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  // To change body of catch statement use
                // File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return encrypfile;
    }

    /**
     * AES方式解密文件
     *
     * @param sourceFile
     * @return
     */
    public static File decryptFile(File sourceFile, String toFile, String dir, String sKey) {
        File decryptFile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            decryptFile = new File(dir + toFile);
            Cipher cipher = initAESCipher(sKey, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(decryptFile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();  // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  // To change body of catch statement use
                // File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return decryptFile;
    }

    public static String keccak256(String data) {
        byte[] input = data.getBytes();
        org.bouncycastle.jcajce.provider.digest.Keccak.DigestKeccak kecc = new org.bouncycastle.jcajce.provider.digest.Keccak.Digest256();
        kecc.update(input, 0, input.length);
        byte[] hash = kecc.digest();
        return toHex(hash).toLowerCase();
    }

    public static String getRandomPwd() {
        String a = "ADGJMPSVY";
        String b = "13579";
        String c = "fkv";
        String d = "257";
        StringBuilder s = new StringBuilder();
        Random random = new Random();
        int r = random.nextInt(10000);
        s.append(a.charAt(r % a.length()));
        r = random.nextInt(500);
        s.append(b.charAt(r % b.length()));
        r = random.nextInt(300);
        s.append(c.charAt(r % c.length()));
        r = random.nextInt(100);
        s.append(d.charAt(r % d.length()));
        return s.toString();
    }

    public static String base64(String src) {
        if (StringUtil.isEmpty(src)) {
            return "";
        }
        byte[] bytes = src.getBytes();
        //将byte[]转为base64
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64;
    }

}