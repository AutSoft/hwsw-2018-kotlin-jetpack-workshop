# Room integration

Let's add a database to our app and cache something. This something, for simplicity's sake, will be just the URLs that belong to each job that we've looked at. We'll use yet another Jetpack component, the _Room_ library, which wraps the platform's built-in SQLite database into a lightweight ORM of sorts, while still allowing us to write SQL for our queries easily and safely.

(It would make more sense to cache the list of jobs, and/or the job details for the jobs that we've already opened the details screen and looked it up for. You can do these on your own later!)

## Entity

First step, we need to define the database model which we'll be storing in Room. Let's create a `disk` package within the `data` package we already have, and within this new package, a `model` package.

In here, we'll create a `JobUrl` class:

```kotlin
@Entity
class JobUrl(
        @PrimaryKey
        val id: String,
        val url: String
)
```

The `@Entity` annotation marks that it will be stored in Room, and `@PrimaryKey` tells the library that the `id` property will act as the unique key for each instance.

## Dao

Next, we need a way to store and then access our `JobUrl`s in the database. Room's uses DAOs (Data Access Objects) for this purpose. We'll create ours in the `data/disk` package, and it will look like this:

```kotlin
@Dao
interface JobUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(jobUrl: JobUrl)

    @Query("SELECT * FROM joburl WHERE id = :id")
    fun getJobUrlById(id: String): JobUrl?

}
```

The methods here are fairly simple. 

- We have an `@Insert` operation which replaces existing records if there's a conflict (the same ID is already in the database). We don't need to provide the implementation here, Room can figure out how to do this based on the method signature. (The same works for `@Delete` and `@Update` operations most of the time, and we could even use different parameters, for example, we could also insert a `List` or `Array` of model objects at a time.)

- The second method is a `@Query`, where we need to provide the SQL query we want to execute. The implementation here will still be generated for us, but we're interacting with the database a bit more manually. Notice that we get syntax highlighting and code completion when writing these queries, and we can use our method's parameters in them as well.

_If you squint a little, this might look pretty similar to a Retrofit interface, which isn't by accident..._

## Database

Finally, we need to create a Database to hold our entities and DAOs together:

```kotlin
@Database(
        version = 1,
        entities = [
            JobUrl::class
        ]
)
abstract class JobDatabase : RoomDatabase() {

    abstract fun jobUrlDao(): JobUrlDao

}
```

Databases are abstract classes that have to inherit from `RoomDatabase`, and be annotated with `@Database`. In this annotation we can set the version, as well as the types of entities that this database will store (i.e. its tables). Note that we could have multiple databases in a single application.

DAOs will be created by the concrete implementations of this Database class, and we can add abstract getter methods for the DAOs we'll want to use to access the given database's contents.

## Data source

As per the architecture's guidance, we'll need to wrap these Room specific classes in a data source which only has domain objects passing through its interface, in both directions. Internally, this data source will make use of our concrete database implementation.

```kotlin
class DiskDataSource @Inject constructor(
        private val jobUrlDao: JobUrlDao
) {

    fun saveJobUrl(jobDetails: JobDetails) {
        val jobUrl = jobDetails.let {
            JobUrl(
                    id = it.id,
                    url = it.url
            )
        }
        jobUrlDao.upsert(jobUrl)
    }

    fun getJobUrl(jobId: String): String? {
        return jobUrlDao.getJobUrlById(jobId)?.url
    }

}
```

We'll inject a `JobUrlDao` directly into our `DiskDataSource`, because it's the most convenient solution here - we'd be no better off injecting the entire `JobDatabase`. The next section will cover the DI setup for this.

We do a small amount of mapping here between domain objects and Room specific objects, in the same style as in `NetworkDataSource`. We even use `let` on a single object so that the mapping code matches the style we'd use if we were mapping an entire list of these objects.

Unlike in `NetworkDataSource`, these methods aren't suspending, as Room doesn't have support for coroutines in the way that Retrofit did. We're simply blocking the thread in the IO pool here that our coroutine happens to be running on when it calls the `DiskDataSource` methods. 

## Dependency injection

We'll need a new Dagger module that can provide these classes, most importantly, the `JobUrlDao` we want to use in `DiskDataSource`.

Let's create a `DiskModule` class still in the `disk` package:

```kotlin
@Module
class DiskModule {

    @Provides
    @Singleton
    fun provideJobDatabase(context: Context): JobDatabase {
        return Room.databaseBuilder(context, JobDatabase::class.java, "jobdb").build()
    }

    @Provides
    @Singleton
    fun provideJobUrlDao(jobDatabase: JobDatabase): JobUrlDao {
        return jobDatabase.jobUrlDao()
    }

}
```

The `JobDatabase` can be provided by calling `Room.databaseBuilder`. We need to provide the type of database we want to instantiate, and the name it should have on disk. That's it! (We did need a `Context` here, but we already have a Dagger module set up in our application that's providing that.)

The DAO provider method is trivial - remember, we gave `JobDatabase` a method that returns a `JobUrlDao`! This gets us the generated implementation for the queries we wrote. 

We need to add this module to our Dagger component as well. This is called `AppComponent`, and its `@Component` annotation should look like this after you add the newly created `DiskModule` to the ones that are already there:

```kotlin
@Component(modules = [
    ApplicationModule::class,
    ViewModelModule::class,
    NetworkModule::class,
    DiskModule::class
])
```

## Continue...

[Next, we'll actually make use of our database.](./caching.md)
