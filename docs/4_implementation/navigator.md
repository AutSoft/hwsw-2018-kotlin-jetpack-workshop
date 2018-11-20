# Navigator implementation

As we've seen, our `MainActivity` currently has adding `JobListFragment` when it's first created hardcoded into it. Let's make some changes to get it ready for navigation between our `Fragment`s.

The architecture provides a `Navigator` interface for us to implement, which will be very easy to invoke later from `Fragment`s. It looks something like this:

```kotlin
interface Navigator {

    fun add(fragment: Fragment)
    fun replace(fragment: Fragment)
    fun pop()

}
```

This version of it has just a few operations so that we can easily implement it. We'll think about our app as a stack of screens (not the `Fragment` backstack, a _real_ stack). We can:

- `add`: put new screens on the top,
- `replace`: replace the screen on top with another one, 
- `pop`: remove the screen on top. 

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

We can reuse this code for `replace`, only in this case, we won't add the transaction to the `Fragment` backstack - this essentially loses the `Fragment` we had on top already, which is what we want here. 

```kotlin
override fun replace(fragment: Fragment) {
    supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContent, fragment)
            .commit()
}
```

`pop` is the simplest to implement:

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
