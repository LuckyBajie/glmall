package com.glmall.common.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtils {
    public static void main(String[] args) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        // 加密方式
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
}
