package com.spring.ioc.ioc_not_annotation.applicationContext;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 非注解形势实现IOC控制反转
 *
 * 创建spring容器 ，该容器提供
 * 存储单例对象的容器，
 * 存储定义对象的容器，
 * 存储xml文件内bean节点内容的容器
 * 存储scope作用域的容器
 */
public class ClassPathXmlApplicationContext {
    //存储单例对象容器
    private Map<String, Object> singletonBeanFactory;
    //存储创建类定义对象的容器
    private Map<String, Class<?>> beanDefinationFactory;
    //存储beanElement 也就是xml文件里的bean节点
    private Map<String, Element> beanEleMap;
    //存储bean的scope属性容器
    private Map<String, String> beanScopeMap;

    //定义有参的构造方法，在创建此类实例时，需要指定xml文件路径
    public ClassPathXmlApplicationContext(String xmlpath) {
        singletonBeanFactory = new HashMap<>();
        beanDefinationFactory = new HashMap<>();
        beanEleMap = new HashMap<>();
        beanScopeMap = new HashMap<>();
        init(xmlpath);
    }

    /**
     * 初始化方法，在创建ClassPathXmlApplicationContext 对象时初始化容器
     * 并解析xml配置文件，获取bean元素，在运行时动态创建对象，并为对象的属性赋值
     * 最后吧对象存放在容器中
     *
     * @param xmlPath
     */
    private void init(String xmlPath) {
        //使用dom4j技术读取xml文档 创建saxreader对象
        SAXReader reader = new SAXReader();
        try {
            //获取读取xml配置文件的输入流
            InputStream is = getClass().getClassLoader().getResourceAsStream(xmlPath);
            //读取xml,该操作会返回一个Document对象
            Document document = reader.read(is);
            //获取文档的根元素
            Element rootElement = document.getRootElement();
            //获取根元素下所有的bean节点，element方法会返回元素的集合
            List<Element> beanElements = rootElement.elements("bean");

            //遍历bean节点
            for (Element beanEle : beanElements) {
                //获取bean节点的id，把该值做为key存储在map集合中
                String beanId = beanEle.attributeValue("id");
                //将beanElementd对象存入map中，为对象设置属性值时使用
                beanEleMap.put(beanId, beanEle);
                //获取bean节点的scope值
                String beanScope = beanEle.attributeValue("scope");
                //如果beanScope不等于null,将bean的scope值存入map中方便后续使用
                if (beanScope != null) {
                    beanScopeMap.put(beanId, beanScope);
                }
                //获取bean节点实体类对应的class路径
                String beanClassPath = beanEle.attributeValue("class");
                //利用反射根据class路径得到类定义对象
                Class<?> cls = Class.forName(beanClassPath);
                //如果反射获取的类定义对象不为null，则放入工厂中，方便创建实例对象
                if (cls != null) {
                    beanDefinationFactory.put(beanId, cls);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据传入的bean的id值获取容器中的对象，类型为Object
     * @param beanId
     * @return
     */
    public Object getBean(String beanId) {
        //根据传入的beanId获取类对象
        Class<?> cls = beanDefinationFactory.get(beanId);
        //根据id获取该bean对象的element对象
        Element element = beanEleMap.get(beanId);
        //获取scopeMap中bean元素的scope属性值
        String scope = beanScopeMap.get(beanId);
        Object object = null;
        try {
            //如果scope 等于singleton，或者scope为空 由于为空的话系统默认设置的作用域是singleton，创建单例对象；
            if ("singleton".equals(scope) || null == scope) {
                //判断容器中是否已有该对象的实例，如果没有，创建一个实例对象放到容器中
                if (singletonBeanFactory.get(beanId) == null) {
                    //创建反射加载的类的实例
                    Object instance = cls.newInstance();
                    //将创建后的对象放入实例对象容器中 beanid为xml配置文件的bean节点的id 也就是实例对象的名字
                    singletonBeanFactory.put(beanId, instance);
                }
                //根据beanid获取实例对象容器中的对象
                object = singletonBeanFactory.get(beanId);
            }
            //如果socpe等于prototype，则创建并返回多例对象
            if ("prototype".equals(scope)) {
                object = cls.newInstance();
            }
            setFieldValues(beanId, element, scope, cls, object);
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //暂不支持其他类型，若不是以上两种类型或者遭遇异常则返回null;
        return null;
    }

    /**
     * 此为重载方法，再根据传入的bean的id值获取容器中的对象的同时，还可以自动转换类型。
     * 返回指定的类型，在调用该方法时省略强转的步骤，传入时第二个参数为指定的类型，
     * 方法实现上一个方法，只是在返回对象前加了类型强转
     *
     * @param beanId 传入bean的id
     * @param cls    指定的类型
     * @param <T>    未知类型
     * @return 返回指定的类型
     */
    public <T> T getBean(String beanId, Class<?> cls) {
        return (T) getBean(beanId);
    }

    /**
     * 该方法用于为对象设置成员属性值
     * @param beanId    bean元素的id
     * @param element   bean所对应的element对象
     * @param beanScope bean元素的scope属性
     * @param cls       类对象
     * @param object    要为其成员属性赋值的实例对象
     */
    private void setFieldValues(String beanId, Element element, String beanScope, Class<?> cls, Object object) {
        try {
            //获取每个bean元素下的所有property元素，该元素用于给属性赋值
            List<Element> propEles = element.elements("property");
            //如果property元素集合为null，调用putInMap方法将对象放进Map中
            if (propEles == null) {
                return;
            }
            //遍历property元素集合
            for (Element e : propEles) {
                //获取每个元素的name属性值和value属性值
                String filedName = e.attributeValue("name");
                String filedValue = e.attributeValue("value");
                //利用反射根据name属性获得类的成员属性
                Field field = cls.getDeclaredField(filedName);
                //将该属性设置为可访问(防止成员属性被私有化导致访问失败)
                field.setAccessible(true);
                //获取成员属性的类型名称，若非字符串类型，则需要做相应的转换
                String filedTypeName = field.getType().getName();
                //判断该成员属性是否是int或INteger类型
                if ("int".equals(filedTypeName) || "java.lang.Integer".equals(filedTypeName)){
                    //转换为int类型并为该成员属性赋值
                    int intFiledValue = Integer.parseInt(filedValue);
                    field.set(object,intFiledValue);
                }
                //判断改成员是否是String类型
                if ("java.lang.String".equals(filedTypeName)){
                    //为该成员属性赋值
                    field.set(object, filedValue);
                }
                //此处省略其他类型的判断   道理同上。
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


       public void destroy(){
        singletonBeanFactory.clear();
        singletonBeanFactory=null;

        beanDefinationFactory.clear();
        beanDefinationFactory=null;

        beanEleMap.clear();
        beanEleMap=null;

        beanScopeMap.clear();
        beanScopeMap=null;
    }


   /* public Map<String, Object> getSingletonBeanFactory() {
        return singletonBeanFactory;
    }

    public void setSingletonBeanFactory(Map<String, Object> singletonBeanFactory) {
        this.singletonBeanFactory = singletonBeanFactory;
    }

    public Map<String, Class<?>> getBeanDefinationFactory() {
        return beanDefinationFactory;
    }

    public void setBeanDefinationFactory(Map<String, Class<?>> beanDefinationFactory) {
        this.beanDefinationFactory = beanDefinationFactory;
    }

    public Map<String, Element> getBeanEleMap() {
        return beanEleMap;
    }

    public void setBeanEleMap(Map<String, Element> beanEleMap) {
        this.beanEleMap = beanEleMap;
    }

    public Map<String, String> getBeanScopeMap() {
        return beanScopeMap;
    }

    public void setBeanScopeMap(Map<String, String> beanScopeMap) {
        this.beanScopeMap = beanScopeMap;
    }
*/
}
