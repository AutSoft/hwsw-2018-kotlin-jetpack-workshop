# Fragment, pt I: Creation and arguments

Finally we can create our Fragment, our Detail screen itself! 

## Basics

We'll create the `JobDetailFragment` class, and make it a subclass of `BaseFragment` with the appropriate type parameters for the view state (`VS`) and ViewModel (`VM`).

```kotlin
class JobDetailFragment : BaseFragment<JobDetailViewState, JobDetailViewModel>()
```

We'll be forced to implement a couple abstract methods from the base class, let's generate these. We can already implement `provideViewModel` the same way as we've seen it implemented in `JobListFragment`, using the `viewModelFactory` in one of our superclasses:

```kotlin
override fun provideViewModel(): JobDetailViewModel {
    return ViewModelProviders.of(this, viewModelFactory).get(JobDetailViewModel::class.java)
} 
```

We'll get back to implementing `render` later.

Let's override the `onCreateView` method and inflate the Fragment's layout - this is already in the project, in the `fragment_job_detail.xml` file:

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_job_detail, container, false)
}
```

## Adding Arguments

We need to pass an argument to our Fragment, the ID of the job to load, which is a single `String`. Let's create a property that will hold this - we'll make it non-nullable as it's not optional to provide when opening the screen, so we'll definitely initialize it from our argument `Bundle`.

```kotlin
private var jobListingId: String = ""
```

It's good practice to provide a static factory method for creating `Fragment` instances that take parameters, since building their argument `Bundle` on every call site is error prone. Since we're using Kotlin, instead of a static method, we'll create a companion object, and a method inside that:

```kotlin
companion object {
    private const val JOB_LISTING_ID = "JOB_LISTING_ID"

    fun newInstance(jobListingId: String): JobDetailFragment {
        return JobDetailFragment().applyArgs {
            putString(JOB_LISTING_ID, jobListingId)
        }
    }
}
```

We've added a `String` constant that will serve as the key of our job ID argument within our argument `Bundle`. 

The `applyArgs` extension function used here creates an argument `Bundle`, applies the actions described in the lambda it takes to that `Bundle`, and sets the `Bundle` as the given `Fragment`'s arguments. Finally, it returns the `Fragment` it was called on.

We should now implement the other side of this argument passing mechanism, where we read the arguments from the `Bundle` received. Let's create an `initArguments` method (this is a regular instance method, and doesn't belong in the companion):

```kotlin
private fun initArguments() {
    jobListingId = requireArguments().requireString(JOB_LISTING_ID)
}
```

The `requireArguments` and `requireString` methods here free us of having to deal with the possible `null` values of the `arguments` `Bundle` itself, and then the `String` value within it for our key. If any of these things happen to be `null`, these methods will throw easy to read exceptions.

This is very similar to how the [`requireContext`](https://developer.android.com/reference/android/support/v4/app/Fragment#requireContext()) and [`requireActivity`](https://developer.android.com/reference/android/support/v4/app/Fragment#requireActivity()) methods of the Fragment API already work - these were introduced in Support Library 27.1.0 to make Kotlin usage easier.

Think about it: we have no good recovery in these situations, and they're not an unexpected state of the application based on some input. If the screen has been started without these in place, it's a mistake in the code that starts it, which should be fixed during development. A crash makes sure that we'll do this.

Let's not forget to call this method now that we have it. `onViewCreated` is a good place for this:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initArguments()
    
    // TODO init Views
}
```

This way, if any View initialization happens to depend on arguments, they'll already be written into properties, ready to use.

## Navigating

Let's actually create an instance using all this code now. `JobListFragment` already has the appropriate callback method (`onJobListingSelected`) set up that's called by the `RecylerView` adapter every time a job is selected in the list. This method has been throwing `Toast` messages so far.

Replace the code that creates the message with the following, using our `newInstance` method, and passing the job ID to it:

```kotlin
navigator?.add(JobDetailFragment.newInstance(jobListingId))
```

Where is this `navigator` property coming from? If you navigate to it (pun intended), you'll see that it's an _extension property_ available on every `Fragment`, and all it does is return the `Activity` the `Fragment` is currently in, cast to `Navigator`. This is a loosely coupled way to have easy access to the `Navigator`, which avoids having to add this property directly to the base class.

## Continue...

[We're nowhere near done yet, time to populate our screen with data.](./fragment-data.md)
