# State handling in base classes

Let's take a look at how exactly the state is stored and observed.

## BaseViewModel

Here's a stripped down view of `BaseViewModel` which only shows the code related to state handling:

```kotlin
abstract class BaseViewModel<VS : Any>(initialState: VS) : ViewModel() {

    private val _state = MutableLiveData<VS>()

    init {
        _state.value = initialState
    }

    val state: LiveData<VS> = _state

    protected var viewState: VS
        get() = _state.value!!
        set(value) {
            _state.value = value
        }
}
```

The class is generic on the type of the state it can store. It contains a `private` `MutableLiveData` instance, which actually stores the state. 

This is immediately initialized at construction time via an `init` initializer block and a constructor parameter. This is essential, because `LiveData` may technically store a `null` value, which we want to avoid. The way we implement state here ensures that we'll always be in a valid, concrete, non-`null` state.

Moving on, the `state` property is how the private backing property is exposed to observers, i.e. `Fragments` and `Activities`. Note that this has the read-only `LiveData` type, so that they can't modify the stored value.

Finally, the concrete implementations of `ViewModel` need to be able to modify the state. We _could_ expose the `_state` property to them by making it `protected`, but there would be a couple drawbacks:

- It has an ugly name, since it's a backing property. This _could_ be solved by just renaming it, of course.
- They would have to be aware that they're using `LiveData`, and always set state like such:
    
    ```kotlin
    _state.value = SomeViewState(...)
    ```

- Reading the state would be done very similarly via `_state.value`.
- When writing the state, `null` could be set by a `ViewModel` implementation, which makes no sense as a state, and which we want to avoid. Similarly, when reading it, `null` may be returned, which we'd always be forced to handle by the compiler before we use it.

How does the `viewState` property we've introduced solve these issues? Its type isn't a `LiveData<VS>`, but simply `VS`. 

You can get its value by just writing down `viewState`, and you can set it like so:

```kotlin
viewState = SomeViewState(...)
viewState = viewState.copy(...)
```

It's non-nullable, so it doesn't let anyone pass a `null` value into it. Note that the `!!` operator used here is safe because we control all accesses to this `LiveData`, and we initialize it at construction time with a value like described above.

## BaseFragment

We have our ViewModel implementation already, which stores a non-null, very safe and valid state at all times. How do we observe this state from our Fragments?

Again, we'll look at a filtered view of `BaseFragment`:

```kotlin
abstract class BaseFragment<VS : Any, VM : BaseViewModel<VS>> : Fragment() {

    protected lateinit var viewModel: VM

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = provideViewModel()

        viewModel.state.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { render(it) }
        })
    }

    abstract fun provideViewModel(): VM

    abstract fun render(viewState: VS)

}
```

This base class has two type parameters, the view state it will work with, as well as the `ViewModel`, which is required to store that same type of view state.
 
`BaseFragment` will store an instance of the `ViewModel` which belongs to it in a `protected` property, since subclasses will need to call methods on the `ViewModel` in response to input events. 

The `provideViewModel` method called in `onViewCreated` fetches the correct `ViewModel` instance, which has to be done in the concrete subclass for reasons discussed later, therefore, it's `abstract`.

Then, the public `state` exposed by `BaseViewModel` is observed, and any non-null values it emits (this is essentially an extra safety measure here, as this should never happen anyway) will be passed on to a `render` method, which is expected to populate the UI using the values in the current view state. Notice that this setup happens in the `onViewCreated` method, and accordingly, the observations are tied to the `viewLifecycleOwner` as well, making the "subscriptions" to `LiveData` clean up when the `Fragment`'s view is destroyed, and recreated when it's created again. 

What does a subclass of `BaseFragment` need to do to properly handle state then? Just implement the `render` method properly, and that's it. This method's responsibility is to update the state of the UI to reflect the current view state. It must be implemented in a way so that previous view states do not affect the current state of the UI. In other words, the same view state being set must always result in the same state for the `Fragment`'s UI.

Here's a basic implementation of a `Fragment` in this architecture, with a focus on the `render` mechanism, some other parts simplified or omitted.

```kotlin
class ProfileFragment : BaseFragment<ProfileViewState, ProfileViewModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.load()
    }

    override fun render(viewState: ProfileViewState) {
        when (viewState) {
            is Loading -> showLoadingView()
            is Profile -> showProfileView(viewState) 
        }
    }

    private fun showLoadingView() {
        nameText.isVisible = false
        progressBar.isVisible = true
    }
    
    private fun showProfileView(profile: Profile) {
        progressBar.isVisible = false
        nameText.isVisible = true
        nameText.text = profile.name
    }

}
```

The view state used here may be something like this:

```kotlin
sealed class ProfileViewState

object Loading : ProfileViewState()

data class Profile(val name: String) : ProfileViewState()
```

## Continue...

[Next, we'll take a look at the dependency injection setup for ViewModels.](./di-setup.md)
