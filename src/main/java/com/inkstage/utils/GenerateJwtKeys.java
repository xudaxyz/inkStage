package com.inkstage.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * JWT密钥生成器
 * 用于生成PEM格式的RSA密钥对, 存储到指定目录
 */
public class GenerateJwtKeys {

    public static void main(String[] args) throws Exception {
        // 生成RSA密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 创建密钥存储目录
        File keysDir = new File("src/main/resources/jwt-keys");
        if (!keysDir.exists()) {
            boolean result = keysDir.mkdirs();
            if (result) {
                System.out.println("密钥存储目录已创建: " + keysDir.getAbsolutePath());
            } else {
                System.out.println("密钥存储目录创建失败: " + keysDir.getAbsolutePath());
            }
        }

        // 写入私钥到文件
        writeKeyToPemFile(privateKey, keysDir, "private.pem");
        System.out.println("私钥已写入: " + new File(keysDir, "private.pem").getAbsolutePath());

        // 写入公钥到文件
        writeKeyToPemFile(publicKey, keysDir, "public.pem");
        System.out.println("公钥已写入: " + new File(keysDir, "public.pem").getAbsolutePath());

        System.out.println("JWT密钥生成完成！");
    }

    /**
     * 将密钥写入PEM格式的文件
     *
     * @param key      密钥对象
     * @param dir      目录
     * @param fileName 文件名
     * @param <T>      密钥类型
     * @throws IOException IO异常
     */
    private static <T extends Key> void writeKeyToPemFile(T key, File dir, String fileName) throws IOException {
        // 生成Base64编码的密钥
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        // 构建PEM格式的密钥字符串
        String pemKey;
        if (key instanceof PrivateKey) {
            pemKey = "-----BEGIN PRIVATE KEY-----\n" + encodedKey + "\n-----END PRIVATE KEY-----\n";
        } else if (key instanceof PublicKey) {
            pemKey = "-----BEGIN PUBLIC KEY-----\n" + encodedKey + "\n-----END PUBLIC KEY-----\n";
        } else {
            throw new IllegalArgumentException("Unsupported key type: " + key.getClass());
        }

        // 写入文件
        File keyFile = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(keyFile)) {
            fos.write(pemKey.getBytes());
        }
    }
}
