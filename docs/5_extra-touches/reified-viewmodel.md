# Improved ViewModel lookups with `reified`

Our Fragments all have this type of code "duplicated" amongst them:

```kotlin
override fun provideViewModel(): ProfileViewModel {
    return ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class)
}
```

It would be a nice idea to move this into `BaseFragment` as well, but `BaseFragment` doesn't know about the concrete type parameter it was instantiated with, and this call with a type parameter is - unfortunately - not valid:

```kotlin
viewModel = ViewModelProviders.of(this, viewModelFactory).get(VM::class)
```

It _would be_ valid if we were to make the type parameter reified... Which we can't do for a class' type parameter (the inlining trick wouldn't work here), only a function's.

This still lets us do something nice for out syntax though. Here's a tricky function that can only be called from a subclass of `BaseFragment`. This function essentially captures the `BaseFragment`'s concrete `VM` type via a reified type parameter:

```kotlin
inline fun <F : BaseFragment<VS, VM>, VS, reified VM : BaseViewModel<VS>> F.getViewModelFromFactory(): VM {
    return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
}
```

This function works best as a top level function, in the same file as `BaseFragment` itself - although it may also be nested inside `BaseFragment`, if you don't mind the tighter coupling.

With this created, we can just delegate all of our `provideViewModel` calls to this helper function, with a single, very concise call in each of our concrete `Fragment`s:

```kotlin
class JobDetailFragment : BaseFragment<JobDetailViewState, JobDetailViewModel> {

    override fun provideViewModel() = getViewModelFromFactory()
    
}
```

All types are inferred in this call. The return value of `provideViewModel` from the `VM` type parameter of the current `Fragment`, and everything else from the receiver of the `getViewModelFromFactory` method.

## Continue...

[That's the end of the guided tour. You can still continue this way on your own, however.](./do-it-yourself.md)
