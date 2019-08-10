package com.example.roomsample

import androidx.room.*

@Dao
interface UserDao {

    // シンプルなSELECTクエリ
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    // 複数の引数も渡せる
    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    // データモデルのクラスを引数に渡すことで、データの作成ができる。
    @Insert
    fun insert(user: User)

    // 可変長引数
    @Insert
    fun insertAll(vararg users: User)

    // List渡し
    @Insert
    fun insertAll(users: List<User>)

    // List渡し データがなければInsert あればUpdate いわゆるUpSert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertiOrUpdateUsers(list: List<User>)

    // データモデルのクラスを引数に渡すことで、データの削除ができる。
    @Delete
    fun delete(user: User)

    //　条件での削除は@Queryを使用してSQLを記述
    @Query("DELETE FROM user")
    fun deleteAll()
}