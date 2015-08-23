import com.fasterxml.jackson.core.JsonProcessingException;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.parser.utils.JsonUtils;
import org.junit.Test;

/**
 * Created by duoxiongwang on 15-8-23.
 */
public class TestJsonUtils {

    @Test
    public void testobject2Json() throws JsonProcessingException {
        CrawlDatum datum = new CrawlDatum("http://qunar.com");

        System.out.println(JsonUtils.object2JsonString(datum));
    }
}
