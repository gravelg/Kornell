package kornell;

import static kornell.core.util.StringUtils.mkurl;
import static kornell.core.util.StringUtils.trimSlashes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import kornell.core.util.StringUtils;

public class TestStringUtils {

    @Test
    public void testTrimSlashes(){
        assertEquals("um/dois/tres",trimSlashes("/um/dois/tres/"));
        assertEquals("um/dois/tres",trimSlashes("um/dois/tres/"));
        assertEquals("um/dois/tres",trimSlashes("/um/dois/tres"));
        assertEquals("um/dois/tres",trimSlashes("um/dois/tres"));
        assertEquals("um/dois/tres",trimSlashes("//um/dois/tres///"));
        assertEquals("",trimSlashes("/"));
        assertEquals("",trimSlashes("///"));
        assertEquals("",trimSlashes("////"));
    }

    @Test
    public void testComposeURL() {
        assertEquals("http://correct/path1",mkurl("http://correct","/path1") );
        assertEquals("http://correct/path2",mkurl("http://correct","path2") );
        assertEquals("http://correct/path3",mkurl("http://correct/","path3") );
        assertEquals("http://no/slashes/is/ok",mkurl("http://no","slashes","is","ok") );
        assertEquals("http://double/slash/happens",mkurl("http://double/","/slash/","/happens") );
        assertEquals("http://empty/segments/too",mkurl("http://empty","/segments","","/too") );
        assertEquals("/absolute/path/welcome",mkurl("/absolute","/path","/welcome") );
        assertEquals("relative/path/welcome",mkurl("relative","/path","/welcome") );
        assertEquals("/composite/segments/too",mkurl("/composite/segments","/too") );
        assertEquals("/does/not/start/with/double/slash",mkurl("/","/does/not/start/with/double/slash") );
        assertEquals("http://slash/segments/also",mkurl("http://slash","/segments","/","/also") );
    }

    @Test
    public void testOptionalString(){
        assertEquals(StringUtils.opt("uala").getOrNull(),"uala");
        assertNull(StringUtils.opt(null).getOrNull());
        assertEquals(StringUtils.opt("").orElse("uala").getOrNull(),"uala");
        assertNull(StringUtils.opt(null).orElse("").getOrNull());
    }

    @SuppressWarnings("serial")
    @Test
    public void testComposeProperties(){
        Map<String, String> props = new HashMap<String, String>(){{
            put("a", "0");
            put("b", "1");
            put("c", "2");
        }};
        String composed = StringUtils.composeProperties(props);
        Map<String,String> parsed = StringUtils.parseProperties(composed);
        assertEquals(props, parsed);
    }

}
