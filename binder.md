# ArgParser Binder: Simple and comfy
Binder is a layer on top of ArgParser to make working with it easier and more comfortable

- [Documentation](https://mibac138.github.io/ArgParser/javadoc/binder)
- [Source](https://github.com/mibac138/ArgParser/tree/master/binder)

## Getting started

1. Add ArgParser Binder to your project
```groovy
compile 'com.github.mibac138.argparser:argparser-binder:1.0.0'
```
2. Bind your method(s). You have several options at this point:
- bind a specific method (`Binder.bind(object, object.class.getMethod("name", /* params */))`),
- bind a specific method using Kotlin (`Binder.bind(object::method)`),
- or bind the whole class (`Binder.bind(object)`, every method meant to be bound must be annotated with `@BindMethod`)

3. Invoke your binding
- from Java
```java
binding.invoke(asReader("args"), /* parser */ new SimpleParserRegistry())
 ```
 - from Kotlin
 ```kotlin
 binding.invoke("args".asReader(), /* parser */ SimpleParserRegistry())
 ```

## Comunication
- [Github Issues](https://github.com/mibac138/ArgParser/issues)
- [Discord](https://discord.gg/9wxjQuv)






[Main site](https://mibac138.github.io/ArgParser/)