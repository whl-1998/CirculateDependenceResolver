# CirculateDependenceResolver


参考 Spring 手撸的简化版 IoC 框架，不涉及通过配置文件解析 BeanDefinition 的内容。

该框架能完成如下几个工作：

1. 通过传入一个待实例化类的 Class 类型，能够自动创建它的实例对象，并注入它的依赖。当然，依赖实例被创建时也会进行自动属性注入。
2. 通过两个缓存解决了循环依赖的问题。



本框架主要涉及的组件如下所示：

### DefaultSingletonBeanRegistry

它内部封装了三个容器

```java
// 一级缓存
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

// 二级缓存
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

// 被注册的bean名称
private final Set<String> registeredSingletons = new LinkedHashSet<>(256);
```

其中一级缓存负责缓存被完整实例化的 Bean，二级缓存负责缓存属性还未被注入的 Bean。

它主要实现了如下几个 API：

* getSingleton - 从缓存池中获取 bean
* createSingleton - 通过无参构造器创建一个 bean 实例
* addEarlySingleton - 添加到二级缓存
* addSingletonObject - 更新缓存



### AbstractBeanFactory

继承了 DefaultSingletonBeanRegistry，实现了如下几个 API：

* doGetBean - 创建 Bean 实例，并注入属性
* populateBean - 属性注入，会获取到 WriteMethod
* doPopulateBean - 通过获取到的 WriteMethod 执行属性注入
* transformedBeanName - 通过 class 解析它的 beanName
* markBeanAsCreated - 标识一个 bean 正处于创建中



### ReflectUtils

反射工具类，内部实现了与反射相关的诸多操作，比如通过 class 获取到它的 setter 方法列表、获取到构造方法等。



## Note

由于循环依赖的解决需要依靠无参构造的创建，因此，待实例化的 Bean 必须实现无参构造方法，且遵循 java bean 的规范提供了 setter 方法。

> 针对这一点，Spring 对未提供 setter 方法的 bean 还提供了工厂方法、自动按照名称、自动按照类型的方式进行注入。具体可以参照 @Bean、@Autowired 等注解的使用。