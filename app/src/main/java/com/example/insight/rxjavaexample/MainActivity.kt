package com.example.insight.rxjavaexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

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

        Observable.just("APPL", "GOOGLE", "TWTR")
                .subscribe({ s ->
                    stockDataAdapter.add(s)
                })

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = stockDataAdapter
    }

    class StockDataAdapter : RecyclerView.Adapter<StockUpdateViewHolder>() {
        private val data = ArrayList<String>()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StockUpdateViewHolder? {
            return parent?.let {
                val v = LayoutInflater.from(it.context).inflate(R.layout.stock_update_item, parent, false)
                StockUpdateViewHolder(v)
            }
        }

        override fun onBindViewHolder(holder: StockUpdateViewHolder, position: Int) {
            holder.stockSymbol.text = data.get(position)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        public fun add(stockSymbol: String) {
            data.add(stockSymbol)
            notifyItemInserted(data.size - 1)
        }
    }

    class StockUpdateViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        @BindView(R.id.stock_item_symbol) lateinit var stockSymbol: TextView
        init {
            ButterKnife.bind(this, v)
        }
    }
}

