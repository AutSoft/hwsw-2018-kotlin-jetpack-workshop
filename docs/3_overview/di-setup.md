# ViewModel Dependency injection setup

## Initial approach

We'll now take a look at how the correct `ViewModel` ends up being injected into any given `Fragment`. Which `ViewModel` type we need is always specified in the type parameter of the `BaseFragment` subclass, as we've seen before, so we could in each subclass fetch a `ViewModel` instance with something like this:

```kotlin
override fun provideViewModel(): ProfileViewModel {
    return ViewModelProviders.of(this).get(ProfileViewModel::class)
}
```

(The `provideViewModel` method here is the one that `BaseFragment` calls to set the value of `viewModel` before starting the `LiveData` observations.)

What's wrong with this?
- It only works for `ViewModel` implementations that take no parameters, which is a no-go for us, they'll have to get a Presenter from somewhere.
- We _could_ implement a `ViewModelProvider.Factory` for every ViewModel separately, but that's an extra class to write for each screen. Also, we'd have trouble instantiating a `ViewModel` even there, since we'd have to create its dependencies ourselves. Maybe we could pass in a Context, and go from there... Or we could create separate `inject` methods for each `ViewModel` subclass in our Dagger component, and use field injection...

Really, we'd like to hook our entire `ViewModel` creation process into Dagger, so we could just slap `@Inject` on its constructor and ask for our Presenter as a parameter, which could then be created by Dagger, injected with all of its dependencies, etc.

```kotlin
class ProfileViewModel @Inject constructor(
        private val profilePresenter: ProfilePresenter
) : BaseViewModel<ProfileViewState>(Loading) {

    // ...

}
```

## One Factory to rule them all

Let's recall how the `ViewModelProvider.Factory` class worked. It always receives the `ViewModel` type to instantiate as a parameter in its `create` method:  

```kotlin
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ...
    }

} 
```

Inside this method, we have complete freedom to manufacture our `ViewModel` instance, so we'll make Dagger do it, gaining the ability to inject the `ViewModel`s the factory creates via their constructors so that they can just "ask for" dependencies there.

Implementing such a Factory is - well, _possible using Dagger_. But involves a bit of magic. I'll spare you the details now as it uses some rather advanced mechanisms that would take a presentation on their own to explain in detail. 

![The Dagger powered ViewModel factory](../images/dagger-factory.png)

What you need to know about it is that the Factory is powered by Dagger, and that concrete `ViewModel`s have to be registered in a Dagger module if we want the Factory to be able to create them.

In the example app, this is done in the `ViewModelModule` class, where you can find the "registration" (binding) of `JobListViewModel` as an example.

If you want to find out more about how this Factory works, you can read a bit more about it and find additional references [here](../5_extra-touches/dagger-factory-details.md).

## Getting the Factory in place

We still need to get an instance of this Factory into our Fragments so that we can fetch `ViewModel`s with its help. 

We could add each of our `Fragment`s to our Dagger component as we're creating them, and inject them manually one by one, but this is tedious. It's a good idea to instead put the property that stores the Factory in `BaseFragment`, but due to its type parameters, it practically can't be injected by Dagger. The workaround is to introduce another base class above `BaseFragment` which is non-generic, and injects itself with the Factory:

```kotlin
abstract class InjectedFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

}
``` 

This is no longer painful to add to our Dagger component, as it only has to be done once, and it injects itself neatly via the relatively simple field injection mechanism.

We can now fetch our `ViewModel` in our concrete Fragments like this, and if it needs to be created, it will be created by Dagger somewhere in the Factory:

```kotlin
override fun provideViewModel(): ProfileViewModel {
    return ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class)
}
```

## Continue...

[Next, a brief look at coroutine integration.](./coroutine-support.md)
