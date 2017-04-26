package com.github.mibac138.argparser.syntax;

import com.github.mibac138.argparser.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mibac138 on 06-04-2017.
 */
public class SyntaxBuilder {
	@NotNull
	public static SyntaxBuilder start() {
		return new SyntaxBuilder();
	}
	
	@NotNull
	private List<SyntaxElement<?>> syntax = new ArrayList<SyntaxElement<?>>();
	
	@NotNull
	public <T> SyntaxBuilder appendType(@NotNull Class<T> type) {
		Objects.requireNonNull(type);
		syntax.add(new BasicSyntaxElement<T>(type, null));
		return this;
	}
	
	@NotNull
	public <T> SyntaxBuilder appendOptionalType(@NotNull Class<T> type, @NotNull T defaultValue) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(defaultValue);
		syntax.add(new BasicSyntaxElement<T>(type, defaultValue));
		return this;
	}
	
	@NotNull
	public SyntaxBuilder append(@NotNull SyntaxElement element) {
		syntax.add(Objects.requireNonNull(element));
		return this;
	}
	
	@NotNull
	public SyntaxContainer build() {
		return new BasicParserSyntax<Object>(syntax, Object.class, null);
	}
}
