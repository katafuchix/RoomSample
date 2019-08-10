# RoomSample

## build.gradle(app)
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.0.2'
    implementation 'androidx.core:core-ktx:1.0.2'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.2.0'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'

    implementation "androidx.core:core-ktx:1.0.2"
    implementation "androidx.room:room-runtime:2.1.0"
    implementation 'androidx.lifecycle:lifecycle-extensions:2.0.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0'
    kapt "androidx.room:room-compiler:2.1.0"
    implementation "androidx.room:room-ktx:2.1.0"

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2'
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-RC2"
}
```

## User.kt
```
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User {

    // auto increment
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "first_name")
    var firstName: String? = null

    @ColumnInfo(name = "last_name")
    var lastName: String? = null

    var age: Int = 0
}
```

## UserDao.kt 
```
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
```

## MainActivity.kt
```

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


```
