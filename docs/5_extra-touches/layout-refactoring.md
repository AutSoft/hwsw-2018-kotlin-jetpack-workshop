# Layout refactoring

The layout code inside `fragment_job_detail.xml` is a bit inconvenient as is. While the `ViewFlipper` solution works great and offers a simple way to choose which child to display at runtime, it will only ever show the first one in the Android Studio preview.

Additionally, if we had even more states to flip between, this file would get quite long and hard to navigate. A simple refactoring step can go a long way here.

Select the entire `ScrollView` that contains the "loaded" state of the detail screen. Invoke the _Refactor this_ action (Ctrl+Alt+Shift+T on Windows, Control+T on Mac), and choose _Layout_. Provide a file name in the dialog, for example, `layout_job_detail.xml`.

This is a refactoring step in XML much like extracting a method is a natural and frequently used action in our Kotlin code. The selection ends up in a new XML file, and an `include` will show up in its original location, simplifying `fragment_job_detail.xml` to a very manageable layout:

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
    <include layout="@layout/layout_job_detail" />

</ViewFlipper>
``` 

This technique would also scale very neatly if the ViewFlipper had many children. 

(You could even extract the single `ProgressBar` to its own layout to be more consistent.)

## Continue

[Something complex this way comes.](./dagger-factory-details.md)
