import edu.xiyou.andrew.Egg.proxy.Proxy;
import edu.xiyou.andrew.Egg.utils.PropertiesUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by andrew on 15-9-8.
 */
public class TestProPertiesUtils {
    @Test
    public void getProperties(){
        try {
            List<Proxy> list = PropertiesUtils.getProxysFromProperties("proxy.properties");
            System.out.println(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
