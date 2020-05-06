package com.springMVC.servlet;

import com.springMVC.annotation.MyController;
import com.springMVC.annotation.MyRequestMapping;
import com.sun.xml.internal.bind.v2.model.core.ID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 创建MyDisPatcherServlet类，继承HttpServlet类，重写init、doGet、doPost方法
 */
public class MyDispattcherServlet extends HttpServlet {

    //该类主要用于读取配置文件 application.properties
   private Properties properties = new Properties();

   //用于保存class文件路径
    private List<String> classNames = new ArrayList<>();
    //保存控制的容器，key为控制器首字母小写的名称，key为控制器的反射对象
    private Map<String, Object> ioc = new HashMap<>();
    //保存方法路径 key为方法路径，method为方法
    private Map<String, Method> handlerMapping = new  HashMap<>();
    //保存控制器对象  key为控制器路径，value为控制器反射创建的实例
    private Map<String, Object> controllerMap  =new HashMap<>();


    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置文件  也就是加载在web.xml文件里的application.properties配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocaltion"));

        //2.初始化所有相关联的类,扫描用户设定的包下面所有的类
        doScanner(properties.getProperty("scanPackage"));

        //3.拿到扫描到的类,通过反射机制,实例化,并且放到ioc容器中(k-v  beanName-bean) beanName默认是首字母小写
        doInstance();

        //4.初始化HandlerMapping(将url和method对应上)
        initHandlerMapping();


    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //处理请求
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500!! Server Exception");
        }

    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //判断控制器内方法是否为空
        if(handlerMapping.isEmpty()){
            return;
        }
        //获取访问路径
        String url =req.getRequestURI();
        //获取项目名字
        String contextPath = req.getContextPath();

        url=url.replace(contextPath, "").replaceAll("/+", "/");
        //判断方法容器内是否有访问的方法路径
        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 NOT FOUND!");
            return;
        }
        //获取访问的方法路径对应的方法
        Method method =this.handlerMapping.get(url);

        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        //获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数值
        Object [] paramValues= new Object[parameterTypes.length];

        //方法的参数列表
        for (int i = 0; i<parameterTypes.length; i++){
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();
            if (requestParam.equals("HttpServletRequest")){
                //参数类型已明确，这边强转类型
                paramValues[i]=req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")){
                paramValues[i]=resp;
                continue;
            }
            if(requestParam.equals("String")){
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value =Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i]=value;
                }
            }
        }
        //利用反射机制来调用
        try {
            //调用方法，第一个参数是method所对应的实例 在ioc容器中
            method.invoke(this.controllerMap.get(url), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 执行方法
     * @param object
     * @param method
     * @return
     */
    public String methodInvok(Object  object, String  method,Object... org) {
        try {
            //4、执行方法
            Class<? extends Object> classInfo =  object.getClass();
            //5、得到方法对象
            Method actMenthod = classInfo.getMethod(method);
            //6、执行方法
            return (String) actMenthod.invoke(object,org);


        } catch (Exception e) {
            System.out.println("执行方法报错："+e);

        }
        return null;
    }

    private void  doLoadConfig(String location){
        //把web.xml中的contextConfigLocation对应value值的文件加载到流里面
        //也就是web.xml文件里的param-name 对应的param-value的配置文件
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            //用Properties文件加载文件里的内容
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关流
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doScanner(String packageName) {
        //把所有的.替换成/  也就是将包结构缓存目录结构
        URL url  =this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        //过滤文件，将class文件提取出来，
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                //递归读取包
                doScanner(packageName+"."+file.getName());
            }else{
                String className =packageName +"." +file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }


    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        //遍历class文件，将controller类提取出来，并反射创建其对象，放到ioc容器中
        for (String className : classNames) {
            try {
                //把类搞出来,反射来实例化(只有加@MyController需要实例化)
                Class<?> clazz =Class.forName(className);
                //判断类上面是否有mycontroller注解
                if(clazz.isAnnotationPresent(MyController.class)){
                    //如果是控制器，则将该类首字母小写作为key，反射对象作为value放到ioc容器中
                    ioc.put(toLowerFirstWord(clazz.getSimpleName()),clazz.newInstance());
                }else{
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    private void initHandlerMapping(){
        if(ioc.isEmpty()){
            return;
        }
        try {
            //遍历ioc容器中的对象
            for (Map.Entry<String, Object> entry: ioc.entrySet()) {
                //ioc容器保存的对象是反射创建的，获取该反射对象的路径 即clazz为控制器路径
                Class<? extends Object> clazz = entry.getValue().getClass();
                if(!clazz.isAnnotationPresent(MyController.class)){
                    continue;
                }
                //拼url时,是controller头的url拼上方法上的url
                String baseUrl ="";
                //遍历控制器类里的所有的方法，在这里就是TestController
                if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                    //获取控制器上的注解的值
                    MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl=annotation.value();
                }
                //获取控制器内所有的方法
                Method[] methods = clazz.getMethods();
                //遍历这些方法，将这些方法进行过滤，提取带有注解的方法
                for (Method method : methods) {
                    if(!method.isAnnotationPresent(MyRequestMapping.class)){
                        continue;
                    }
                    //提取带有路径的方法
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    //获取注解方法的路径
                    String url = annotation.value();
                    //获取方法全路径  将控制器上的注解路径和内部的方法路径拼接在一起
                    url =(baseUrl+"/"+url).replaceAll("/+", "/");
                    //将方法路径保存在map容器中
                    handlerMapping.put(url,method);
                    //利用反射，为该控制器创建实例对象，并将路径一起放到map中
                    controllerMap.put(url,clazz.newInstance());
                    System.out.println(url+","+method);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 把字符串的首字母小写
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name){
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }


}
