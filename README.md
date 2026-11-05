# Dokka 2.3.0: Java sources cannot resolve Kotlin classes from the classpath

Module `a` declares `a.KotlinBase` (Kotlin) and `a.JavaBase` (Java).
Module `b` depends on `a` and contains Java sources that extend these interfaces and link them in Javadoc,
plus a Kotlin class implementing the Java interfaces.

In Dokka 2.3.0, references from **Java sources** to **Kotlin-compiled classes on the classpath**
(both from another module, `a.KotlinBase`, and from the Kotlin standard library, `kotlin.Unit`) are not resolved.
References to Java-compiled classes on the classpath (`a.JavaBase`) still work.
The broken supertype also propagates to Kotlin sources implementing the Java interface.

Dokka 2.2.0 resolves all of them, with both the K1 analysis and the K2 one (`org.jetbrains.dokka.experimental.tryK2`).

## Reproduce

```shell
./gradlew :b:dokkaGeneratePublicationHtml
```

fails (`failOnWarning` is enabled) with:

```
w: [:b:dokkaGeneratePublicationHtml] `KotlinBase` is unresolved in file:///.../b/src/main/kotlin/b/Impl.kt:6:7
w: [:b:dokkaGeneratePublicationHtml] Couldn't resolve JavaDoc link 'KotlinBase' in file:///.../b/src/main/java/b/FromKotlin.java:6:21
w: [:b:dokkaGeneratePublicationHtml] Couldn't resolve JavaDoc link 'kotlin.Unit' in file:///.../b/src/main/java/b/FromKotlin.java:6:89
```

No warning is reported for `JavaBase`.

## Rendered output

Compare `b/build/dokka/html/b/b/-from-kotlin/index.html` across versions:

```shell
./gradlew :b:dokkaGeneratePublicationHtml -PfailOnWarning=false   # 2.3.0
./gradlew :b:dokkaGeneratePublicationHtml -PdokkaVersion=2.2.0    # 2.2.0, K1
./gradlew :b:dokkaGeneratePublicationHtml -PdokkaVersion=2.2.0 \
  -Porg.jetbrains.dokka.experimental.tryK2=true \
  -Porg.jetbrains.dokka.experimental.tryK2.noWarn=true            # 2.2.0, K2
```

| Reference from Java source             | 2.2.0 (K1)                | 2.2.0 (K2)                | 2.3.0                  |
|----------------------------------------|---------------------------|---------------------------|------------------------|
| `{@link KotlinBase}` (Kotlin, module)  | resolved                  | resolved                  | plain text, no DRI     |
| `{@link kotlin.Unit}` (Kotlin, stdlib) | link to kotlinlang.org    | link to kotlinlang.org    | plain text             |
| `{@link JavaBase}` (Java, module)      | resolved                  | resolved                  | resolved               |

(`KotlinBase` and `JavaBase` render as unresolved-link spans with a DRI when resolved,
since module `a` is not part of this publication.)

Possibly related: Kotlin/dokka#4583 (annotations written in Kotlin and taken from the classpath are dropped from Java sources).

## Environment

- Dokka Gradle Plugin 2.3.0 (DGPv2)
- Kotlin 2.4.21
- Gradle 9.8.2
- JDK 25
