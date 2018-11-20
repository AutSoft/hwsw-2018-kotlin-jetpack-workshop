package hu.autsoft.hwswmobile18.jobs.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hu.autsoft.hwswmobile18.jobs.ui.jobdetail.JobDetailViewModel
import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListViewModel
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(daggerViewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(JobListViewModel::class)
    abstract fun bindListViewModel(jobListViewModel: JobListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(JobDetailViewModel::class)
    abstract fun bindJobDetailViewModel(jobDetailViewModel: JobDetailViewModel): ViewModel

}
