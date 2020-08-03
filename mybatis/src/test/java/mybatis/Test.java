package mybatis;

import com.mybatis.dao.UserMapper;
import com.mybatis.pojo.User;
import com.mybatis.sqlsession.MySqlSession;

public class Test {
    public static void main(String[] args) {

        //加载类，生成mySqlSession的实例和myConfigration的实例交给SqlSession内的Proxy.newProxyInstance代理
        MySqlSession sqlSession = new MySqlSession();

        //将接口与mapper.xml文件关联起来   getMapper(任意类)
        UserMapper mapper =sqlSession.getMapper(UserMapper.class);

        User user = mapper.getUserById("1");

        System.out.println(user.toString());
        /**
         * 大致流程：
         *      创建一个配置类，用于读取配置文件   config.xml 和 userMapper.xml
         *      创建sql工厂，内部实例化一个执行器和一个配置类  同时有一个动态代理方法
         *
         *      将接口与对应的xml文件关联起来：
         *          session使用动态代理，他会生成UserMapper这个类的代理对象，
         *          让userMapper的实例获取这个代理对象
         *
         *      接下来就需要与数据库交互了，
         *      mapper调用方法，然后代理对象执行内部的invoke方法，这个方法会读取xml文件，获取与
         *      这个对象相对应的所有的方法，然后开始执行判断执行的方法与对象xml文件的方法是不是一致的
         *      如果一致开始执行   ，获取连接，与数据库交互，获取结果集
         *      User对象接受结果集
         *
         *      配置类：负责解析xml文件，这里只有2个xml config，一个usermapper
         *              内部提供了2个主要方法 build 和 readmapper
         *              build 解析config.xml配置文件，并创建数据库连接
         *              readmapper 解析usermapper配置文件，并返回一个MapperBean
         *
         *      工厂类：内部提供了一个执行器实例，一个配置类实例，
         *              一个拱执行器调用的查询方法，作用是与数据库做交互
         *              一个动态代理方法，用于生成传入类的一个代理对象
         *
         *              
         *      执行器类： 完成了JDBC的封装 提供执行器需要执行的方法
         *                  getController  用于获取数据库连接 调用build方法读取xml配置文件
         *                 query         与数据库交互
         *
         *      代理类： 用于完成xml方法和真是方法想对应，执行查询
         *
         */
    }

}
