package test.com.spring.DI.test; 

import com.spring.DI.applicationContext.AnnotationConfigApplicationContext;
import com.spring.DI.service.UserService;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* Test Tester. 
* 
* @author <Authors name> 
* @since <pre>һ�� 6, 2020</pre> 
* @version 1.0 
*/ 
public class TestTest {
    //创建AnnotationConfigApplicationContext工厂
    AnnotationConfigApplicationContext ctx;

    //创建UserService对象
    UserService userService;


    @Before
public void before() throws Exception {
    //实例工厂类，传入pojo/service/test三个包路径进行扫描
    ctx = new AnnotationConfigApplicationContext
            ("com.spring.DI.pojo", "com.spring.DI.service", "com.spring.DI.test");
    userService = ctx.getBean("userService", UserService.class);
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: init() 
* 
*/ 
@Test
public void testInit() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: testSpringDI() 
* 
*/ 
@Test
public void testTestSpringDI() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: close() 
* 
*/ 
@Test
public void testClose() throws Exception { 
//TODO: Test goes here... 
} 


} 
