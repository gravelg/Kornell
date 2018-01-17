package kornell.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

public class StringUtils {

    private static final Logger logger = Logger.getLogger(StringUtils.class.getName());
    private static final Sha1 sha1 = new Sha1();

    public static class URLBuilder {

        private String base;
        private String[] segments;
        private Map<String, String> params = new HashMap<String, String>();

        public URLBuilder(String base) {
            this.base = base;
        }

        public URLBuilder withPath(String... segments) {
            this.segments = segments;
            return this;
        }

        public URLBuilder withParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        public String build() {
            StringBuilder url = new StringBuilder();
            url.append(composeURL(base, segments));
            url.toString();
            if (!params.isEmpty()) {
                url.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    url.append(entry.getKey());
                    url.append("=");
                    url.append(entry.getValue());
                }
            }
            return url.toString();
        }
    }

    public static URLBuilder with(String base) {
        return new URLBuilder(base);
    }

    /**
     * Concatenates segments into a single-slashed url or path
     */
    public static String mkurl(String base, String... path) {
        return composeURL(base, path);
    }

    /**
     * @deprecated Prefer mkurl()
     */
    @Deprecated
    public static String composeURL(String base, String... path) {
        StringBuffer buf = new StringBuffer();
        buf.append(base);
        if (path != null && path.length > 0) {
            for (int i = 0; i < path.length; i++) {
                String segment = trimSlashes(path[i]);
                if (isSome(segment)) {
                    if ((buf.length() > 0) && (buf.charAt(buf.length() - 1) != '/'))
                        buf.append("/");
                    buf.append(segment);
                }
            }
        }
        return buf.toString();
    }

    public static String trimSlashes(String segment) {
        if (isSome(segment)) {
            if (segment.startsWith("/"))
                return trimSlashes(segment.substring(1));
            else if (segment.endsWith("/"))
                return trimSlashes(segment.substring(0, segment.length() - 1));
            else
                return segment;
        } else
            return segment;
    }

    public static final boolean isNone(String str) {
        return str == null || "".equals(str);
    }

    public static final boolean isSome(String str) {
        return !isNone(str);
    }

    public static String digitsOf(String orig) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < orig.length(); i++) {
            char ch = orig.charAt(i);
            if (Character.isDigit(ch))
                buf.append(ch);
        }
        return buf.toString();
    }

    public static OptionString opt(String str) {
        if (isSome(str))
            return new SomeString(str);
        else
            return new NoneString();
    }

    public interface OptionString {

        OptionString orElse(String other);

        String getOrNull();
    }

    public static class SomeString implements OptionString {

        String str;

        public SomeString(String str) {
            this.str = str;
        }

        @Override
        public OptionString orElse(String other) {
            return this;
        }

        @Override
        public String getOrNull() {
            return str;
        }
    }

    public static class NoneString implements OptionString {

        @Override
        public OptionString orElse(String other) {
            return opt(other);
        }

        @Override
        public String getOrNull() {
            return null;
        }

    }

    public static boolean noneEmpty(Object... objs) {
        for (Object obj : objs) {
            if (obj == null || obj.toString().length() == 0)
                return false;
        }
        return true;
    }

    private static String i2s(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    public static String isoNow() {
        Date now = new Date();
        return toISO(now);
    }

    // TODO: Differentiate client and server date formatting
    @SuppressWarnings("deprecation")
    public static String toISO(Date date) {
        if (date == null)
            return "";
        String year = i2s(1900 + date.getYear());
        String month = i2s(date.getMonth() + 1);
        String day = i2s(date.getDate());

        String hour = "00";
        String min = "00";
        String sec = "00";
        try {
            hour = i2s(date.getHours());
            min = i2s(date.getMinutes());
            sec = i2s(date.getSeconds());
        } catch (Exception e) {
            logger.warning("Could not get time from date, returning start of day.");
        }

        String result = year + "-" + month + "-" + day + "T" + hour + ":" + min + ":" + sec;
        return result;
    }

    private static final String HASH_SEP = "|#|";

    public static String hash(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(HASH_SEP);
        }
        return sha1.hex_sha1(builder.toString());
    }

    public static class RepositoryString {

        private String value;
        private String repositoryUUID;
        private String key;

        public String getKey() {
            return key;
        }

        public String getRepositoryUUID() {
            return repositoryUUID;
        }

        public RepositoryString(String value) {
            this.value = value;
            int idx = value.indexOf("/");
            if (idx > 0) {
                this.repositoryUUID = value.substring(0, idx);
                this.key = value.substring(idx + 1);
            } else {
                this.repositoryUUID = value;
                this.key = "";
            }
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public static RepositoryString parseRepositoryData(String str) {
        RepositoryString result = new RepositoryString(str);
        return result;
    }

    @SuppressWarnings("all")
    static final Map<String, String> mimeTypes = new HashMap<String, String>() {

        {
            put(".txt", "text/plain");
            put(".html", "text/html");
            put(".js", "application/javascript");
            put(".css", "text/css");
        }
    };

    public static final String getMimeType(String key) {
        String ext = key.substring(key.lastIndexOf("."));
        return mimeTypes.get(ext);
    }

    private static final String SEP = "--";

    public static Map<String, String> parseProperties(String str) {
        Map<String, String> result = new HashMap<>();
        String[] split = str.split(SEP);
        for (String token : split) {
            String[] kv = token.split("=");
            if (kv.length == 2)
                result.put(kv[0], kv[1]);
            else
                logger.warning("Could not parse [" + token + "]");
        }
        return result;
    }

    public static String composeProperties(Map<String, String> props) {
        StringBuilder bud = new StringBuilder();
        Set<Entry<String, String>> entrySet = props.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (bud.length() > 0)
                bud.append(SEP);
            bud.append(entry.getKey());
            bud.append("=");
            bud.append(entry.getValue());
        }
        return bud.toString();
    }

    public static String[] parseStrings(String str) {
        return str.split(SEP);
    }

    public static String composeStrings(String[] strs) {
        StringBuilder bud = new StringBuilder();
        for (String string : strs) {
            if (bud.length() > 0)
                bud.append(SEP);
            bud.append(string);
        }
        return bud.toString();
    }

    @SuppressWarnings("serial")
    public static void main(String[] args) {
        Map<String, String> props = new HashMap<String, String>() {

            {
                put("a", "0");
                put("b", "1");
                put("c", "2");
            }
        };
        System.out.println(StringUtils.composeProperties(props));
    }

}
