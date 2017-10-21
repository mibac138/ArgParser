package example;

import com.github.mibac138.argparser.parser.SimpleParserRegistry;
import com.github.mibac138.argparser.reader.ArgumentReader;
import com.github.mibac138.argparser.syntax.SyntaxElement;

import java.util.Map;

import static com.github.mibac138.argparser.reader.ArgumentReaderUtil.asReader;
import static com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSLCreator.element;
import static com.github.mibac138.argparser.syntax.dsl.SyntaxContainerDSLCreator.syntaxContainer;

/**
 * Created by mibac138 on 03-05-2017.
 */
public class JavaExample {
    /**
     * A rather basic example. Uses only core. Kind of uncomfortable.
     * Please check JavaBinderExample for a more pleasant
     * solution
     */
    public static void main(String[] args) {
        JavaExample example = new JavaExample();
        SimpleParserRegistry parser = new SimpleParserRegistry();
        ArgumentReader reader = asReader("Luke 100");
        SyntaxElement syntax = syntaxContainer(container -> {
            element(container, String.class);
            element(container, int.class);
        });
        
        Map<Integer, ?> result = parser.parse(reader, syntax);
        example.lendMoney(((String) result.get(0)), ((Integer) result.get(1)));
    }
    
    private void lendMoney(String name, int amount) {
        System.out.println("Lent $" + amount + " to " + name);
    }
}
