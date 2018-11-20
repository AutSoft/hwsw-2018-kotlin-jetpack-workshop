# Do It Yourself

Finally, if you're still looking for something to code, I invite you to solve the following tasks on your own:

## Empty view

Add an Empty state to the List screen, which is shown when the list is loaded, but there are no items in it. This may be plain text describing that there are no jobs available, or perhaps some sort of illustration.

## Error handling
 
Add Errored states to both the List and Detail screens, which are shown if a network error has occurred. 

It would be nice to implement a button, a Snackbar, or a swipe-down-to-refresh mechanism that allows the user to try loading the again in these situations.

## Proper, meaningful caching

Implement caching for the list of jobs. Save downloaded jobs to disk, and if they've been saved there relatively recently (say, no more than an hour ago), return those jobs instead of fetching from the network. The last time you've downloaded articles may be stored as a timestamp in `SharedPreferences` - this would require the creation of a third data source in the app. May I suggest the [Krate](https://github.com/AutSoft/Krate) library for this?

Or...

Implement caching for article details. If a user opens the detailed screen of a job, fetch it from the network, but save all of its content to disk so that the next time the user opens the same job its details don't have to be downloaded again. It's probably safe to assume that a job with a given ID will never have its content updated, so you can use a cached version indefinitely here.

## Continue...

Umm... Actually, I've got nothing else. That's it! You're done, congratulations! Go out there and use all that you've learned wisely.
