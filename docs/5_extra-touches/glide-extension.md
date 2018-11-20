# Glide extension

Glide calls like this are rather verbose by Kotlin standards, even though the library actually has a very neat API:

```kotlin
Glide.with(companyLogoImage).load(job.companyLogo).into(companyLogoImage)
```

It's not hard to imagine an extension function that would allow you to quickly and easily put an image by its URL into an `ImageView`, using Glide internally:

```kotlin
companyLogoImage.load(job.companyLogo)
```

Let's create a new top level `util` package within our project, and within that, a file named `Glide.kt`, since we'll place Glide related extensions here. Instead of going for a trivial implementation, we'll do something fancier to show off some Kotlin features:

```kotlin
inline fun ImageView.load(path: String?, withCache: Boolean = true) {
    if (path == null) {
        setImageDrawable(null)
        return
    }

    val requestManager = Glide.with(this)

    if (!withCache) {
        requestManager.applyDefaultRequestOptions(
                RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))

    }

    requestManager.load(path).into(this)
}
```

Now, what's all this code if we're only trying to replace the one-liner of a Glide load we've seen earlier?

We've added some extra features here. In the case of receiving a `null` path in the parameter, we're setting a `null` value as an `ImageDrawable` into the `ImageView` directly, instead of having Glide fail to load it when we invoke it.

The function also has an extra `withCache` parameter which has a default value, making it essentially optional at the call site. By default, this parameter is `true`, and does essentially nothing. However, if set to `false`, it will circumvent Glide's memory and disk cache, and always fully reload the image from its source. We don't need this functionality in this project, but it's useful if, for example, you're modifying an image file on disk in place, and want those changes reflected immediately when loading it into an `ImageView`.

## Optimization fun

What's even more interesting here is that if we try calling this `inline` function with a `true` and a `false` value for this parameter, we'll see that the `if` check for this parameter is never actually generated in the inlined code. If the value is known at compile time, the compiler will simply inline the appropriate branches as an optimization.

For example, this call:

```kotlin
companyLogoImage.load(job.companyLogo)
```

... generates the following in the bytecode (this is Kotlin code, based on the decompiled Java from bytecode):

```kotlin
val path = job.companyLogo
if (path == null) {
    companyLogoImage.setImageDrawable(null)
} else {
    val requestManager: RequestManager = Glide.with(companyLogoImage)
    requestManager.load(path).into(companyLogoImage)
}
```

And this one:

```kotlin
companyLogoImage.load(job.companyLogo, withCache = false)
```

... generates this:

```kotlin
val path = job.companyLogo
if (path == null) {
    companyLogoImage.setImageDrawable(null)
} else {
    val requestManager: RequestManager = Glide.with(companyLogoImage)
    requestManager.applyDefaultRequestOptions(RequestOptions().skipMemoryCache(true)
                                              .diskCacheStrategy(DiskCacheStrategy.NONE))
    requestManager.load(path).into(companyLogoImage)
}
```

No `if` check for `withCache` anywhere!

## Usage

Let's actually use this new extension in our project now. There are two Glide calls we can update.

First, in `JobDetailFragment`'s `showJobDetails` method:

```kotlin
companyLogoImage.load(job.companyLogo)
```

And then in `onBindViewHolder` inside `JobListingAdapter`, where we load the images for the list items:

```kotlin
holder.image.load(jobListing.imageUrl)
```

## Continue...

[Next, some Android Studio tooling tips.](./layout-preview-improvements.md)
