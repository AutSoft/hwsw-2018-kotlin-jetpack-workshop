# Android app architecture powered by Jetpack & Kotlin (HWSW mobile! 2018)

## Contents

- Written instructions (this document, and [this folder](./docs)).
- [Slides](./slides/slides.pdf)
- [Starter project](./projects/starter) 
- [Completed project](./projects/complete)

## Introduction

If you're like anything like me and you like to keep up-to-date with the trends in Android, you watch conference talks. Conference talks where Jake Wharton praises Kotlin and Dagger, Christina Lee or Dan Lew teaches you about Rx, and perhaps Lisa Wray shows off the advantages of data binding. From others again, you might see mentions of React, Flutter, MVI, Flux/Redux, MvRx, and many other technologies you _could be_ building your apps on.

But what you actually use in practice is an entirely different question. The answer to this question, at least for us, is Kotlin - combined with the architecture components provided by Jetpack.

In this workshop, I'd like to give you some insight into the sorts of decisions we made while defining our architecture, and show you the way we're harnessing the power of the aforementioned technologies. We'll do this by first learning a bit about them, and then looking at a simplified version of our architecture through an example application.

#### Disclaimer

Of course, no architecture will fit _every_ application you'll ever need to develop. However, we found that the types of applications we frequently build - business applications, mostly performing CRUD operations against a server - tend to fit the same architecture. 

Unifying the architecture of our apps has obvious benefits, as it... 
- Lets people get a grip on a new projects quicker when they get reassigned
- Provides solutions to common problems so we don't have to solve them again for new projects
- Establishes company wide best practices, a common set of known technologies

I'll also be the first to admit that several aspects of the architecture I'll show you will be overkill for simple applications, or entirely unsuitable for others for certain reasons. I don't want to prescribe this architecture to anyone, don't take what's shown here as gospel. I merely want to show you a way of getting started with the technologies in question, and the solutions we found to some common Android development problems along the way.

## Some theory

This workshop assumes that the participants have a stable knowledge of Android and an at least basic knowledge of the Kotlin language.

With that said, before we jump into the architecture itself, we'll familiarize ourselves with (or brush up on) some of the technologies we'll be using. We'll do this in 4 parts:

- [Advanced Kotlin](./docs/1_theory/advanced-kotlin.md)
- [Coroutines](./docs/1_theory/coroutines.md)
- [Jetpack](./docs/1_theory/jetpack.md)
- [Coroutine scopes](./docs/1_theory/coroutine-scopes.md)

Now that we are all ready to suspend any function we come across and know how to separate our state from our `Activity`, we can take a look at the architecture itself.

## The architecture

It's time to combine these advanced Kotlin features with the architecture components we've looked at to create our architecture.

Here's what the goals of this architecture are going to be:
- Clearly separate concerns between different components
- Always keep views in a safe and consistent state with ViewModels
- In addition to a consistent state, also enable emitting one-time events from these ViewModels (alerts, navigation, error messages) for the views to display
- Handle configuration changes and even process death gracefully
- Make offloading work to background threads easy and avoid callbacks
- Provide Fragment-based navigation with argument passing between screens in a single Activity
 
Before we just into code, I'd like to give you an overview of the architecture, the roles of its different layers, and a couple design choices that have been made when separating these roles.

- [Layers](./docs/2_architecture/layers.md)
- [Threading](./docs/2_architecture/threading.md)
- [Models](./docs/2_architecture/models.md)

## The example application

The example application we'll be working with is a simple client that lets us browse the jobs available via the [GitHub Jobs API](https://jobs.github.com/api). The starter project is a single screen, which lists the available jobs.

## Overview

Let's take a look at the existing code of the application first to get more familiar with the architecture.

The architecture - as usual - provides some base classes for us to inherit from with our concrete implementations. The main ones we'll be focusing on are `BaseViewModel` and `BaseFragment`.

First, `BaseViewModel`...
- Contains a `LiveData` that stores the current view state, and provides convenient access to it for subclasses.
- Implements `CoroutineScope` for easy coroutine creation - remember, this is one of the main tasks of a ViewModel!
- Provides event handling, which we'll look at later on.

And then there's `BaseFragment`, which...
- Helps us fetch the appropriate concrete ViewModel (we'll see how this happens exactly in a bit), and stores it.
- Sets up appropriate `LiveData` observers so that we don't have to do it ourselves, and forwards any events into methods we can override.

Let's look at how these are implemented, and then how they're used in our example app.

- [State handling in base classes](./docs/3_overview/state-handling.md)
- [ViewModel dependency injection setup](./docs/3_overview/di-setup.md)
- [Coroutine support](./docs/3_overview/coroutine-support.md)
- [List screen implementation](./docs/3_overview/list-screen.md)
- [Data and models](./docs/3_overview/data-and-models.md)

## Implementation

Now that we have a basic grasp on the architecture, let's see what it's like to use in practice. Finally, new code to write for the app!

We'll implement a new screen which will display a detailed view of a selected job offer when it's selected from the list.

- [Navigator implementation](./docs/4_implementation/navigator.md)
- [View state and Presenter](./docs/4_implementation/viewstate-and-presenter.md)
- [ViewModel](./docs/4_implementation/viewmodel.md)
- [Fragment, pt I: Creation and arguments](./docs/4_implementation/fragment-args.md)
- [Fragment, pt II: Data and rendering](./docs/4_implementation/fragment-data.md)
- [Fragment, pt III: Buttons and events](./docs/4_implementation/fragment-button.md)
- [Room integration](./docs/4_implementation/room.md)
- [Caching implementation](./docs/4_implementation/caching.md)

## Extra touches

Finally, we'll take a look at some ways to make our existing code nicer by embracing Kotlin features, as well as a bunch of Android tooling tips.

- [Enforcing correct `newInstance` usage](./docs/5_extra-touches/enforcing-newinstance.md)
- [Exhaustive `when`](./docs/5_extra-touches/exhaustive-when.md)
- [Simpler View visibilities with KTX](./docs/5_extra-touches/simpler-view-visibilities.md)
- [Glide extension](./docs/5_extra-touches/glide-extension.md)
- [Layout preview improvements, sample data](./docs/5_extra-touches/layout-preview-improvements.md)
- [Layout refactoring](./docs/5_extra-touches/layout-refactoring.md)
- [Dagger Factory details](./docs/5_extra-touches/dagger-factory-details.md)
- [Improved ViewModel lookups with `reified`](./docs/5_extra-touches/reified-viewmodel.md)
- [Do It Yourself](./docs/5_extra-touches/do-it-yourself.md)
