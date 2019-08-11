package com.example.roomsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val TAG: String = "RoomSample.MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.async {
            // 永続データベースを作成
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-name").build()

            // インメモリデータベースを作成
            // val db = Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java).build()

            // データモデルを作成
            val user = User()
            user.firstName = "Nanashi"
            user.lastName = "001"
            user.age = 20

            // データを保存
            launch {
                db.userDao().insert(user)
                Log.d(TAG, "insert !")
            }.join()

            // 特定データの抽出
            launch {
                val sUser = db.userDao().findByName(user.firstName!!,user.lastName!!)
                Log.d(TAG, "find by user : ${sUser.uid}")
            }.join()

            // 複数データを登録
            launch {
                val user2 = User()
                user2.uid = 2
                user2.firstName = "Nanashi"
                user2.lastName = "002"
                user2.age = 22

                val user3 = User()
                user3.firstName = "Nanashi"
                user3.lastName = "003"
                user3.age = 33

                val list: MutableList<User> = mutableListOf()
                list.add(user2)
                list.add(user3)
                db.userDao().insertiOrUpdateUsers(list)
            }.join()

            // 全データの取得
            launch {
                // データの呼び出し
                val list: List<User> = db.userDao().getAll()
                list.forEach { Log.d(TAG, "${it.uid} / ${it.firstName} / ${it.lastName} / ${it.age}") }
            }.join()

            // データの更新と登録
            launch {
                val user2 = User()
                user2.uid = 2
                user2.firstName = "Nanashi update"
                user2.lastName = "002"
                user2.age = 22

                val user3 = User()
                user3.uid = 3
                user3.firstName = "Nanashi update"
                user3.lastName = "003"
                user3.age = 33

                val user4 = User()
                user4.firstName = "Nanashi insert"
                user4.lastName = "004"
                user4.age = 44

                val list: MutableList<User> = mutableListOf()
                list.add(user2)
                list.add(user3)
                list.add(user4)
                db.userDao().insertiOrUpdateUsers(list)
            }.join()

            // 全データの取得
            launch {
                // データの呼び出し
                val list: List<User> = db.userDao().getAll()
                list.forEach { Log.d(TAG, "${it.uid} / ${it.firstName} / ${it.lastName} / ${it.age}") }
            }.join()

            // データを削除
            launch {
                db.userDao().deleteAll()
                Log.d(TAG, "delete !")
            }.join()
        }
    }
}
