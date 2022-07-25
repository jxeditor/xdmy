//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xdmy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Md5Util {
    private static final Logger logger = LogManager.getLogger(Md5Util.class);
    protected static final String CONTENT_CHARSET = "UTF-8";
    protected static final String HMAC_ALGORITHM = "HmacSHA1";
    protected static final String ETT_SECURITY_KEY = "*BIGDATA#SUPERBRAIN#2018*";

    public Md5Util() {
    }

    public static String encodeUrl(String input) {
        try {
            return URLEncoder.encode(input, CONTENT_CHARSET).replace("+", "%20").replace("*", "%2A");
        } catch (UnsupportedEncodingException var2) {
            logger.error("编码出错！", var2);
            throw new RuntimeException(var2);
        }
    }

    public static String makeSig(String method, String url_path, HashMap<String, String> params, String secret) {
        String sig = null;

        try {
            Mac e = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), e.getAlgorithm());
            e.init(secretKey);
            String mk = makeSource(method, url_path, params);
            byte[] hash = e.doFinal(mk.getBytes(CONTENT_CHARSET));
            sig = new String(CodecUtil.encodeBase64(hash));
            return sig;
        } catch (NoSuchAlgorithmException var9) {
            logger.error("NoSuchAlgorithmException！", var9);
            throw new RuntimeException(var9);
        } catch (UnsupportedEncodingException var10) {
            logger.error("UnsupportedEncodingException！", var10);
            throw new RuntimeException(var10);
        } catch (InvalidKeyException var11) {
            logger.error("InvalidKeyException！", var11);
            throw new RuntimeException(var11);
        }
    }

    public static String makeSigSimple(String url_path, HashMap<String, String> params) {
        return makeSigSimple(url_path, params, ETT_SECURITY_KEY);
    }

    public static String makeSigSimple(String url_path, HashMap<String, String> params, String secret) {
        String sig = null;
        String mk = makeSourceSimple(url_path, params);
        String md5 = CodecUtil.encryptMD5(mk + secret);
        sig = CodecUtil.encodeBase64(md5);
        return sig;
    }

    public static String makeSource(String method, String url_path, HashMap<String, String> params) {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(url_path)).append("&");
        StringBuilder buffer2 = new StringBuilder();

        for(int i = 0; i < keys.length; ++i) {
            buffer2.append(keys[i]).append("=").append((String)params.get(keys[i]));
            if(i != keys.length - 1) {
                buffer2.append("&");
            }
        }

        buffer.append(encodeUrl(buffer2.toString()));
        return buffer.toString();
    }

    public static String makeSourceSimple(String url_path, HashMap<String, String> params) {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(url_path).append("&");
        StringBuilder buffer2 = new StringBuilder();

        for(int i = 0; i < keys.length; ++i) {
            buffer2.append(keys[i]).append("=").append(encodeUrl((String)params.get(keys[i])));
            if(i != keys.length - 1) {
                buffer2.append("&");
            }
        }

        buffer.append(buffer2.toString());
        return buffer.toString();
    }

    public static boolean verifySig(String method, String url_path, HashMap<String, String> params, String secret, String sig) {
        params.remove("sig");
        codePayValue(params);
        String sig_new = makeSig(method, url_path, params, secret);
        return sig_new.equals(sig);
    }

    public static boolean verifySigSimple(String url_path, HashMap<String, String> params, String secret, String sig) {
        params.remove("sig");
        String sig_new = makeSigSimple(url_path, params, secret);
        return sig_new.equals(sig);
    }

    public static void codePayValue(Map<String, String> params) {
        Set keySet = params.keySet();
        Iterator itr = keySet.iterator();

        while(itr.hasNext()) {
            String key = (String)itr.next();
            String value = (String)params.get(key);
            value = encodeValue(value);
            params.put(key, value);
        }

    }

    public static String encodeValue(String s) {
        String rexp = "[0-9a-zA-Z!*\\(\\)]";
        StringBuffer sb = new StringBuffer(s);
        StringBuffer sbRtn = new StringBuffer();
        Pattern p = Pattern.compile(rexp);

        for(int i = 0; i < sb.length(); ++i) {
            char temp = sb.charAt(i);
            String tempStr = String.valueOf(temp);
            Matcher m = p.matcher(tempStr);
            boolean result = m.find();
            if(!result) {
                tempStr = hexString(tempStr);
            }

            sbRtn.append(tempStr);
        }

        return sbRtn.toString();
    }

    private static String hexString(String s) {
        byte[] b = s.getBytes();
        String retStr = "";

        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            retStr = "%" + hex.toUpperCase();
        }

        return retStr;
    }

    public static void main(String[] args) {
        HashMap map = new HashMap();
        map.put("activityId", "986736917");
        map.put("name", "测试");
        String str = getParamSimple("getActivity.do", map);
        System.out.println(str);
        String sig= makeSigSimple("getActivity.do", map);
        System.out.println("sig---------" + sig);
        System.out.println(verifySigSimple("getActivity.do", map, sig));
    }

    public static String getParamSimple(String url_path, HashMap<String, String> params) {
        return getParamSimple(url_path, params, ETT_SECURITY_KEY);
    }

    public static String getParamSimple(String url_path, HashMap<String, String> params, String secret) {
        StringBuffer sf = new StringBuffer();
        sf.append(makeSourceSimple(url_path, params)).append("&sign=").append(makeSigSimple(url_path, params, secret)).toString();
        return sf.toString();
    }

    public static boolean verifySigSimple(String url_path, HashMap<String, String> params, String sig) {
        return verifySigSimple(url_path, params, ETT_SECURITY_KEY, sig);
    }
}
