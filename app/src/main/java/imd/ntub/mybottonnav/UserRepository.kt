package imd.ntub.mybottonnav

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserRepository(context: Context) {
    private val dbHelper = UserDatabaseHelper(context)

    fun addUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserDatabaseHelper.COLUMN_NAME, user.name)
            put(UserDatabaseHelper.COLUMN_PHONE, user.phone)
        }
        return db.insert(UserDatabaseHelper.TABLE_NAME, null, values)
    }

    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserDatabaseHelper.COLUMN_NAME, user.name)
            put(UserDatabaseHelper.COLUMN_PHONE, user.phone)
        }
        return db.update(UserDatabaseHelper.TABLE_NAME, values, "${UserDatabaseHelper.COLUMN_ID} = ?", arrayOf(user.id.toString()))
    }

    fun deleteUser(userId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(UserDatabaseHelper.TABLE_NAME, "${UserDatabaseHelper.COLUMN_ID} = ?", arrayOf(userId.toString()))
    }

    fun getAllUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(UserDatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
        val users = mutableListOf<User>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_NAME))
                val phone = getString(getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PHONE))
                users.add(User(id, name, phone))
            }
        }
        cursor.close()
        return users
    }
}