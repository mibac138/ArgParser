package com.github.mibac138.argparser.syntax;

import com.github.mibac138.argparser.Parser;
import com.github.mibac138.argparser.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public <T> SyntaxElementBuilder<T> appendComplex() {
		return new SyntaxElementBuilder<T>(this);
	}
	
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
	
	/* SYNTAX ELEMENT BUILDER */
	
	public static final class SyntaxElementBuilder<T> implements RequiredBuilder<T> {
		@NotNull
		private SyntaxBuilder builder;
		
		@Nullable
		private T defaultValue = null;
		@Nullable
		private Class<T> outputType;
		@Nullable
		private Parser parser;
		
		private SyntaxElementBuilder(@NotNull SyntaxBuilder builder) {this.builder = Objects.requireNonNull(builder);}
		
		@NotNull
		public SyntaxElementBuilder<T> ofType(@NotNull Class<T> outputType) {
			this.outputType = Objects.requireNonNull(outputType);
			return this;
		}
		
		@NotNull
		public RequiredBuilder required() {
			return this;
		}
		
		@NotNull
		public SyntaxBuilder build() {
			Objects.requireNonNull(outputType);
			
			if (parser == null)
				return builder.append(new BasicSyntaxElement<T>(outputType, defaultValue));
			else
				return builder.append(new CustomParsedSyntaxElement<T>(outputType, defaultValue, parser));
		}
		
		@NotNull
		public SyntaxElementBuilder<T> withCustomParser(@Nullable Parser parser) {
			this.parser = parser;
			return this;
		}
		
		@NotNull
		public SyntaxElementBuilder<T> withoutCustomParser() {
			this.parser = null;
			return this;
		}
		
		@NotNull
		@Override
		public SyntaxElementBuilder<T> defaultValue(@NotNull T value) {
			this.defaultValue = Objects.requireNonNull(value);
			return this;
		}
	}
	
	public interface RequiredBuilder<T> {
		@NotNull
		SyntaxElementBuilder defaultValue(@NotNull T value);
	}
}
