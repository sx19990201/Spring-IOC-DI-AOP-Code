package com.spring.DI.applicationContext;

import com.spring.DI.annotation.MyAutowired;
import com.spring.DI.annotation.MyComponent;
import com.spring.DI.annotation.MyValue;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解工厂类
 */
public class AnnotationConfigApplicationContext {

    //此map容器用于存储类定义对象
    private Map<String, Class<?>> beanDefinationFactory = new ConcurrentHashMap<String, Class<?>>();
    //此map容器用于存储单例对象
    private Map<String, Object> singletonBeanFactory = new ConcurrentHashMap<String, Object>();

    /**
     * 有参构造方法，参数类型指定要扫描加载的包名，此工厂可接受多个包路径
     */
    public AnnotationConfigApplicationContext(String... packageNames) {
        //遍历扫描指定的所有包路径
        for (String packageName : packageNames) {
            System.out.println("开始扫描包：" + packageName);
            //扫描包的方法
            scanPkg(packageName);
        }
        /**
         * 进行DI依赖注入
         * 在方法内部获取类定义对象容器内标有autowried的对象，然后进行注入
         */
        dependencyInjection();
    }

    /**
     * 在工厂类的构造方法中，可以接受多个包路径，并且遍历循环扫描每一个包路径，
     * 该方法用于扫描制定包，找到包中的类文件
     * 对于标准（类上定义注解的）类文件反射加载创建类定义对象并放到容器中
     *
     * @param pkg
     */
    private void scanPkg(final String pkg) {
        //替换包路径，将包结构转换为目录结构
        String pkgDir = pkg.replaceAll("\\.", "/");
        //获取目录结构在类路径中的位置（其中url中封装了具体资源的路径）
        URL url = getClass().getClassLoader().getResource(pkgDir);
        //基于这个路径资源（url)，构建一个文件对象
        File file = new File(url.getFile());
        //获取此目录中指定标准（以.class结尾的文件）  也就是过滤一遍
        File[] fs = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                //获取文件名
                String fName = file.getName();
                //判断该文件是目录还是文件,如果是目录进一步递归
                if (file.isDirectory()) {
                    scanPkg(pkg + "." + fName);
                } else {
                    //判断文件后缀是否是.class
                    if (fName.endsWith(".class")) {
                        return true;
                    }
                }
                return false;
            }
        });
        //过滤完成后，遍历所有的class文件
        for (File f : fs) {
            //获取文件名  User.class
            String fName = f.getName();
            //获取去除。class之后的文件  User
            fName = fName.substring(0, fName.lastIndexOf("."));
            //讲名字（类名通常是大写开头）的第一个字母转换为小写（用它作为key存储在工厂中） user
            String beanId = String.valueOf(fName.charAt(0)).toLowerCase() + fName.substring(1);
            //构建一个类全名（包.类名） 改名字就是类所在的位置
            String pkgCls = pkg + "." + fName;
            try {
                //通过反射构建类对象
                Class<?> c = Class.forName(pkgCls);
                //判定这个类对象上是否有MyConponent注解
                if (c.isAnnotationPresent(MyComponent.class)) {
                    //将类对象存储到map容器中
                    beanDefinationFactory.put(beanId, c);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 扫描所有的包完成之后，对需要的属性进行注入
     * 此方法用于对属性进行依赖注入
     * 从工厂中获取所有的类对象，如果类中的属性有myAutowried注解
     * 那么首先从根据属性名从工厂中获取对象，或者根据对象类型获取对象
     * 最后用该对象对属性注入
     */
    private void dependencyInjection() {
        //获取容器中所有类定义对象
        Collection<Class<?>> classes = beanDefinationFactory.values();
        //遍历每一个对象
        for (Class<?> cls : classes) {
            //获取类对象的名字全全称（包名.类名）
            String clsName = cls.getName();
            //获取类名 反射保存的对象地址指向是包路径.类名 所以找到最后一个点
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            //将类名（通常为大写开头）的第一个字母转换为小写
            String beanId = String.valueOf(clsName.charAt(0)).toLowerCase() + clsName.substring(1);
            //获取类中所有的属性
            Field[] fields = cls.getDeclaredFields();
            //遍历每一个属性
            for (Field field : fields) {
                //如果这个属性上有MyAutowried注解，进行注入操作
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    try {
                        //获取属性名 user1
                        String fieldName = field.getName();
                        System.out.println("属性名：" + fieldName);
                        //定义为属性注入的bean对象（此对象从容器中获取）
                        //这个对象用来接收User类生成的反射对象
                        Object fieldBean = null;
                        //首先根据属性名从容器中取出对象，如果不是null，则赋值给fieldBean对象
                        if (beanDefinationFactory.get(fieldName) != null) {
                            fieldBean = getBean(fieldName, field.getType());
                        } else {  //否则按照属性的类型从容器中取出对象进行注入
                            //获取属性的类型（包名+类名）
                            String type = field.getType().getName();
                            //截取最后的类名
                            type = type.substring(type.lastIndexOf(".") + 1);
                            //j将类名（通常为大写开头）的第一个字母转换小写
                            String fieldBeanId = String.valueOf(type.charAt(0)).toLowerCase() + type.substring(1);
                            System.out.println("属性类型ID：" + fieldBeanId);
                            //根据转换后的类型beanId，从容器中获取对象并赋值给fieldBean对象
                            fieldBean = getBean(fieldBeanId, field.getType());
                        }
                        System.out.println("要为属性注入的值：" + fieldBean);
                        //如果fieldBean对象不为空，则为该属性进行注入
                        if (fieldBean != null) {
                            //获取此类定义的对象的实例对象 获取userService的实例对象
                            Object clsBean = getBean(beanId, cls);
                            //设置此属性可访问
                            field.setAccessible(true);
                            //为该属性注入值 将user对象注入到userService实例对象
                            //field.set(object,value),作用是为重新设置新的属性值，即
                            //clsBean对象重新设值为fieldBean，即
                            //userService设置为User 在这里就是将user对象注入到了userService了
                            field.set(clsBean, fieldBean);
                            System.out.println("注入成功！");
                        } else {
                            System.out.println("注入失败");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 在dependencyInjection方法中调用了getBean方法
     * 根据传入的bean值的id获取容器对象，类型为Object
     *
     * @param beanId user
     * @return
     */
    public Object getBean(String beanId) {
        //根据传入的beanid获取类对象
        Class<?> cls = beanDefinationFactory.get(beanId);
        //根据类对象获取其定义的注解
        MyComponent annotation = cls.getAnnotation(MyComponent.class);
        //获取注解的scope属性值
        String scope = annotation.scope();
        try {
            //如果scope的值为单例，则创建单例对象
            if ("singleton".equals(scope) || "".equals(scope)) {
                if (singletonBeanFactory.get(beanId) == null) {
                    //判断单例容器是否已有该类的实例，如果没有就创建
                    Object instance = cls.newInstance();
                    //给对象的成员属性赋值
                    setFieldValues(cls, instance);
                    //保存在单例容器中
                    singletonBeanFactory.put(beanId, instance);
                }
                //根据beanId获取单例容器内的对象并返回
                return singletonBeanFactory.get(beanId);
            }
            //如果scope = prototype，则创建返回多例对象
            if ("prototype".equals(scope)) {
                Object instance = cls.newInstance();
                setFieldValues(cls, instance);
                return instance;
            }
            //目前仅支持单例和多例两种创建对象的方式
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //如果遭遇异常则返回null
        return null;
    }

    /**
     * 在getBean方法中从工厂容器中获取对象，并且需要调用setFieldValues方法为对象的属性赋值
     * 此方法用于为对象的属性赋值
     * 内部是通过获取成员属性上注解的值，再转换为类型之后，通过反射为对象
     *
     * @param cls      类定义对象 cls是反射获取的类路径，
     * @param obj 要为其赋值的实例对象        obj是由反射路径创建的实例对象
     */
    private void setFieldValues(Class<?> cls, Object obj) {
        //获取类中所有的成员属性
        Field[] fields = cls.getDeclaredFields();
        //遍历所有属性
        for (Field field : fields) {
            //如果此属性有Myvalue修饰，对其进行操作
            if (field.isAnnotationPresent(MyValue.class)) {
                //获取属性名
                String fieldName = field.getName();
                //获取注解中的值
                String value = field.getAnnotation(MyValue.class).value();
                //获取属性定义的类型
                String type = field.getType().getName();
                //将属性名改为以大写字母开头：如id改为Id
                fieldName = String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
                //定义set方法名称
                String setterName = "set" + fieldName;
                try {
                    //返回一个Method对象，该对象反映了此Class对象所表示的类或者接口的指定已声明的方法
                    //即获取cls类里面所有的方法
                    Method method = cls.getDeclaredMethod(setterName, field.getType());
                    //判断属性类型，如果类型不一致，则转换类型后调用set方法为属性赋值
                    if ("java.lang.Integer".equals(type) || "int".equals(type)) {
                        int intValue = Integer.valueOf(value);
                        //将方法注入到反射对象中去，由于invoke方法返回的是一个对象，并且参数必须为
                        //包装类型，所以进行类型转换
                        method.invoke(obj, intValue);
                    }else if ("java.lang.String".equals(type)){
                        method.invoke(obj,value);
                    }
                    //作为测试，仅判断Integer和String类型，其它类型同理
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 此为重载方法，根据传入的class对象在内部进行强转，返回传入的class对象类型
     * @param beanId
     * @param c
     * @return
     */
    public <T> T getBean(String beanId, Class<?> c) {
        return (T) getBean(beanId);
    }

    /**
     * 销毁方法，用于释放资源
     */
    public void close(){
        beanDefinationFactory.clear();
        beanDefinationFactory=null;
        singletonBeanFactory.clear();
        singletonBeanFactory=null;
    }

}
