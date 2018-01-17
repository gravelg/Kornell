package kornell.core.scorm12.rte;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DataType {

    public static final DataType CMITimespan = new DataType();

    public static final DataType UUID = new DataType();

    public static DataType EITHER(DataType... ors) {
        return new DataType(ors);
    }

    public static DataType CMIBlank = new DataType();

    public static DataType CMIString255 = new DataType() {

        protected boolean isInstance(String value) {
            return value == null || value.length() <= 255;
        };
    };

    public static DataType CMIIdentifier = CMIString255;

    public static DataType CMIString4096 = new DataType() {

        protected boolean isInstance(String value) {
            return value == null || value.length() <= 4096;
        };
    };

    public static DataType CMIDecimal = new DataType() {

        public boolean isInstance(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    };

    Set<DataType> ors = new HashSet<>();
    Set<String> words = new HashSet<>();

    public DataType(String... words) {
        this.words.addAll(Arrays.asList(words));
    }

    public DataType(DataType[] ors) {
        this.ors.addAll(Arrays.asList(ors));
    }

    public static DataType CMIVocabulary(String... words) {
        return new DataType(words);
    }

    public boolean check(String value) {
        boolean isVocab = !words.isEmpty();
        boolean valueNotDeclared = !words.contains(value);
        if (isVocab && valueNotDeclared) {
            return false;
        } else
            return isInstance(value);
    }

    protected boolean isInstance(String value) {
        return true;
    }
}
