# Spring源码-Bean的实例化

&emsp;&emsp;接下来我们看看Bean的实例化处理

# 一、BeanDefinition

&emsp;&emsp;首先我们来看看BeanDefinition的存放位置。因为Bean对象的实例化肯定是BeanFactory基于对应的BeanDefinition的定义来实现的，所以在这个过程中BeanDefinition是非常重要的，前面的课程讲解已经完成了BeanDefinition的定义。同时根据前面refresh方法的讲解我们知道了BeanFactory的具体实现是 `DefaultListableBeanFactory`.所以BeanDefinition的相关信息是存储在 `DefaultListableBeanFactory`的相关属性中的。

```java
/** Map of bean definition objects, keyed by bean name. */
private final Map<String, BeanDefinition> beanDefinitionMap = new
ConcurrentHashMap<>(256);

```

# 二、Bean实例的创建过程

&emsp;&emsp;然后就是Bean实例的创建过程。这块儿我们可以通过Debug的形式非常直观的看到。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/8ee5c6ecff8044c4b3a1fe3a9ccee78c.png)

&emsp;&emsp;按照这种步骤一个个去分析就OK了。

# 三、单例对象

&emsp;&emsp;在创建单例对象的时候是如何保存单例的特性的？这块我们需要注意下面的代码

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/2e77915f6918402fa9b4adef27aa42ec.png)

然后进入到getSingleton方法中。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/bcaece7883b746fcb61c7817c0048913.png)

创建成功的单例对象会被缓存起来。在 addSingleton 方法中

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/3c706758501a441ab2ea9cd47a44112b.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/a4acbfb844aa4505b6e05af8cf336439.png)

所以singletonObjects是缓存所有Bean实例的容器

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/c1d31dcfc11d452d9fc0389de0ff2662.png)

而具体创建单例Bean的逻辑会回调前面的Lambda表达式中的createBean方法

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/6497b6627bdf48d9b9a4aab1019ed4b4.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/faee3814aafb40eda1c6bc142ab98d89.png)

# 四、单例对象的销毁

&emsp;&emsp;然后我们先来看下一个单例Bean对象的销毁过程。定义一个案例

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/c8a25488d5944702b03af07eba64bf8e.png)

然后我们在测试的案例中显示的调用 `close`方法

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/84cb3013aa7a41228dc692ebce708e6d.png)

执行的时候可以看到相关的日志执行了。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/acbcfddeb5434ccdb8814048cb1a02b7.png)

进入到close方法中分析，比较核心的有两个位置。在doClose方法中。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/59998ddda74c45f6aa320ec2eb8970b7.png)

具体销毁的代码进入destroyBeans()中查看即可。

在doClose方法中有个提示。registerShutdownHook方法

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/c5136dd08f8f4ef1a69064ac0ebca36a.png)

```java
	@Override
	public void registerShutdownHook() {
		if (this.shutdownHook == null) {
			// No shutdown hook registered yet.
			this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
				@Override
				public void run() {
					synchronized (startupShutdownMonitor) {
						doClose();
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		}
	}
```

对应的在web项目中就有对应的调用

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1663654521076/7892997af8744151a95852d4e4bb998d.png)

这个就是Bean实例化的过程了，当然在实例化中的DI问题我们在下篇文章中重点分析。
