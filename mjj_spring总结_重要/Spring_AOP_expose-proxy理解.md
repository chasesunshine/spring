https://www.cnblogs.com/itplay/p/11751039.html

写在前面#
　　expose-proxy。为是否暴露当前代理对象为ThreadLocal模式。

SpringAOP对于最外层的函数只拦截public方法，不拦截protected和private方法（后续讲解），另外不会对最外层的public方法内部调用的其他方法也进行拦截，即只停留于代理对象所调用的方法。