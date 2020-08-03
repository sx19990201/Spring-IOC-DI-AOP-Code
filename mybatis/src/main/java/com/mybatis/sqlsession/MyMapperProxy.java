package com.mybatis.sqlsession;

import com.mybatis.config.Function;
import com.mybatis.config.MapperBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class MyMapperProxy implements InvocationHandler {

    private MySqlSession mySqlSession;
    private MyConfiguration myConfiguration;
    //前面在初始化的时候mySqlSession以及myConfiguration就已经通过饿汉模式加载了
    public MyMapperProxy(MyConfiguration myConfiguration, MySqlSession mySqlSession) {
        this.myConfiguration = myConfiguration;
        this.mySqlSession = mySqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //读取UserMapper.xml文件
        MapperBean readMapper = myConfiguration.readMapper("mabtis/UserMapper.xml");
        //是否对应xml文件接口
        if (!method.getDeclaringClass().getName().equals(readMapper.getInterfaceName())){
            return null;
        }
        //获取UserMapper的所有方法
        List<Function> list = readMapper.getList();
        if (null !=list || 0!= list.size()){
            for (Function function : list){
                //id是否和接口方法名一样
                if (method.getName().equals(function.getFuncName())){
                    //如果是一样的则可以开始执行方法
                    return mySqlSession.selectOne(function.getSql(),String.valueOf(args[0]));
                }
            }
        }
        return null;
    }
}
