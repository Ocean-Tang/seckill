package com.study.seckill.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.seckill.pojo.User;
import com.study.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

/**
 * 生成用户，获取用户登录状态
 * @author 黄灿杰
 * @date 2021/10/11
 */
public class UserUtil {

    public static void createUser(long startId, int count) throws SQLException {

        ArrayList<User> users = new ArrayList<>(count);
        // 生成用户
        for (int i = 0; i < count; ++i) {
            String id = String.valueOf(startId + i);
            String password = Md5Util.inputPassToDbPass("123456", "1a2b3c4d");
            User user = new User(id, "user" + i, password, "1a2b3c4d", null,
                    new Date(System.currentTimeMillis()), null, null);
            users.add(user);
        }

        System.out.println("========= create user ========");

        // 插入数据库
        Connection conn = DriverManager.getConnection("jdbc:mysql://47.102.208.253:3306/seckill",
                "root", "4FCIW2zs");
        String sql = "insert into t_user(id, nickname, password,slat, register_date) values(?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for(int i = 0; i < users.size(); ++i) {
            User user = users.get(i);
            ps.setString(1, user.getId());
            ps.setString(2, user.getNickname());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getSlat());
            ps.setDate(5, user.getRegisterDate());
            ps.addBatch();
            System.out.println("========= add user " + user.getId() + " ========");
        }
        ps.executeBatch();
        ps.close();
        conn.close();
        System.out.println("========= insert into db over ========");
    }

    public static void getUsersCookie (long startId, int count) throws Exception {
        ArrayList<User> users = new ArrayList<>(count);
        // 生成用户
        for (int i = 0; i < count; ++i) {
            String id = String.valueOf(startId + i);
            String password = Md5Util.inputPassToDbPass("123456", "1a2b3c4d");
            User user = new User(id, "user" + i, password, "1a2b3c4d", null,
                    new Date(System.currentTimeMillis()), null, null);
            users.add(user);
        }

        // 登录，获取cookie
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\阿杰\\Desktop\\config.config");
        if(file.exists()) {
            file.delete();
        }

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" +Md5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();

            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");

    }

    public static void main(String[] args) throws Exception {

        /*createUser(13000000000L, 5000);*/
        getUsersCookie(13000000000L, 5000);

    }

}
