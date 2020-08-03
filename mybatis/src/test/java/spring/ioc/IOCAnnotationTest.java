package spring.ioc;

import com.spring.ioc.ioc_annotation.pojo.PrototypeUser;
import com.spring.ioc.ioc_annotation.pojo.SingletonUser;
import com.spring.ioc.ioc_annotation.applicationContext.AnnotationConfigApplicationContext;

public class IOCAnnotationTest {
    public static void main(String[] args) {
        //创建AnnotationConfigApplicationContext容器
        AnnotationConfigApplicationContext ctx=
                new AnnotationConfigApplicationContext("com.spring.ioc.ioc_annotation.pojo");
        //仅使用key作为参数获取对象，需要强转
        SingletonUser singletonUser1 =(SingletonUser)ctx.getBean("singletonUser");

        System.out.println("单例User对象："+singletonUser1);

        //使用key和类对象作为参数获取对象，无需强转
        SingletonUser singletonUser2 = ctx.getBean("singletonUser",SingletonUser.class);
        System.out.println("单例User对象"+singletonUser2);

        //仅使用key作为参数获取对象，需要强转
        PrototypeUser prototypeUser1 = (PrototypeUser)ctx.getBean("prototypeUser");
        System.out.println("多例User对象"+prototypeUser1);

        //使用key和类对象作为参数获取对象，无需强转
        PrototypeUser prototypeUser2 = ctx.getBean("prototypeUser",PrototypeUser.class);
        System.out.println("多例User对象"+prototypeUser2);

        ctx.close();

    }

}
