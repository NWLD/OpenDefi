package com.nwld.defi.tools.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    /**
     * 加密
     *
     * @param key 密钥
     * @param src 加密文本
     * @return
     */
    public static String encryptByHex(String key, String src) {
        try {
            byte[] rawKey = toByte(key);
            byte[] iv = getIv();
            byte[] result = encrypt(rawKey, toByte(src), iv);
            return SecurityUtil.toHex(iv) + SecurityUtil.toHex(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decryptByHex(String key, String encrypted) {
        try {
            byte[] rawKey = toByte(key);// key.getBytes();
            byte[] datas = toByte(encrypted);
            byte[] iv = new byte[16];
            System.arraycopy(datas, 0, iv, 0, 16);
            int encLen = datas.length - 16;
            byte[] enc = new byte[encLen];
            // enc = Base64.decode(enc, Base64.DEFAULT);
            System.arraycopy(datas, 16, enc, 0, encLen);
            byte[] result = decrypt(rawKey, enc, iv);
            // /result = Base64.decode(result, Base64.DEFAULT);
            String dec = SecurityUtil.toHex(result);
            LogUtil.e("dec", dec);
            return dec;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
    private static byte[] encrypt(byte[] key, byte[] src, byte[] iv) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    /**
     * 把16进制转化为字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] toByte(String hexString) throws Exception {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    /**
     * 真正的解密过程
     *
     * @param key
     * @param encrypted
     * @return
     */
    private static byte[] decrypt(byte[] key, byte[] encrypted, byte[] iv) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] getIv() {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        byte[] iv = sr.generateSeed(16);
        LogUtil.e("iv", SecurityUtil.toHex(iv));
        return iv;
    }


    // 解密
    public static String Dec(String encData, String AesKey, String ivParameter) throws Exception {
        //获取AESkey
        Key secretKey = new SecretKeySpec(AesKey.getBytes(), "AES"); //密钥
        //获取加密工具
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //初始化加密工具
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivParameter.getBytes()));
        //还原文本框的base64文本为密文
        byte[] bytes = android.util.Base64.decode(encData, android.util.Base64.DEFAULT);
        //把密文解密为明文
        byte[] bytes1 = cipher.doFinal(bytes);
        return new String(bytes1);
    }

    // 加密
    public static String Enc(String srcData, String AesKey, String ivParameter) throws Exception {
        //获取AESkey
        Key secretKey = new SecretKeySpec(AesKey.getBytes(), "AES"); //密钥
        //获取加密工具
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //初始化加密工具
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivParameter.getBytes()));
        //放入我们要加密的内容 并加密
        byte[] bytes = cipher.doFinal(srcData.getBytes());
        //得到的字节在进行Base64换算
        return new String(android.util.Base64.encode(bytes, android.util.Base64.DEFAULT));
    }
}
