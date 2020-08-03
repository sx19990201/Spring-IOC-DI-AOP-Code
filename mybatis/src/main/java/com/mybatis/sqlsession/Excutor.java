package com.mybatis.sqlsession;

/**
 * 执行器接口
 */
public interface Excutor {
    //执行器需要调用的方法
    public <T> T query(String statement,Object parameter);
}
