package CafeMangementSystem.Utils;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class BigDecimalTextFormatter extends TextFormatter<BigDecimal> {
    private static Pattern defaultValidEditingState1 = Pattern.compile("(([1-9][0-9]{0,6})|0)?(\\.[0-9]{0,3})?");
    public static UnaryOperator<TextFormatter.Change> defaultFilter1 = c -> {
        String text = c.getControlNewText();
        if (defaultValidEditingState1.matcher(text).matches()) {
            return c ;
        } else {
            return null ;
        }
    };
    private static Pattern defaultValidEditingState2 = Pattern.compile("(([1-9][0-9]{0,6})|0)?");
    public static UnaryOperator<TextFormatter.Change> defaultFilter2 = c -> {
        String text = c.getControlNewText();
        if (defaultValidEditingState2.matcher(text).matches()) {
            return c ;
        } else {
            return null ;
        }
    };
    public static StringConverter<BigDecimal> defaultConverter = new StringConverter<BigDecimal>() {
        @Override
        public BigDecimal fromString(String s) {
            if(s.isEmpty()){
                return null;
            }else if (".".equals(s)) {
                return new BigDecimal(0.0);
            } else {
                return new BigDecimal(s);
            }
        }
        @Override
        public String toString(BigDecimal d) {
            if(d == null) return "";
            return d.toPlainString();
        }
    };

    public BigDecimalTextFormatter(StringConverter<BigDecimal> stringConverter, BigDecimal bigDecimal, UnaryOperator<Change> unaryOperator) {
        super(stringConverter, bigDecimal, unaryOperator);
    }
}
