# Advanced Kotlin

## Extension functions & properties

Perhaps the most popular language feature in Kotlin are extensions. This language feature allows you to add methods and properties to types without modifying the type itself - which means that you can add them even to types that you don't own.

Extensions are essentially just syntax sugar over static utility functions, but the syntax they provide makes using them much more pleasant than their Java counterparts, as well as more discoverable through IDE tooling.

For example, let's add a new method to `String` which lets us greet someone by their name, like this:

```kotlin
"Samantha".greet() // Hello, Samantha
```

This extension function can be defined as such:

```kotlin
fun String.greet() {
    println("Hello, $this")
}
```

Here, `String.` before the function's name tells us that we're extending the `String` type. Since this function is "a method of `String`", we can access the instance that it was called on as `this` inside it. This object that we call our extension on is called the function's *receiver*.

Properties can be added as extensions in a similar fashion, for example, we could add a square root property to every `Double`:

```kotlin
val Double.sqrt
    get() = Math.sqrt(this)

val d = 169.0
println(d.sqrt) // 13
```

## Higher order functions

Kotlin treats functions as first-class citizens. We can define top-level functions that aren't wrapped in a class, and we have function types in the type system. For example, a function that takes a `String` and an `Int`, and then returns a `Double` would have the type `(String, Int) -> Double`.

Higher order functions are functions that either take other functions as parameters, or return functions. The first of these is the more common thing to do, so that's what we'll be looking at.

Let's write a function that can repeat a block of code a given number of times! We'll take two parameters, the repeat count and the block of code to execute, as a function that takes no parameters and returns `Unit`:

```kotlin
fun repeat(times: Int, block: () -> Unit) {
    for (i in 0 until times) {
        block()
    }
}
```

We can call this function like this, passing in the required function as a lambda:

```kotlin
repeat(3, { println("Sound check") })
```

Kotlin also allows us to move a lambda that's the last parameter to a function outside the parentheses of the parameter list, and if we reformat our code a bit...

```kotlin
repeat(3) { 
    println("Sound check") 
}
```

Our call to our function now looks quite similar to built-in language constructs.

Finally, let's create a function that always repeats the parameter it gets exactly 3 times - notice how we're reusing our previous function, passing the `block` parameter along to it.

```kotlin
fun repeatThrice(block: () -> Unit) = repeat(3, block)
```

If we call this function with a lambda, we can even drop the parentheses of the parameter list entirely, and end up with this syntax:

```kotlin
repeatThrice {
    println("Sound check") 
}
```

## Sealed classes

Sealed classes are a feature of the type system that lets you restrict type hierarchies. Classes that are marked with the `sealed` keyword are `open` (can be subclassed), but they can only be inherited from by classes that are declared in the same file as them.

This guarantees that whenever you come across an instance of this sealed class, its concrete type will be one of a very fixed, finite number of classes.

For example, you can represent the state of a screen with a sealed class:

```kotlin
sealed class UserScreenState

object Loading : UserScreenState()

class NetworkError(val message: String) : UserScreenState()

class UserLoaded(val username: String, val score: Int) : UserScreenState()
```

#### Combined with the `when` expression

The real power of sealed classes becomes obvious once we combine them with the `when` expression. When checking the type of a parameter that's an instance of a sealed class, `when` can guarantee that we've checked all possible subtypes exhaustively.

```kotlin
fun processState(state: UserScreenState) {
    when (state) {
        Loading -> showLoading()
        is NetworkError -> showErrorMessage(state.message)
        is UserLoaded -> showUser(state.username, state.score)
    }
}
```

This is actually only guaranteed if `when` is used as an *expression*, so this previous code example isn't quite right. In that function, the return value of `when` is not used, therefore it's just a statement. It only becomes an expression when it's forced to have a return value.

We can do this in a couple ways, for example, by returning the `when` statement from the function:

```kotlin
fun processState(state: UserScreenState) = when (state) {
    Loading -> showLoading()
    is NetworkError -> showErrorMessage(state.message)
    is UserLoaded -> showUser(state.username, state.score)
}
```

We'll look at another way of forcing a `when` to be an expression later on.

## Reified generics

In Java, whenever we use generics, we have to deal with [*type erasure*](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html). To oversimplify it: generic types only exist at compile time, and generic parameters are replaced with the `Object` type by the time they end up in the bytecode. This differs for example from the C++ implementation of generics, where separate, typed classes get generated for every type parameter that a generic class is used with.

This type erasure is the reason why we have trouble with...

- Telling apart a `List<Kitten>` and a `List<Tiger>` at runtime by performing `instanceof` checks on a `List` instance

- Getting the `.class` of a type parameter like we could with a regular type

    ```java
    System.out.println(String.class); // This works
    System.out.println(T.class);     
                     // ^ Error: Cannot select from a type variable
    ```
 
- Performing type checks against a type parameter
    
    ```java
    System.out.println("" instanceof String); // This works
    System.out.println("" instanceof T);
                                  // ^ Error: Class or array expected
    ```

So what are reified generics, and how do they help us with these issues in Kotlin? They are, essentially, a clever compile time trick. When we create a generic function in Kotlin, we can mark its type parameters as `reified`, but only if we also make the function itself `inline`. For example:

```kotlin
inline fun <reified T> printType(t: T) {
    println("I think '$t' is ${T::class}")
}
```

Accessing the generic parameter's `class` wouldn't normally be possible on the JVM, however, if we call this function, it works:

```kotlin
printType("hi")  // I think 'hi' is class kotlin.String
printType(Bar()) // I think 'Bar' is class foo.Bar
```

How is this possible? Remember, we can only make type parameters reified if our function is inlined - and here's where the magic happens. When we call an inline function, its body essentially get copied to the call site instead of a function call being performed. 

When this happens for a function that has a reified type parameter, all usages of that parameter in the body get replaced with the concrete type that the function was called with that specific time. For example, this is what our two previous calls basically compile to these lines, with the concrete values of the type parameters inlined:

```kotlin
println("I think '${"hi"}' is ${String::class}")
println("I think '${Bar()}' is ${Bar::class}")
```

Which are perfectly valid calls to perform on these concrete types. 

This handy transformation also lets us perform type checks such as `x is T` if `T` is reified. For example, this generic function can be easily implemented in Kotlin:

```kotlin
inline fun <reified R> List<*>.filterType(): List<R> {
    val result = mutableListOf<R>()
    for (element in this) {
        if (element is R) {
            result.add(element)
        }
    }
    return result
}
```

It's an extension on a `List` with an unknown (or any) type parameter, takes a reified parameter, and filters the original list to only those elements that are of the type `R`. Its usage would look like this:

```kotlin
val list = listOf(1, 'o', 992.5233, 2, 25.21, "foo", 17)
println(list.filterType<Double>()) // [992.5233, 25.21]
```

## Continue...

[Up next, coroutines.](./coroutines.md)
