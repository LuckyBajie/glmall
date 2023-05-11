package com.glmall.common.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.NoIvGenerator;

public class JasyptUtils {
    public static void main(String[] args) {
         PBEWITHHMACSHA512ANDAES_256();
//        PBEWithMD5AndDES();

    }

    private static void PBEWITHHMACSHA512ANDAES_256() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        // 加密方式
        // PBEWithMD5AndDES
        // PBEWITHHMACSHA512ANDAES_256
        // config.setAlgorithm("PBEWithMD5AndDES");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        // 盐值
        config.setPassword("glmall-salt20230405");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        // 加密明文
        String encryptValue1 = encryptor.encrypt("root");
        String encryptValue2 = encryptor.encrypt("root");
        System.out.println(encryptValue1);
        System.out.println(encryptValue2);
        // 解密密文
        String decryptValue1 = encryptor.decrypt(encryptValue1);
        String decryptValue2 = encryptor.decrypt(encryptValue2);
        System.out.println(decryptValue1);
        System.out.println(decryptValue2);
    }

    /**
     * 这个加密方式不好用
     */
    public static void PBEWithMD5AndDES() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        standardPBEStringEncryptor.setPassword("glmall-salt2023040");
        standardPBEStringEncryptor.setIvGenerator(new NoIvGenerator());
        String root1 = standardPBEStringEncryptor.encrypt("root");
        String root2 = standardPBEStringEncryptor.encrypt("root");
        System.out.println(root1);
        System.out.println(root2);

        // 解密密文
        String decryptValue1 = standardPBEStringEncryptor.decrypt(root1);
        String decryptValue2 = standardPBEStringEncryptor.decrypt(root2);
        System.out.println(decryptValue1);
        System.out.println(decryptValue2);
    }
}
