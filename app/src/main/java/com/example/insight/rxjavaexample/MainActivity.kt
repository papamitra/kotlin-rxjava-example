package com.example.insight.rxjavaexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.Date
import java.io.Serializable
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    @BindView(R.id.stock_updates_recycler_view) lateinit var recyclerView: RecyclerView
    private val layoutManager by lazy {
        android.support.v7.widget.LinearLayoutManager(this)
    }

    private val stockDataAdapter by lazy {
        StockDataAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)
        val yahooService = RetrofitYahooServiceFactory().create()

        val query = "select * from yahoo.finance.quote where symbol in ('YHOO', 'AAPL', 'GOOG', 'MSFT')"
        val env = "store://datatables.org/alltableswithkeys"

        yahooService.yqlQuery(query,env)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .map { r -> r.query.results.quote }
                .flatMap { r -> Observable.fromIterable(r) }
                .map { r -> StockUpdate.create(r) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { stockUpdate ->
                    Log.d("APP", "New update" + stockUpdate.stockSymbol)
                    stockDataAdapter.add(stockUpdate)
                }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = stockDataAdapter
    }

    class StockDataAdapter : RecyclerView.Adapter<StockUpdateViewHolder>() {
        private val data = ArrayList<StockUpdate>()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StockUpdateViewHolder? {
            return parent?.let {
                val v = LayoutInflater.from(it.context).inflate(R.layout.stock_update_item, parent, false)
                StockUpdateViewHolder(v)
            }
        }

        override fun onBindViewHolder(holder: StockUpdateViewHolder, position: Int) {
            val stockUpdate = data.get(position)
            holder.setStockSymbol(stockUpdate.stockSymbol)
            holder.setPrice(stockUpdate.price)
            holder.setDate(stockUpdate.date)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun add(s: StockUpdate) {
            data.add(s)
            notifyItemInserted(data.size - 1)
        }
    }

    class StockUpdateViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        companion object {
            val PRICE_FORMAT = DecimalFormat("#0.00")
        }

        @BindView(R.id.stock_item_symbol) lateinit var stockSymbol: TextView
        @BindView(R.id.stock_item_date) lateinit var date: TextView
        @BindView(R.id.stock_item_price) lateinit var price: TextView

        fun setPrice(price: BigDecimal) {
            this.price.text = PRICE_FORMAT.format(price.toFloat())
        }

        fun setStockSymbol(stockSymbol: String) {
            this.stockSymbol.text = stockSymbol
        }

        fun setDate(date: Date) {
            this.date.text = DateFormat.format("yyyy-MM-dd hh:mm", date)
        }

        init {
            ButterKnife.bind(this, v)
        }
    }

    class StockUpdate(val stockSymbol: String, val price: BigDecimal, val date: Date) : Serializable {
        constructor(stockSymbol: String, price: Double, date: Date) : this(stockSymbol, BigDecimal(price), date)

        companion object {
            fun create(r: YahooStockQuote): StockUpdate {
                Log.d("App", "symbol: " + r.symbol + ", price: " + r.lastTradePriceOnly.toString())
                return StockUpdate(r.symbol, r.lastTradePriceOnly ?: BigDecimal(-1), Date())
            }
        }
    }
}

