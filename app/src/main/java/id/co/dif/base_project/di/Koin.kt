package id.co.dif.base_project.di

import id.co.dif.base_project.presentation.fragment.BasicInfoFragment
import id.co.dif.base_project.presentation.fragment.ChangeLogFragment
import id.co.dif.base_project.presentation.fragment.DashboardFragment
import id.co.dif.base_project.presentation.fragment.DetailFragment
import id.co.dif.base_project.presentation.fragment.EducationFragment
import id.co.dif.base_project.presentation.fragment.HomeFragment
import id.co.dif.base_project.presentation.fragment.InboxFragment
import id.co.dif.base_project.presentation.fragment.MaintenanceFragment
import id.co.dif.base_project.presentation.fragment.MapsTicketFragment
import id.co.dif.base_project.presentation.fragment.MeFragment
import id.co.dif.base_project.presentation.fragment.MessageNotificationFragment
import id.co.dif.base_project.presentation.fragment.MyDashboardFragment
import id.co.dif.base_project.presentation.fragment.NotificationFragment
import id.co.dif.base_project.presentation.fragment.OverviewFragment
import id.co.dif.base_project.presentation.fragment.SiteFragment
import id.co.dif.base_project.presentation.fragment.SkillFragment
import id.co.dif.base_project.presentation.fragment.TTDashboardFragment
import id.co.dif.base_project.presentation.fragment.TimelineFragment
import id.co.dif.base_project.presentation.fragment.TroubleTicketFragment
import id.co.dif.base_project.presentation.fragment.WorkFragment
import id.co.dif.base_project.persistence.DefaultPersistenceOperator
import id.co.dif.base_project.persistence.PersistenceOperator
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.presentation.fragment.SparePartFragment
import id.co.dif.base_project.presentation.fragment.SparePartListFragment
import id.co.dif.base_project.utils.AndroidDownloader
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 03:46
 *
 */
val koinModules = module {
    single { provideHttpLogingInterceptor() }

    single { provideChuckerInterceptor(androidContext()) }

    single<OkHttpClient>(named("api")) { provideOkHttpClient(get(), get()) }

    single { provideGsonBuilder() }
    single { AndroidDownloader(androidContext()) }


    single { provideRetrofit(get(named("api")), get()) }
    single { provideLocationClient(androidContext()) }

    factory { provideTripleEApi(get()) }
    single<PersistenceOperator> { DefaultPersistenceOperator() }
    single { Preferences(get()) }

    single { MyDashboardFragment() }
    single { TTDashboardFragment() }
    single { MaintenanceFragment() }
    single { provideColorGenerator() }
    single { DashboardFragment() }
    single { MeFragment() }
    single { NotificationFragment() }
    single { MessageNotificationFragment() }
    single { InboxFragment() }
    single { TroubleTicketFragment() }
    single { HomeFragment() }

    single { DetailFragment() }
    single { SiteFragment() }
    single { TimelineFragment() }
    single { ChangeLogFragment() }
    single { MapsTicketFragment() }
    single { SparePartListFragment() }
    single { SparePartFragment() }


    single { OverviewFragment() }
    single { WorkFragment() }
    single { EducationFragment() }
    single { BasicInfoFragment() }
    single { SkillFragment() }
}