//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xdmy.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

public class CodecUtil {
    private static final Logger logger = LogManager.getLogger(CodecUtil.class);
    private static String DEFAULT_ENCODE = "UTF-8";

    public CodecUtil() {
    }

    public static String urlEncode(String str) {
        return urlEncode(str, DEFAULT_ENCODE);
    }

    public static String urlEncode(String str, String enc) {
        try {
            String target = URLEncoder.encode(str, enc);
            return target;
        } catch (Exception var4) {
            logger.error("编码出错！", var4);
            throw new RuntimeException(var4);
        }
    }

    public static String urlDecode(String str) {
        return urlDecode(str, DEFAULT_ENCODE);
    }

    public static String urlDecode(String str, String enc) {
        try {
            String target = URLDecoder.decode(str, enc);
            return target;
        } catch (Exception var4) {
            logger.error("解码出错！", var4);
            throw new RuntimeException(var4);
        }
    }

    public static String encodeBase64(byte[] bt) {
        try {
            String target = Base64.encodeBase64String(bt);
            return target;
        } catch (Exception var3) {
            logger.error("编码出错！", var3);
            throw new RuntimeException(var3);
        }
    }

    public static String encodeBase64(String str) {
        String target = Base64.encodeBase64URLSafeString(str.getBytes());
        return target;
    }

    public static String decodeBase64(String str) {
        return decodeBase64(str, DEFAULT_ENCODE);
    }

    public static String decodeBase64(String str, String enc) {
        try {
            String target = new String(Base64.decodeBase64(str), enc);
            return target;
        } catch (UnsupportedEncodingException var4) {
            logger.error("解码出错！", var4);
            throw new RuntimeException(var4);
        }
    }

    public static String decodeBase64(byte[] bt) {
        try {
            String target = new String(Base64.decodeBase64(bt));
            return target;
        } catch (Exception var3) {
            logger.error("解码出错！", var3);
            throw new RuntimeException(var3);
        }
    }

    public static byte[] decodeBase64Byte(byte[] bt) {
        try {
            byte[] target = Base64.decodeBase64(bt);
            return target;
        } catch (Exception var3) {
            logger.error("解码出错！", var3);
            throw new RuntimeException(var3);
        }
    }

    public static byte[] decodeBase64Byte(String str) {
        try {
            byte[] target = Base64.decodeBase64(str);
            return target;
        } catch (Exception var3) {
            logger.error("解码出错！", var3);
            throw new RuntimeException(var3);
        }
    }

    public static String encryptMD5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String encryptSHA(String str) {
        return DigestUtils.sha1Hex(str);
    }

    public static String createRandom(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    public static String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
