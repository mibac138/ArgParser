# ArgParser

[![Build Status](https://travis-ci.org/mibac138/ArgParser.svg?branch=master)](https://travis-ci.org/mibac138/ArgParser) 

[Javadoc](https://mibac138.github.io/ArgParser/)
## Getting started

1. Add ArgParser to your project:
```groovy
// Core
compile 'com.github.mibac138.argparser:argparser-core:1.0.0'
// Binder (not required)
compile 'com.github.mibac138.argparser:argparser-binder:1.0.0'
```

2. Bind a method:
- in Java:
```java
public class Example {

    public Example() {
    	Binding binding = Binder.bind(this).get("method");
    	
    	// Invoke binding
    	binding.invoke(asReader("text true"), new SimpleParserRegistry());
    }

    @BindMethod("method")
    public void method(String input1, boolean someBoolean) {
    	System.out.println("Input: " + input1);
    	System.out.println("Bool:  " + someBoolean);
        /* ... */
    }
}
```
- in Kotlin:
```kotlin
fun main(args: Array<String>) {
  val binding = Binder.bind(::method)
  
  // Invoke binding
  binding.invoke("text true".asReader(), SimpleParserRegistry())
}

fun method(input1: String, someBoolean: Boolean) {
    println("Input: $input1")
    println("Bool:  $someBoolean")
    /* ... */
}
```

### Which Parser to use?
- if you want a simple parser for only unnamed arguments (ex. `string boolean`) use `SimpleParserRegistry`
- if you want only named arguments (ex. `--value: key --bool=true`) use `NamedParserRegistry`
- if you want to combine these 2 options use `MixedParserRegistry`


## Communication

- [GitHub Issues](https://github.com/mibac138/ArgParser/issues)
- [Discord](https://discord.gg/9wxjQuv)

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/mibac138/ArgParser/issues).

 
## License

Copyright (c) 2017 Michał Bączkowski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.