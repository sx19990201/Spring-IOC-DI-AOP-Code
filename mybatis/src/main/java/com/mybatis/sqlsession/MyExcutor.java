package com.mybatis.sqlsession;

import com.mybatis.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 该类是一个执行器类，他实现了执行器接口中需要调用的方法
 */
public class MyExcutor implements Excutor {

    private MyConfiguration xmlConfiguration = new MyConfiguration();

    //开始操作sql语句
    @Override
    public <T> T query(String sql, Object parameter) {
        //获取数据库连接
        Connection connection = getConnection();
        ResultSet rst = null;
        PreparedStatement ps = null;
        try{
            //传入带占位符的sql语句，
            ps = connection.prepareStatement(sql);
            //如果数据库的字段类型是Char，varChar的，PreparedStatement插入的时候会自动在数据后面加空格
            //setString 方法的作用就是设置第几个 参数，去掉空格
            ps.setString(1,parameter.toString());
            //获取查询数据库获得的结果集
            rst = ps.executeQuery();
            User u = new User();
            //遍历结果集
            while (rst.next()){
                u.setId(rst.getString(1));
                u.setUserName(rst.getString(2));
                u.setPassWord(rst.getString(3));
            }
            return (T) u;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (rst!=null){
                    rst.close();
                }
                if (ps!=null){
                    ps.close();
                }
                if (connection!=null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private Connection getConnection(){
        try{
            //读取config.xml配置文件
            Connection conn = xmlConfiguration.build("mabtis/config.xml");
            return conn;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
