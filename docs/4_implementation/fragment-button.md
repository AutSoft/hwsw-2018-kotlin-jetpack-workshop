# Fragment, pt III: Buttons and events

We still have an _Apply_ button that doesn't work. We'll make this button open the URL contained in the job listing in the browser.

## Problem statement and theory

The flow for this will be the following:

1. The user presses the _Apply_ button.
2. The click listener in the `Fragment` calls the `ViewModel`.
3. The `ViewModel` fetches the URL for the current job from the lower layers in a coroutine.
4. The `ViewModel` _somehow_ notifies the `Fragment` that the URL it fetched should be opened for browsing...

This last part is what we need to tackle first, the rest will be fairly straightforward based on the things we've already done.

So far, when a `ViewModel` needed to communicate with its `Fragment`, we did so by updating the state, which triggered the `Observer` in the `Fragment`. This URL, however, isn't _really_ a part of our view state, for example, it's not displayed on the UI. 

We could somehow work it into view state classes, and have it be a `null` value by default, and set it to the actual URL after we've fetched it to signal that we should navigate to it, and handle this in `render`. But then if we come back to the `Fragment` again, `render` will run, and the actual URL will still be in the state object, triggering navigation again... It would get messy, because we'd be breaking the semantics of view state.

What we need here instead is a way for the `ViewModel` to notify the `Fragment` a single time of something that it should do, without changing its persistent state. This URL being fetched is a contrived example to fit our demo application. A network error Toast message to display, or the need to navigate away from the current screen to the next one after a long-running background save has completed in an asynchronous coroutine would be the more common examples of when we need this sort of communication.

## Events

Enter _events_, the architecture's solution to this problem. We'll use a special `LiveData` implementation which doesn't persistently store the data, but instead only delivers it a single time, to a single `Observer`. This will still happen in a lifecycle-safe manner, however.

This mechanism is already set up in `BaseViewModel` and `BaseFragment`. 

In `BaseViewModel`, there's an instance of `SingleShotLiveData` stored in a private property, and exposed through the `LiveData` interface in a public property. There's also a helper method for `ViewModel` subclasses to call in order to post events into this `LiveData`.

```kotlin
private val viewEvents = SingleShotLiveData<OneShotEvent>()

val events: LiveData<OneShotEvent> = viewEvents

protected fun postEvent(event: OneShotEvent) = viewEvents.postValue(event)
```

This `LiveData` is observed in `BaseFragment` the same way that state changes are, in `onViewCreated`:

```kotlin
viewModel.events.observe(viewLifecycleOwner, Observer { event ->
    event?.let { onEvent(it) }
})
```
 
The `onEvent` method is the equivalent of the `render` method for this flow, except it's not mandatory to override it, as not all `Fragment`s need event support.

Finally, `OneShotEvent` is nothing but a marker interface that concrete events should implement for some increased type safety and code legibility. We _could_ be posting `Any` typed objects as events as well if we wanted to.

## Implementation

Let's create an event class nested inside our `JobDetailViewModel`, implementing the marker interface:

```kotlin
class BrowseUrlEvent(val url: String) : OneShotEvent
``` 

It has a single parameter, the URL to browse. If it had no parameters, we could make it an `object`, just like we do with certain state classes.

We'll add a `browse` method to our `JobDetailViewModel` which fetches the URL of a given job, and then posts an instance of this event to `JobDetailFragment`:

```kotlin
fun browse(jobListingId: String) = launch {
    val url = jobDetailPresenter.getUrl(jobListingId)
    postEvent(BrowseUrlEvent(url))
}
```

The `getUrl` method isn't implemented in `JobDetailPresenter` yet, let's add this implementation next:

```kotlin
suspend fun getUrl(jobListingId: String): String = withContext(Contexts.IO) {
    jobsInteractor.getJobUrlById(jobListingId)
}
```

`JobInteractor` already has this functionality implemented, and we don't need to do any mapping in this case between the domain and presentation models. We'll just return the primitive `String` type as is.

Finally, let's set up everything in `JobDetailFragment`. We'll need a button listener that triggers the entire flow, we can set this up in `onViewCreated`:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initArguments()

    applyButton.setOnClickListener {
        viewModel.browse(jobListingId)
    }
}
```

Then, when the event comes back as a result, we need to handle it in `onEvent`:

```kotlin
override fun onEvent(event: OneShotEvent) {
    when (event) {
        is BrowseUrlEvent -> {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(event.url)
            startActivity(intent)
        }
    }
}
```

## Continue...

[Next, we'll look at even more of Jetpack by exploring Room.](./room.md)
