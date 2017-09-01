package com.example.insight.rxjavaexample

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by maueki on 17/09/01.
 */

@Entity(tableName = "stocks")
data class Stock(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "stock_symbol") var stockSymbol: String = "",
        @ColumnInfo(name = "price") var price: String = "",
        @ColumnInfo(name = "date") var date: String = ""
)

@Dao
interface StockDao {
    @Query("SELECT * from stocks")
    fun getAllStocks(): Flowable<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStock(stock: Stock)
}

@Database(entities = arrayOf(Stock::class), version = 1)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
}
