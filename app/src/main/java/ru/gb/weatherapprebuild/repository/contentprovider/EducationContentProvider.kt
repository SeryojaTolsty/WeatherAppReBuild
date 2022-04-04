package ru.gb.weatherapprebuild.repository.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import ru.gb.weatherapprebuild.App
import ru.gb.weatherapprebuild.App.Companion.getHistoryDao
import ru.gb.weatherapprebuild.R
import ru.gb.weatherapprebuild.model.room.*

private const val URI_ALL = 1 // URI для всех записей
private const val URI_ID = 2 // URI для конкретной записи
private const val ENTITY_PATH =
    "HistoryEntity" // Часть пути (будем определять путь до HistoryEntity)

class EducationContentProvider : ContentProvider() {
    private var authorities: String? = null
    private lateinit var uriMatcher: UriMatcher

    private var entityContentType: String? = null
    private var entityContentItemType: String? = null

    private lateinit var contentUri: Uri // Адрес URI Provider’а

    override fun onCreate(): Boolean {
        authorities = context?.resources?.getString(R.string.authorities)
        // Вспомогательный класс для определения типа запроса
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        "content:ru.gb.kotlinapp.geekbrains.provider/HistoryEntity"
        uriMatcher.addURI(authorities, ENTITY_PATH, URI_ALL)

        "content:ru.gb.kotlinapp.geekbrains.provider/HistoryEntity/#"
        uriMatcher.addURI(authorities, "$ENTITY_PATH/#", URI_ID)

        // Тип содержимого - все объекты
        entityContentType = "vnd.android.cursor.dir/vnd.$authorities.$ENTITY_PATH"
        // Тип содержимого - один объект
        entityContentItemType = "vnd.android.cursor.item/vnd.$authorities.$ENTITY_PATH"

        // Строка для доступа к Provider’у
        contentUri = Uri.parse("content://$authorities/$ENTITY_PATH")

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val historyDao: HistoryDao = getHistoryDao()

        val cursor = when (uriMatcher.match(uri)) {
            URI_ALL -> historyDao.getHistoryCursor()
            URI_ID -> {
                // Определяем id из URI адреса. Класс ContentUris помогает это сделать
                val id = ContentUris.parseId(uri)

                historyDao.getHistoryCursor(id)
            }
            else -> throw IllegalArgumentException("Wrong URI: $uri")
        }

        //cursor.setNotificationUri(context?.contentResolver, contentUri)

        return cursor
    }

    override fun getType(uri: Uri): String? {
        when (uriMatcher.match(uri)) {
            URI_ALL -> return entityContentType
            URI_ID -> return entityContentItemType
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        require(uriMatcher.match(uri) == URI_ALL) { "Wrong URI: $uri" }

        val historyDao = getHistoryDao()

        val entity = map(values)
        val id: Long = entity.id

        historyDao.insert(entity)

        // Конкретная запись в базе
        val resultUri = ContentUris.withAppendedId(contentUri, id)

        context?.contentResolver?.notifyChange(resultUri, null)

        return resultUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        require(uriMatcher.match(uri) == URI_ID) { "Wrong URI: $uri" }

        val historyDao = getHistoryDao()
        val id = ContentUris.parseId(uri)

        historyDao.deleteById(id)

        context?.contentResolver?.notifyChange(uri, null)
        return 1
    }

    override fun update(uri: Uri, values: ContentValues?, selection : String?, selectionArgs: Array<out String>?): Int {
        require(uriMatcher.match(uri) == URI_ID) { "Wrong URI: $uri" }

        val historyDao = getHistoryDao()
        historyDao.update(map(values))

        context?.contentResolver?.notifyChange(uri, null)

        return 1
    }

    private fun map(values: ContentValues?): HistoryEntity {
        return if (values == null) {
            HistoryEntity()
        } else {
            val id = if (values.containsKey(ID)) values[ID] as Long else 0
            val city = values[CITY] as String
            val temperature = values[TEMPERATURE] as Int
            HistoryEntity(id, city, temperature)
        }
    }
}