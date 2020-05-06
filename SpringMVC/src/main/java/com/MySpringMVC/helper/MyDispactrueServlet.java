package com.MySpringMVC.helper;

import com.MySpringMVC.annotation.MyController;
import com.MySpringMVC.annotation.MyRequstMapping;
import com.MySpringMVC.utils.ClassUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDispactrueServlet extends HttpServlet {
    /**
     *  一、设计容器：
     *    1、定义controller容器
     *    2、定义controllerRequestMapping容器
     *    3、定义methodRequstMapping容器
     *  二、设计dispatcherServlet
     *    1、重写servlet init方法
     *      1) 通过解析xml获取package路径
     *      2) 扫描包下所有类，将带有ExtController注解的类全部添加到mvcBeans容器中   ==== init
     *      3) 通过遍历mvcBeans容器 通过java反射初始化mvcControllerUrl，mvcMethodUrl ==== handelMaping
     *    2、重写 get,post方法
     *      1)通过反射得到路径去寻找对应的方法。
     *      2)通过转发不改变访问路径 找到对应的页面   ===视图
     */
    //存储Controller的容器  key为Controller类的首字母小写，vlaue为反射对象
    private Map<String, Object> mvcBeans = new HashMap<String, Object>();
    //存储Controller路径的容器，key为控制器的路径，value为反射对象
    private  Map<String, Object>  mvcControllerUrl = new HashMap<String, Object>();
    //存储控制器内带有注解的方法的容器，key为该方法的访问路径，value为方法名
    private  Map<String, String>  mvcMethodUrl = new HashMap<String, String>();

    @Override
    public void init() throws ServletException {
        try {
            //1、扫包 获取该包路径下所有的文件
            List<Class<?>> classes =  ClassUtils.getClasses("com.MySpringMVC.myController.MyController");
            //2、遍历包下所有类，找到含有MyController类并装配到 mvcBeans容器中
            initBeans(classes);
            //3、处理路径与类的关系，和路径与方法的关系。处理映射关系
            handlerMapping();
        } catch (Exception e) {
            System.out.println("springmbvc初始化异常："+e);
        }
    }

    /**
     * 处理映射关系
     */
    public void handlerMapping() {

        //遍历所有的Controller对象
        for (Map.Entry<String, Object> entry : mvcBeans.entrySet()) {
            // 1、获取对象  以及反射对象
            Object object = entry.getValue();
            Class<? extends Object> classInfo =  object.getClass();
            // 2、判断类上有没有requestMapping
            String obectUrl = null;
            MyRequstMapping myController = classInfo.getDeclaredAnnotation(MyRequstMapping.class);
            if(myController != null ) {
                obectUrl = myController.value();
            }
            //3、遍历该类所有方法并且判断方法上面是否有requestMapping 并且设置对应关系。
            Method []  declareMthods = classInfo.getDeclaredMethods();
            for (Method method : declareMthods) {
                //1、判断该方法上是否有这个 注解
                MyRequstMapping methdoRequestMapping  = method.getDeclaredAnnotation(MyRequstMapping.class);
                //2、判断
                if(methdoRequestMapping != null ) {
                    //获取 value值
                    String  methdUrl = methdoRequestMapping.value();
                    //存储类
                    mvcControllerUrl.put(obectUrl+methdUrl, object);
                    //存储方法名称对应关系
                    mvcMethodUrl.put(obectUrl+methdUrl, method.getName());

                }

            }
        }

    }

    /**
     * 遍历包下所有类，找到含有MyController类并装配到 mvcBeans容器中
     * @param classes
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public  void initBeans(List<Class<?>>  classes) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (Class<?> classinfo : classes) {
            //1、找到含有MyController
            MyController extController = classinfo.getDeclaredAnnotation(MyController.class);
            //2、判断
            if(extController != null) {
                //首字母小写
                String  beanId =  ClassUtils.toLowerCaseFirstOne(classinfo.getSimpleName());
                //通过反射创建对象
                mvcBeans.put(beanId, ClassUtils.newInstance(classinfo));

            }

        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            dipact(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据路径找到对应的方法并且执行 返回对应的视图
     *
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    public void dipact(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //1、得到 请求路径
        String reqUrl = req.getRequestURI();
        //2、去mvcControllerUrl找对应的类
        Object  object = mvcControllerUrl.get(reqUrl);
        if(object == null){
            resp.getWriter().println("not fund controller ");
            System.out.println("没有找到对应的 controller");
            return ;
        }
        //3、找方法
        String  method = mvcMethodUrl.get(reqUrl);
        if(method.isEmpty()) {
            resp.getWriter().println("not fund requstMapping ");
            System.out.println("没有找到对应的 requstMapping");
            return ;
        }
        // 4、执行方法
        String retPage = methodInvok(object,method);

        //5、返回视图处理
        if(!retPage.isEmpty()) {
            viewdisplay(retPage,req,resp);
        }

    }
    /**
     * 执行方法
     * @param object
     * @param method
     * @return
     */
    public String methodInvok(Object  object, String  method) {
        try {
            //4、执行方法
            Class<? extends Object> classInfo =  object.getClass();
            //5、得到方法对象
            Method actMenthod = classInfo.getMethod(method);
            //6、执行方法
            return (String) actMenthod.invoke(object);


        } catch (Exception e) {
            System.out.println("执行方法报错："+e);

        }
        return null;
    }
    /**
     * 视图展示
     * @param pageName
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void viewdisplay(String pageName, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // 获取后缀信息
        String suffix = ".jsp";
        // 页面目录地址
        String prefix = "/mvc";
        //通过路径转发
        req.getRequestDispatcher(prefix + pageName + suffix).forward(req, res);
    }

}
