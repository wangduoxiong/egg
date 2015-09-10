package edu.xiyou.andrew.Egg.proxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 自动查找工程下资源文件里的配置文件
 * Created by andrew on 15-9-6.
 */
public class ProxyPropertiesService {
    private static final Class clazz = ProxyPropertiesService.class;
    private static final Logger logger = LoggerFactory.getLogger(ProxyPropertiesService.class);

    private static final ImmutableMap<Object, Object> functionMap = ImmutableMap.builder()
            .put("hostname", getMethodByName("setHostName"))
            .put("port", getMethodByName("setPort"))
            .put("schemeName", getMethodByName("setSchemeName")).build();

    public static List<Proxy> getProxysFromProperties(String filePath) throws IOException {
        Properties properties = new Properties();
//        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
        properties.load(ProxyPropertiesService.class.getClassLoader().getResourceAsStream(filePath));

        Iterator iterator = properties.entrySet().iterator();
        List<Proxy> list = Lists.newArrayList();

        String lastString = "";
        Host host = null;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            String[] keys = StringUtils.split(key, '.');

            if (StringUtils.isNotEmpty(keys[0]) && !lastString.equals(keys[0])) {
                lastString = keys[0];
                if (host != null) {
                    list.add(new Proxy(new HttpHost(String.valueOf(host.getHostName()), host.getPort(), host.getSchemeName())));
                }
                host = new Host();
            }

            Method method = (Method) functionMap.get(keys[keys.length - 1]);
            if (method != null){
                try {
                    method.invoke(null, host, value);
                } catch (IllegalAccessException e) {
                    logger.error("op=getProxyFromProperties, e", e);
                } catch (InvocationTargetException e) {
                    logger.error("op=getProxyFromProperties, e", e);
                }
            }
        }
        if ((host != null) && (StringUtils.isNotEmpty(host.getHostName()))) {
            list.add(new Proxy(new HttpHost(String.valueOf(host.getHostName()), host.getPort(), host.getSchemeName())));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static Method getMethodByName(String methodName){
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, Host.class, String.class);
        } catch (NoSuchMethodException e) {
            logger.error("op=getMethodByName, methodName={} , exception: ", methodName, e.getMessage());
        }
        return method;
    }

    private static void setPort(Host host, String port){
        host.setPort(Integer.parseInt(port));
    }

    private static void setSchemeName(Host host, String schemeName){
        host.setSchemeName(schemeName);
    }

    private static void setHostName(Host host, String hostName){
        host.setHostName(hostName);
    }

    private static class Host{
        private String hostName;
        private String schemeName;
        private int port;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getSchemeName() {
            return schemeName;
        }

        public void setSchemeName(String schemeName) {
            this.schemeName = schemeName;
        }
    }

}
