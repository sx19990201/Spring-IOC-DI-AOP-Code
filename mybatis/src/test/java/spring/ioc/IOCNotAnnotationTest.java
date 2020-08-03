package spring.ioc;

import com.spring.ioc.ioc_not_annotation.pojo.User;
import com.spring.ioc.ioc_not_annotation.applicationContext.ClassPathXmlApplicationContext;

public class IOCNotAnnotationTest {
    public static void main(String[] args) {
        /**
         * 创建ClassPathXmlApplicationContext容器，
         * 此容器内提供了几个存储不同类型的map
         * 其中singletonBeanFactory;存储单例模式对象
         *  beanDefinationFactory   存储定义对象容器，比如User
         *  beanEleMap              存储xml文件的bean节点
         *  beanScopeMap            存储bean节点的作用域
         * 传入配置文件的path，在此容器内，会加载配置文件
         *  首先他会用懒汉模式初始化内置的容器
         *  他会先读取配置文件，将配置解析，解析后，
         *  他会分别将bean节点的 id，class，scope读取出来，并执行相应的操作
         *  把id作为key，bean节点的内容作为key存到 beanElemap中，
         *  然后将id作为key，scope作用域属性作为值传入beanscopemap中，
         *  将读取的class路径通过反射获取到类的对象，
         *  然后将beanId作为ky，获取到的对象放入beanDefinationFactory工厂中
         *  至此，spring的ApplicationContext初始化完毕
         */
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("spring/ioc_not_annottation/user.xml");
        /**
         * 初始化容器过后，可以调用容器内的getBean方法
         * 这个方法会将传入的beanId，通过用idid获取到beanDefinationFactory工厂内反射的对象
         * 然后通过beanid获取到beanEleMap内存储的bean节点
         * 然后通过beanid获取到beanScopeMap内存储的该bean的作用域属性
         * 拿到这些数据后，先用作用域进行比对，
         * 如果是作用域是单例模式，则将通过反射创建一个对象的实例存储到单例模式的工厂singletonBeanFactory中
         *如果是多例模式，则直接通过beanid反射创建一个对象
         * 创建对象过后就可以为对象的属性赋值了
         * 在赋值过程中，
         *  先将beanEleMap内存储的bean节点的property节点提取出来用集合保存，
         *  然后遍历这个集合
         *  先获取property节点的name属性和value属性，
         *  然后利用Field反射类获得类的成员属性，再将该属性设置为可访问，防止属性是优化访问失败
         *  判断name的类型和value的类型，看name和value的类型是否反射对象的属性对否一致，
         *  如果不一致则需要转换类型，直到name和value的类型与反射对象的属性全部一致后
         *  讲这些属性保存到反射对象中去
         *
         *  最后返回这个反射对象
         */
        //使用手动强转的方式获取单例的user对象
        User user1_1 = (User) ctx.getBean("user1");
        System.out.println("单例user1_1:"+user1_1);
        /**
         *getBean有一个重载方法，根据传入的id值获取到bean的同时，强制转换为指定为类型.
         * .
         *
         *
         *
         * 
         */
        //使用传入类对象的方式获取单例的user对象
        User user1_2 = ctx.getBean("user1",User.class);
        System.out.println("单例user1_2:"+user1_2);


        //使用手动强转的方式获取多例的user对象
        User user2_1 = (User) ctx.getBean("user2");
        System.out.println("多例user2_1:"+user2_1);

        //使用传入类对象的方式获取单例的user对象
        User user2_2 = ctx.getBean("user2",User.class);
        System.out.println("多例user2_2:"+user2_2);

        ctx.destroy();
    }
}
