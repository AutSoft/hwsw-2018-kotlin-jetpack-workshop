# Enforcing correct `newInstance` usage

What's even better than runtime exceptions which let us correct mistakes during development? IDE errors which show up immediately when we type the incorrect code. Normally, you'd have to write custom lint rules to provide these in your own code. That is, until now...

## Annotate all the things

We can use Kotlin's great support for deprecating code to signal that our `JobDetailFragment`'s constructor should never be called. We could put this deprecation in the header of the class on the primary constructor, but it would look very messy. Instead of doing this, we'll make the empty constructor a secondary constructor inside the class:

```kotlin
@Deprecated(
    message = "Use newInstance instead", 
    replaceWith = ReplaceWith("JobDetailFragment.newInstance()")
)
constructor()
```

We can not only provide a handy warning message, but the code that should replace a call to this constructor if someone attempts to call it as well! We'll look at this in a moment, but we have some small fixes to make after this change.

Since we no longer allow the empty primary constructor, we don't need to make a call to our superclass constructor in the class header, so we'll remove the `()` signifying this. The class header should now look like this:

```kotlin
class JobDetailFragment : BaseFragment<JobDetailViewState, JobDetailViewModel> {
```

We can clean up some warnings in our `Fragment` by adding more annotations. The IDE will keep suggesting to convert our secondary constructor to a primary one, we can get rid of this by annotating it like so:

```kotlin
@Suppress("ConvertSecondaryConstructorToPrimary")
@Deprecated(
        message = "Use newInstance instead",
        replaceWith = ReplaceWith("JobDetailFragment.newInstance()")
)
constructor()
``` 

In the `newInstance` method we're calling this constructor that we ourselves have marked as deprecated. There's no need to warn us, this will be the only safe place to call the `Fragment` constructor directly, so we can suppress the warning about it:

```kotlin
@Suppress("DEPRECATION")
fun newInstance(jobListingId: String): JobDetailFragment {
    return JobDetailFragment().applyArgs {
        putString(JOB_LISTING_ID, jobListingId)
    }
}
```

## Back to navigation

We create the instance of `JobDetailFragment` in `JobListFragment`'s `onJobListingSelected` method. We've done this correctly the first time around, but let's mess this code up on purpose so that we can see how our deprecation works in practice.

Replace the existing code in this method that uses `newInstance` correctly with the following direct constructor call:

```kotlin
navigator?.add(JobDetailFragment())
```

You'll see the constructor call marked with the deprecation warning we've just added. If you invoke intentions on it (Alt+Enter on Windows, Option+Enter on Mac), you'll get an action which suggests _Replace with `JobDetailFragment.newInstance()`_. This is generated from the `replaceWith` parameter we provided earlier. Let's use it!

Since the `newInstance` function takes a parameter, we're now forced to provide it. This ensures that any arguments the `Fragment` requires will be provided whenever it's created. 

We can pass in the ID we got in the callback, like we initially did:

```kotlin
override fun onJobListingSelected(jobListingId: String) {
    navigator?.add(JobDetailFragment.newInstance(jobListingId))
}
```

## Continue...

[Remember `when`...?](./exhaustive-when.md)
