package com.study.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Component
public class Md5Util {

    private static final String SALT = "1a2b3c4d";

    /**
     * 对src字符串进行md5加密
     *
     * @param src 输入的字符串
     * @return md5加密后的字符串
     */
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 对前端传过来的用户密码进行加密
     *
     * @param inputPass 用户输入的密码
     * @return 加密后的密码
     */
    public static String inputPassToFormPass(String inputPass) {
        String src = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(src);
    }

    /**
     * 对第一次加密过的密码在进行一次MD5加密
     *
     * @param formPass 第一次加密的密码
     * @param salt     随机生成的 salt 值
     * @return 二次加密后的密码
     */
    public static String formPassToDbPass(String formPass, String salt) {
        String src = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(src);
    }

    /**
     * 返回二次加密的密码
     *
     * @param inputPass 用户传过来的密码
     * @param salt      随机生成的salt值
     * @return 二次加密后的密码
     */
    public static String inputPassToDbPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDbPass(formPass, salt);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {

        String inputPass = "123456";
        //ce21b747de5af71ab5c2e20ff0a60eea
        System.out.println(inputPassToFormPass(inputPass));
        System.out.println(formPassToDbPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDbPass(inputPass,"1a2b3c4d" ));

    }
}
