package com.github.mibac138.argparser.binder;

import com.github.mibac138.argparser.GlobalParserRegistry;
import com.github.mibac138.argparser.reader.ArgumentReaderKt;
import org.junit.Test;

/**
 * Created by mibac138 on 14-04-2017.
 */
public class BindingTest2 {
	@Test
	public void test() throws NoSuchMethodException {
		Object o = new Object() {
			public void method(@Arg Integer num, @Arg String hi) {
				System.out.println("Num: " + num + "  hi: " + hi);
			}
		};
		
		
		Binding binding = Binder.bind(o, o.getClass().getMethod("method", Integer.class, String.class));
		binding.invoke(ArgumentReaderKt.asReader("10 hi!"), GlobalParserRegistry.Companion.getINSTANCE());
	}
}
