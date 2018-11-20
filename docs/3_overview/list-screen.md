# List screen implementation

We've looked at the theory, we've looked at the base classes, let's see how they're actually used in our example app now! We'll look at our List screen which is already implemented.

## Jumping in

The say that a Manifest is like a table of contents for an Android application. If we look at ours, we'll see we have a single `Activity`, so whatever's shown on the UI must be set up there. If we look at `MainActivity`, we'll find this code in it:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContent, JobListFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

}
```

This is simple enough, and we see that the list is implemented in `JobListFragment`.

## The Fragment 

Let's look at this `Fragment` then, step by step.

In its header, we see that it, of course, extends `BaseFragment`, and will use a `JobListViewState` and a `JobListViewModel` to do its work. It also implements `provideViewModel` by fetching a `JobListViewModel`.

```kotlin
class JobListFragment : BaseFragment<JobListViewState, JobListViewModel>(), JobListingAdapter.Listener {

    override fun provideViewModel(): JobListViewModel {
        return ViewModelProviders.of(this, viewModelFactory).get(JobListViewModel::class.java)
    }

    // ...

}
```

Moving on, we see boring `Fragment` code. Its view is inflated, and the `RecyclerView` its layout contains is set up with an adapter. The `Fragment` itself is set as the adapter's listener.

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_job_list, container, false)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupRecyclerView()
}

private lateinit var adapter: JobListingAdapter

private fun setupRecyclerView() {
    adapter = JobListingAdapter()
    adapter.listener = this
    jobListings.adapter = adapter
}
```

You may look at the implementation of `JobListingAdapter`, it's pretty straightforward as far as a `RecyclerView.Adapter` goes, except for one thing: it uses the [`ListAdapter`](https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter) base class introduced in Support Library 27.1.0. This gets rid of having to make `notify...` calls and lets us just throw new lists at the adapter when our data changes. `ListAdapter` will then calculate a diff on a background thread, and animate any changes we made to display the new data. For more info on how to use it, take a look at [this tutorial](https://medium.com/@trionkidnapper/recyclerview-more-animations-with-less-code-using-support-library-listadapter-62e65126acdb). 

The `render` method is implemented similarly as the example before:

```kotlin
override fun render(viewState: JobListViewState) {
    when (viewState) {
        is JobListReady -> {
            progressBar.isVisible = false
            jobListings.isVisible = true

            adapter.submitList(viewState.jobPostings)
        }
        is Loading -> {
            progressBar.isVisible = true
            jobListings.isVisible = false
        }
    }
}
```

## View state

At this point, it's worth jumping into the `JobListViewState` class to see what our available states are - again, nothing unexpected based on what we've already seen. A handy sealed class that clearly separates the two distinct states of the screen.

```kotlin
sealed class JobListViewState

data class JobListReady(val jobPostings: List<JobListing>) : JobListViewState()

object Loading : JobListViewState()
```

## ViewModel

I've actually shown you the real code of `JobListViewModel` before. It takes a `JobListPresenter` as its dependency via constructor injection, and when its `load` method is called, it fetches the list of `JobListing` instances from there. Then, it updates the view state:

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

The `launch` here will create a coroutine in the UI context because of the way the `CoroutineScope` is implemented in `BaseViewModel`. This makes setting `viewState` in it safe. The `getDate` call in it, however, is suspending (as you can see next to the line numbers if you're looking at the code in Android Studio), so that it doesn't block the UI thread. 

(You can find a call to this `load` method in `JobListFragment`'s `onStart` lifecycle method.)

## Presenter

Finally, the last screen-specific class needed is a Presenter, in this case, `JobListPresenter`.

We see constructor injection again, this class will depend on an Interactor.

```kotlin
class JobListPresenter @Inject constructor(
        private val jobsInteractor: JobsInteractor
)
```

The `getData` function is called from the UI thread inside a coroutine, and we want to move all work below the ViewModel layer to background threads. We'll do this as the first thing in every Presenter method we implement, using `withContext` to jump to the `IO` threadpool, and perform anything we need in this new context.

```kotlin
suspend fun getData(): List<JobListing> = withContext(Contexts.IO) {
    jobsInteractor.getAllJobListings().map {
        JobListing(
                id = it.id,
                title = it.title,
                company = it.company,
                location = it.location,
                imageUrl = it.companyLogo
        )
    }
}
```

We'll get data from `JobsInteractor`, and map the list of domain models we receive from it to the screen specific presentation models, which are `data class`es nested inside the Presenter.

```kotlin
data class JobListing(
        val id: String,
        val title: String,
        val company: String,
        val location: String,
        val imageUrl: String?
)
```

## Continue...

[Next, we'll look at  models and data fetching in our entire application in detail.](./data-and-models.md)
