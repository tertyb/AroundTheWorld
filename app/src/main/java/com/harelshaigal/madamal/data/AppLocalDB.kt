package com.harelshaigal.aroundtw.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.harelshaigal.aroundtw.application.AroundTwApplication
import com.harelshaigal.aroundtw.data.report.Report
import com.harelshaigal.aroundtw.data.report.ReportDao
import com.harelshaigal.aroundtw.data.user.User
import com.harelshaigal.aroundtw.data.user.UserDao


@Database(entities = [Report::class, User::class], version = 5)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun reportDao(): ReportDao?
    abstract fun userDao(): UserDao?
}


object AppLocalDb {
    var db = Room.databaseBuilder(
        AroundTwApplication.context,
        AppLocalDbRepository::class.java,
        "dbFileName.db"
    )
        .fallbackToDestructiveMigration()
        .build()
}