package spring.DI;

import com.spring.DI.annotation.MyComponent;
import com.spring.DI.applicationContext.AnnotationConfigApplicationContext;
import com.spring.DI.service.UserService;
import org.junit.After;
import org.junit.Before;

@MyComponent
public class Test {

    //创建AnnotationConfigApplicationContext工厂
    AnnotationConfigApplicationContext ctx;
    //创建UserService对象
    UserService userService;

    /**
     * 初始化方法
     */
    @Before
    public void init() {
        //实例工厂类，传入pojo/service/test三个包路径进行扫描
        ctx = new AnnotationConfigApplicationContext
                ("com.spring.DI.pojo", "com.spring.DI.service", "com.spring.DI.test");
        userService = ctx.getBean("userService", UserService.class);
    }
    /**
     * 测试方法
     */
    public void testSpringDI() {
        userService.userLogin();
    }

    /**
     * 销毁方法
     */
    @After
    public void close() {
        ctx.close();
    }


}
