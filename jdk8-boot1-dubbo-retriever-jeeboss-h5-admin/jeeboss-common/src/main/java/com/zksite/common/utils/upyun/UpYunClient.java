package com.zksite.common.utils.upyun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.core.env.Environment;

import com.zksite.common.utils.SpringContextUtil;


public class UpYunClient {

    private static String USER_NAME;
    private static String PASSWORD;
    private static String BUCKETNAME;

    private static String domain;

    private static UpYun upYun;

    private static Environment environment = SpringContextUtil.getBean(Environment.class);

    static {
        USER_NAME = environment.getProperty("upyun_user_name");
        PASSWORD = environment.getProperty("upyun_password");
        BUCKETNAME = environment.getProperty("upyun_bucketname");
        domain = environment.getProperty("upyun_domain");
        upYun = new UpYun(BUCKETNAME, USER_NAME, PASSWORD);
    }

    public static String upload(String fileExt, InputStream inputStream) {
        try {
            String path = randomPath() + fileExt;
            boolean flag = upYun.writeFile(path, input2byte(inputStream), true);
            if (flag) {
                return domain + path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String upload(String fileExt, byte[] bytes) {
        String path = randomPath() + fileExt;
        boolean flag = upYun.writeFile(path, bytes, true);
        if (flag) {
            return domain + path;
        }
        return null;
    }

    public static String randomPath() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            String newFileName = UUID.randomUUID().toString().replace("-", "");
            String name1 = Integer.toHexString((newFileName.hashCode()) & 0xf);// 一级目录
            String name2 = Integer.toHexString(((newFileName.hashCode()) & 0xf0) >> 4);// 二级目录
            stringBuilder.append(name1).append(name2).append("/");
        }
        return stringBuilder.toString();
    }

    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }
}
