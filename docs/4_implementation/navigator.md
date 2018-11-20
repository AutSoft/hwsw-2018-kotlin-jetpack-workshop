# Navigator implementation

As we've seen, our `MainActivity` currently has adding `JobListFragment` when it's first created hardcoded into it. Let's make some changes to get it ready for navigation between our `Fragment`s.

The architecture provides a `Navigator` interface for us to implement, which will be very easy to invoke later from `Fragment`s. It looks something like this:

```kotlin
interface Navigator {

    fun add(fragment: Fragment)
    fun pop()

}
```

This version of it has just these two operations so that we can easily implement it. (You could of course add others onto it later!)

We'll think about our app as a stack of screens (not the `Fragment` backstack, a _real_ stack). Through this interface, we can:

- `add`: put a screen on the top of the stack,
- `pop`: remove the screen currently on top.

We'll add the `Navigator` interface to `MainActivity`'s supertypes, and generate the necessary methods.

For `add`, we can use the implementation already in `onCreate`, modified to add the `Fragment` we get as a parameter, and not just a `JobListFragment` every time:

```kotlin
override fun add(fragment: Fragment) {
    supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContent, fragment)
            .addToBackStack(null)
            .commit()
}
```

With this done, we can update our `onCreate` method to use `add`:

```kotlin
if (savedInstanceState == null) {
   add(JobListFragment())
}
```

From here, `pop` is very simple to implement:

```kotlin
supportFragmentManager.popBackStack()
```

Finally, we should handle back presses nicely as well:

```kotlin
override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount > 1) {
        pop()
    } else {
        finish()
    }
}
```

If we forgot this, we could remove our first `Fragment` and be left with a blank `MainActivity` still open!

## Continue...

[Time to create some state.](./viewstate-and-presenter.md)
