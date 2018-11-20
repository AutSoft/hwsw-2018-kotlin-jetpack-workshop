# Simpler View visibilities with KTX

Let's revisit the implementation of the `showJobDetails` method inside `JobDetailFragment`. This is what it currently looks like:

```kotlin
private fun showJobDetails(job: JobDetailPresenter.DetailedJob) {
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

We'll focus on the last statement here, the `if` that checks whether `job.companyLogo` contains a `null` value. We use these sorts of checks often for setting `View` visibilities, and we can do better here.

Specifically, we can use the `isVisible` (and `isInvisible`, `isGone`) method from the Android KTX library, another Jetpack solution. These let us to set `View` visibilities with `Boolean` values instead of having to use the `View.VISIBLE` and other similar constants.

In our case, we can replace the `if` block with this code, using `isVisible`:

```kotlin
companyLogoImage.isVisible = job.companyLogo != null
Glide.with(companyLogoImage).load(job.companyLogo).into(companyLogoImage)
``` 

This will set the visibility of `companyLogoImage` to `View.VISIBLE` if the expression is `true`, and to `View.GONE` if it's `false`. Simple!

As for the Glide call, which now happens whether the value of `companyLogo` is `null` or not... Glide actually doesn't mind it too much if we pass a `null` value to it. It will simply fail to load it silently in the background. 

## Continue...

[There's still more to be done here.](./glide-extension.md)
