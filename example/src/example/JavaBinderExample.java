package example;

import com.github.mibac138.argparser.binder.Binder;
import com.github.mibac138.argparser.binder.Binding;
import com.github.mibac138.argparser.parser.Parser;
import com.github.mibac138.argparser.parser.SimpleParserRegistry;
import com.github.mibac138.argparser.reader.ArgumentReader;

import static com.github.mibac138.argparser.reader.ArgumentReaderUtil.asReader;

/**
 * Created by mibac138 on 04-05-2017.
 */
public class JavaBinderExample {
	public static void main(String[] args) throws NoSuchMethodException {
		Binding binding = Binder.bind(new JavaBinderExample(), JavaBinderExample.class.getMethod("lendMoney",
				String.class, int.class));
		ArgumentReader reader = asReader("Luke 100");
		Parser parser = new SimpleParserRegistry();
		
		boolean lent = (boolean) binding.invoke(reader, parser);
		if (lent)
			System.out.println("Money lent!");
		else
			System.out.println("Didn't lend money");
	}
	
	public boolean lendMoney(String name, int amount) {
		if (amount < 200) {
			System.out.println("Lent $" + amount + " to " + name);
			return true;
		} else {
			System.out.println("Didn't lend $" + amount + " to " + name);
			return false;
		}
	}
}
