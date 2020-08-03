package com.mybatis.config;

/**
 * 定义Function类 封装映射文件所需要的各种数据
 * Function 对象包括sql的类型，方法名，sql语句、返回类型和参数类型
 */
public class Function {
    //Sql语句的类型 也就是sql语句的前缀  比如select  insert  update  delete
    private String sqlType;
    //映射的方法名
    private String funcName;
    //映射的 sql语句
    private String sql;
    //返回值类型
    private Object resultType;
    //入参类型
    private String parameterType;

    public Function(String sqlType, String funcName, String sql, Object resultType, String parameterType) {
        this.sqlType = sqlType;
        this.funcName = funcName;
        this.sql = sql;
        this.resultType = resultType;
        this.parameterType = parameterType;
    }

    public Function() {
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object getResultType() {
        return resultType;
    }

    public void setResultType(Object resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }
}
