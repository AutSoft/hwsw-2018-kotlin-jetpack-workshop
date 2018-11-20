# Coroutine support

`ViewModel`s have to launch coroutines when they want to interact with lower layers based on input events. Based on what we've learned about coroutines from earlier, this is going to be pretty straightforward.

## Launch _all_ the things

We've seen that a `ViewModel` can implement a `CoroutineScope` very neatly, tying any coroutines it creates to the lifecycle of a screen. There's nothing preventing us from moving this implementation into `BaseViewModel`, so that any `ViewModel` can at any time easily (and safely) `launch` a coroutine. The implementation is the same as we've seen before:

```kotlin
abstract class BaseViewModel<VS : Any>(initialState: VS) : ViewModel(), CoroutineScope {

    private val rootJob = Job()

    final override val coroutineContext: CoroutineContext = Contexts.UI + rootJob

    override fun onCleared() {
        super.onCleared()
        rootJob.cancel()
    }

}
```

Then, in every `ViewModel` method that's called from a `Fragment`, we `launch` a coroutine to interact with lower layers through our Presenter:

```kotlin
class JobListViewModel @Inject constructor(
        private val jobListPresenter: JobListPresenter
) : BaseViewModel<JobListViewState>(Loading) {

    fun load() = launch {
        val jobPostings = jobListPresenter.getData()
        viewState = JobListReady(jobPostings)
    }

}
```

## A note on LiveData

Many architectures will have `LiveData` flow through all their layers, right from the database all the way to the UI. This is what Google recommends in their official guide as well. We decided against this for now, and are betting on coroutines for the lower layers, only using `LiveData` for the final hop that the data makes from `ViewModel` to `Fragment`.

## Continue...

[Onward to concrete classes, we can now take a look at how our List screen is implemented.](./list-screen.md)
