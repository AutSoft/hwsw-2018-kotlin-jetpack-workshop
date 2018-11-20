# View state and Presenter

Let's start creating the classes we'll need for our new screen. We'll create a `jobdetail` package under the existing the `ui` package. Similarly to the list screen, this is where we'll place our Fragment, ViewModel, State and Presenter.

## State

In the `jobetail` package, we'll create a new Kotlin file called `JobDetailViewState`. This will, of course, be a sealed class:

```kotlin
sealed class JobDetailViewState
```

We need two different implementations of this class. Our screen will initially be in a `Loading` state, and when we've fetched the necessary details it'll enter a `JobDetailLoaded` state, which will contain the data to display.

```kotlin
data class JobDetailLoaded(val detailedJob: DetailedJob) : JobDetailViewState()

object Loading : JobDetailViewState()
```

Note that `Loading` is declared as a singleton `object`. Since it contains no state, there's no need to always create instances of it.

Our presentation model, `DetailedJob` doesn't exist yet - it will be nested in the Presenter just like the presentation model of the List screen is. Let's continue with this!

## Presenter

In the same pacakge, we'll create the `JobDetailPresenter` class. It will receive a single dependency via constructor injection, a `JobsInteractor`. For an app this small we don't need separate Interactors for our two screens yet.

```kotlin
class JobDetailPresenter @Inject constructor(
        private val jobsInteractor: JobsInteractor
)
```

_Inside_ our Presenter, we'll create the presentation model that was missing earlier:

```kotlin
class DetailedJob(
        val id: String,
        val positionInfo: String,
        val location: String,
        val description: String,
        val companyLogo: String?
)
```

Remember to go back to the file containing our state and importing this model. If you invoke intention actions on `DetailedJob`, you can import it directly, so that it doesn't have to always be prefixed with the `JobDetailPresenter` that contains it. 

And now we can add a method to fetch the details of a job by its ID. This will be a great example of the tasks of a Presenter, which are to... 

- place the call on a background thread using `withContext`, 
- use the Interactor(s) it depends on to fetch data, 
- when the data is acquired, map it to the presentation model.

```kotlin
suspend fun getDetailedJob(jobListingId: String): DetailedJob = withContext(Contexts.IO) {
    jobsInteractor.getJobDetailsById(jobListingId).let {
        DetailedJob(
                id = it.id,
                positionInfo = "${it.title} @ ${it.company}",
                location = it.location,
                description = it.description,
                companyLogo = it.companyLogo
        )
    }
}
```

As an example of how models can contain the same data in different forms, we map the domain objects received in a way that combines the job's title and company into a single `positionInfo` field that we'll display on the UI in a single `TextView`.

## Continue...

[Up next: ViewModel.](./viewmodel.md)
