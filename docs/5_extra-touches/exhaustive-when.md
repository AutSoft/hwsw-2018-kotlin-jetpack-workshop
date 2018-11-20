# Exhaustive `when`

`when` is an interesting construct in Kotlin. It can be used both as a statement and an expression, the difference being that in the latter case it has a return value.

If we use `when` as an expression, it will ensure that there is always a branch that's executed. Normally, this means that we have to add an `else` branch.

For example, this usage would be invalid, since there would be cases when it doesn't know what to assign to `result`:

```kotlin
val result = when(x) {
    1 -> "One"
    2, 3, 4 -> "Few"
}
```

But this one would be okay, because there's always a branch that runs and returns a value:

```kotlin
val result = when(x) {
    1 -> "One"
    2, 3, 4 -> "Few"
    else -> "Dunno"
}
```

## Forcing an expression

Let's take a look at the way we use `when` inside our `render` methods, for example, in `JobDetailFragment`:

```kotlin
override fun render(viewState: JobDetailViewState) {
    when (viewState) {
        is Loading -> {
            viewFlipper.displayedChild = Flipper.LOADING
        }
        is JobDetailLoaded -> {
            viewFlipper.displayedChild = Flipper.READY
            showJobDetails(viewState.detailedJob)
        }
    }
}
```

This `when` doesn't return a value, so it's a statement. This is a problem, because we don't get the benefit of being forced to handle all possible cases, and might miss one here. We could return the value of `when` from this method, but depending on what happens to be the last expression in each branch, we might be leaking random information as the return value of `render` by doing so.

```kotlin
override fun render(viewState: JobDetailViewState) = when (viewState) {
    is Loading -> {
        viewFlipper.displayedChild = Flipper.LOADING
    }
    is JobDetailLoaded -> {
        viewFlipper.displayedChild = Flipper.READY
        showJobDetails(viewState.detailedJob)
    }
}
```

Instead of returning the value of `when` like this, it's enough to use it within this function for it to be an expression. We could assign it to a variable, but better still, let's call something on it. If we make a call like this, `when` will _need to be_ an expression:

```kotlin
when { 
    ... 
}.foo()
``` 

What's something we can both call on it and that would be sensible to call here? We can just write our own well-named extension for this single case, and this has already been done for us in the `Extensions.kt` file inside the `arch` package. It's an extension property named `exhaustive`, and it does nothing but return whatever it was called on. Its usage looks like this:

```kotlin
override fun render(viewState: JobDetailViewState) {
    when (viewState) {
        is Loading -> {
            viewFlipper.displayedChild = Flipper.LOADING
        }
        is JobDetailLoaded -> {
            viewFlipper.displayedChild = Flipper.READY
            showJobDetails(viewState.detailedJob)
        }
    }.exhaustive
}
```

We've now have a way to make sure that a `when` has exhaustive branches. 

One more thing to mention here... 

## Why sealed classes?

Notice that if we make type checks against a sealed class and we check every possible case, `when` will be nice enough to let us omit the `else` branch that would never be executed anyway. Remember, sealed classes have only their predefined subclasses, so any instance we come across _must be_ an instance of one of these.

This is where the true power of sealed classes lies. If we were to add a new member to a sealed class hierarchy, any `when` _expressions_ (and only the expressions!) doing type checks against this sealed class hierarchy would break at compile time and force us to handle this new type. (Try it with one of the view states!)

## Continue...

[A tiny improvement by a handy library extension, coming up next.](./simpler-view-visibilities.md)
