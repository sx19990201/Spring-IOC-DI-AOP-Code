package com.mybatis.sqlsession;

import com.mybatis.config.Function;
import com.mybatis.config.MapperBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  MyBatis的核心配置类Configuration，这个类主要做的就是读取配置文件，
 *
 * build方法，就是读取配置文件后，获取数据库连接
 * readMapper方法，读取mapper配置文件后，将里面的各种数据封装到mapperbean中
 */
public class MyConfiguration {

    //启动类加载器
    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    /**
     * 读取xml信息并处理
     *  读取config.xml文件
     * @param resources
     * @return
     */
    public Connection build(String resources) {
        try {
            //读取resources配置文件  这里传进来的是config。xml 也就是配置数据源的配置文件
            InputStream stream = loader.getResourceAsStream(resources);
            //创建xml文件解析器
            SAXReader reader = new SAXReader();
            //开始读取xml配置文件
            Document document = reader.read(stream);
            //获取根节点
            Element root = document.getRootElement();
            //获取数据库连接
            return evalDataSource(root);
        } catch (DocumentException | ClassNotFoundException e) {
            throw new RuntimeException("error occurd while evaling xml" + resources);
        }
    }

    //读取配置文件的子节点，并将这些子节点的值作为参数获取数据库连接
    private Connection evalDataSource(Element node) throws ClassNotFoundException {
        if (!node.getName().equals("database")) {
            throw new RuntimeException("root should be <database>");
        }
        String driverClassName = null;
        String url = null;
        String username = null;
        String password = null;
        //获取属性节点
        for (Object item : node.elements("property")) {
            Element i = (Element) item;
            String value = getValue(i);
            String name = i.attributeValue("name");
            if (name == null || value == null) {
                throw new RuntimeException("[database]:<property> should contain name and value");
            }
            //赋值
            switch (name) {
                case "url":
                    url = value;
                    break;
                case "username":
                    username = value;
                    break;
                case "password":
                    password = value;
                    break;
                case "driverClassName":
                    driverClassName = value;
                    break;
                default:
                    throw new RuntimeException("[database]:<property> unknown name");
            }
        }
        Class.forName(driverClassName);
        Connection conn = null;
        try {
            //连接数据库，获取连接
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    //获取property属性的值，如果有value的值，则读取  没有设置value，则读取内容
    private String getValue(Element node) {
        return node.hasContent() ? node.getText() : node.attributeValue("value");
    }

    /**
     * java.lang.SuppressWarnings是J2SE5.0中标准的Annotation之一。
     * 可以标注在类、字段、方法、参数、构造方法，以及局部变量上。
     * value -将由编译器在注释的元素中取消显示的警告集。允许使用重复的名称。忽略第二个和后面出现的名称。
     * 出现未被识别的警告名不是错误：编译器必须忽略无法识别的所有警告名。
     * 但如果某个注释包含未被识别的警告名，那么编译器可以随意发出一个警告。
     * 各编译器供应商应该将它们所支持的警告名连同注释类型一起记录。
     * 鼓励各供应商之间相互合作，确保在多个编译器中使用相同的名称。
     * @param path
     * @return
     */
    @SuppressWarnings("rawtypes")
    public MapperBean readMapper(String path) {
        MapperBean mapper = new MapperBean();
        try {
            //读取path的数据
            InputStream stream = loader.getResourceAsStream(path);
            //创建XML解析器
            SAXReader reader = new SAXReader();
            //从xml文件获取数据
            Document document = reader.read(stream);
            //获取根节点
            Element root = document.getRootElement();
            //把mapper节点的namespace值存为接口名  （完全限定名）
            mapper.setInterfaceName(root.attributeValue("namespace").trim());
            //用来存储方法的list
            List<Function> list = new ArrayList<>();
            //遍历根节点下所有节点
            for (Iterator rootIter = root.elementIterator(); rootIter.hasNext(); ) {
                //用来存储一条方法的信息  function定义了Mabatis。xml文件需要的各种参数
                //sql语句 ，sql语句的类型，传入参数，返回参数，接口方法名
                Function fun = new Function();
                Element e = (Element) rootIter.next();
                //获取sql语句的类型
                String sqlType = e.getName().trim();
                //获取方法名
                String funcName = e.attributeValue("id").trim();
                //获取sql语句
                String sql = e.getText().trim();
                //获取返回参数
                String resultType = e.attributeValue("resultType").trim();
                //设置sql类型
                fun.setSqlType(sqlType);
                //设置方法名
                fun.setFuncName(funcName);
                Object newInstance = null;
                try {
                    newInstance = Class.forName(resultType).newInstance();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                //设置返回参数
                fun.setResultType(newInstance);
                //设置sql语句
                fun.setSql(sql);
                //将保存的数据对象方法存到list集合中 ，该list集合存放方法
                list.add(fun);
            }
            //将全部方法给mapperbean
            mapper.setList(list);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return mapper;
    }

}
