# ViewModel

Next up, our ViewModel! We need it to hold an instance of the state we've just defined, and let us fetch data for the screen in a coroutine.

## ViewModel implementation

Let's create the `JobDetailViewModel` class, and inject a `JobDetailPresenter` into it. Remember to inherit from `BaseViewModel` so that we can integrate with our architecture. We're forced to pass in an initial state for the screen - since we don't have our data ready yet, this will be the `Loading` state.

```kotlin
class JobDetailViewModel @Inject constructor(
        private val jobDetailPresenter: JobDetailPresenter
) : BaseViewModel<JobDetailViewState>(Loading)
```

Next, we need a method to fetch data, we'll simply call it `load`:

```kotlin
fun load(jobListingId: String) = launch {
    if (viewState is JobDetailLoaded) {
        return@launch
    }
    
    viewState = Loading
    
    val detailedJob = jobDetailPresenter.getDetailedJob(jobListingId)
    viewState = JobDetailLoaded(detailedJob)
}
```

We're immediately `launch`ing a coroutine when the method is called, in the UI context thanks to our `BaseViewModel` superclass providing us a `CoroutineScope`.

We guarantee that data is only loaded once even if it's called multiple times by checking if we already have a job loaded. Otherwise, we explicitly set the current state to reflect that we are in fact loading, and we make a suspending call into the Presenter to fetch data. This frees the UI thread to handle whatever it needs to in the meantime.

When the Presenter call completes, our coroutine continues back on the UI thread again by setting a new view state, a `JobDetailLoaded` containing the data we got.

## DI integration

We're almost done here, but if we want to use this ViewModel, we'll need to be able to create it in our `DaggerViewModelFactory`, which can only manufacture `ViewModel`s that we told Dagger about.

Let's add our new `ViewModel` to `ViewModelModule`, next to the one that backs our List screen, with this code:

```kotlin
@Binds
@IntoMap
@ViewModelKey(JobDetailViewModel::class)
abstract fun bindJobDetailViewModel(jobDetailViewModel: JobDetailViewModel): ViewModel
```

## Continue...

[The star of the show, coming up: let's create a Fragment.](./fragment-args.md)
