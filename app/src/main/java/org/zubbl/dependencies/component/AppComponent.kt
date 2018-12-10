package org.zubbl.dependencies.component

import dagger.Component
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.AddZubblDailog
import org.zubbl.customviews.ConnectZubble
import org.zubbl.customviews.JoinZubblDialog
import org.zubbl.dependencies.module.AppContainerModule
import org.zubbl.dependencies.module.ApplicationModule
import org.zubbl.dependencies.module.NetworkModule
import org.zubbl.service.MyFeedService
import org.zubbl.service.NearByService
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RunTimePermission
import org.zubbl.views.activities.*
import org.zubbl.views.fragments.MyFeed
import org.zubbl.views.fragments.leaderboard.LeaderBoardFragment
import org.zubbl.views.fragments.leaderboard.LeaderboardBottomDialog
import org.zubbl.views.fragments.leaderboard.ProgressFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class), (ApplicationModule::class), (AppContainerModule::class)])
interface AppComponent {

    fun inject(sessionManager: SessionManager) {}
    fun inject(requestCallback: RequestCallback){}

    fun inject(runTimePermission: RunTimePermission) {}



    //ACTIVITIES
    fun inject(splashActivity: SplashActivity){}
    fun inject(loginActivity: LoginActivity){}
    fun inject(mainActivity: MainActivity){}
    fun inject(nearByActivity: NearByActivity){}
    fun inject(registerActivity: RegisterActivity){}
    fun inject(forgotPasswordActivity:ForgotPasswordActivity){}


    //FRAGMENTS
    fun inject(myFeed: MyFeed){}
    fun inject(joinZubblDialog: JoinZubblDialog){}
    fun inject(connectZubble: ConnectZubble){}
    fun inject(leaderboardBottomDialog: LeaderboardBottomDialog){}
    fun inject(addZubblDailog: AddZubblDailog){}
    fun inject(leaderBoardFragment: LeaderBoardFragment){}
    fun inject(progressFragment: ProgressFragment){}

    //SERVICE
    fun inject(nearByService: NearByService){}
    fun inject(myFeedService: MyFeedService){}



    //UTILITY



}