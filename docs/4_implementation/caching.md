# Caching implementation

We have our database all set up, let's make use of it now. Coordination between data sources happens in Interactors, so we'll have to make our changes in `JobInteractor`.

## Interactions

Our current implementation for fetching a job's URL based on its ID looks like this:

```kotlin
suspend fun getJobUrlById(jobId: String): String {
    return networkDataSource.getJobDetailsById(jobId).url
}
```

We're fetching the entire detailed view of the job from the network, and then returning only its URL.

We'll want to involve a `DiskDataSource` instance here. Let's inject one into the Interactor by simply updating its constructor:

```kotlin
class JobsInteractor @Inject constructor(
        private val networkDataSource: NetworkDataSource,
        private val diskDataSource: DiskDataSource
) {
    ...
}
```

We'll implement a very simple cache here. Whenever a URL is requested, we'll first attempt to grab it from disk. If it's not there, we'll fetch it from the network, save it to the disk, and only then return it. Any subsequent requests will now find this URL on disk.

Let's update the `getJobUrlById` method accordingly:

```kotlin
suspend fun getJobUrlById(jobId: String): String {
    val jobUrl = diskDataSource.getJobUrl(jobId)

    if (jobUrl != null) {
        Timber.d("Found job URL on disk")

        return jobUrl
    }

    Timber.d("Didn't find job URL on disk")

    val jobDetails = networkDataSource.getJobDetailsById(jobId)

    Timber.d("Got job URL from API")

    diskDataSource.saveJobUrl(jobDetails)

    Timber.d("Saved job URL to disk")

    return jobDetails.url
}
```

And that's it, we're done! Notice how easy the business logic was to implement thanks to both data sources accepting and returning domain models in their interface. 

Build and run the app again, and pay attention to the Logcat output when hitting the _Apply_ button on a detail screen.

- For each job, the first time you hit _Apply_, you should see the messages about the job URL not being on disk and being fetched from the API instead.
- If you navigate back, and start applying for the same job a second time however, you should see that the job URL was now found in the disk cache. 

## Functional vibes

Some might point out that there would be more concise ways to write the `getJobUrlById` method using functional constructs. This is true, and this is the best I could come up with for a "funtional one-liner" off the top of my head:

```kotlin
suspend fun getJobUrlById(jobId: String): String {
    return diskDataSource.getJobUrl(jobId)
            ?: networkDataSource.getJobDetailsById(jobId)
                    .also { diskDataSource.saveJobUrl(it) }
                    .url
}
```

This is neat in a way, and there are many Kotlin folks who will understand it. However, I'd usually advocate for the previous solution instead, because it's much, much friendlier for beginners.

## Continue...

[If you've enjoyed the main parts, check out the deleted scenes.](../../README.md#extra-touches)
