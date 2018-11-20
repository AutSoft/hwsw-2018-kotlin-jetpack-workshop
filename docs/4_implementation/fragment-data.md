# Fragment, pt II: Data and rendering

## Data fetching

We'll need to call our `JobDetailViewModel`'s `load` method at some point to fetch data and have our state updated. `onStart` is usually a good place to do this:

- by the time it's called, our argument properties have already been initialized, 
- it's safe to make the call in every `onStart` since we're already checking in the `ViewModel` if we've already loaded data or not, and disregarding any unnecessary `load` requests.

```kotlin
override fun onStart() {
    super.onStart()
    
    viewModel.load(jobListingId)
}
```

## Rendering

Let's take a look at our layout in `fragment_job_detail.xml`. It contains a `ViewFlipper` with two children: one is a simple `ProgressBar`, while the other one is a `ScrollView` with many descendants which make up the UI that displays the details of a job posting.

```xml
<ViewFlipper xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/viewFlipper"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- 0 -->
    <ProgressBar
        android:layout_width="64dp"
        android:layout_height="64dp"
        android:layout_gravity="center" />

    <!-- 1 -->
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        ...

    </ScrollView>

</ViewFlipper>
``` 

`ViewFlipper` is essentially a fancy `FrameLayout` which allows you to easily display only a single child inside it at a time. To make these calls in a legible way instead of just flipping to child `0` and `1`, we'll introduce constants for its different children in our `JobDetailFragment`.

We could put these in our companion object, but there's a bunch of code there already for handling arguments. We can't have multiple companion objects in a single class, but we can have other, named objects nested into it. Let's create one called `Flipper` (again, inside `JobDetailFragment`):

```kotlin
private object Flipper {
    const val LOADING = 0
    const val READY = 1
}
```

With this in place, we can now implement our `render` method. We only need to flip between our two pages depending on which state we receive as a parameter to update `View` visibilities.

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

If we've entered the loaded state, we'll also need to populate the UI with the data received. Let's do this next by implementing the `showJobDetails` method we've called above:

```kotlin
private fun showJobDetails(job: DetailedJob) {
    positionInfoText.text = job.positionInfo
    locationText.text = job.location

    descriptionText.text = Html.fromHtml(job.description)

    if (job.companyLogo != null) {
        companyLogoImage.visibility = View.VISIBLE
        Glide.with(companyLogoImage).load(job.companyLogo).into(companyLogoImage)
    } else {
        companyLogoImage.visibility = View.GONE
    }
}
```

The job description is received from the server as HTML, so we use `Html.fromHtml` to create a spanned `String` for the `TextView` that displays it. For image loading, we'll use [Glide](https://github.com/bumptech/glide), and if the job doesn't have an image, we'll simply hide the `ImageView`.

## Try it!

At this point, we can finally run our app! When we click on a job in the list, it should open the detailed view of it, and display the data.

## Continue...

[Still not done here, button handling awaits (no pun intended).](./fragment-button.md)
