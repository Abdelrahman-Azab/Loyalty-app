package com.zeal.loyaltyapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeal.loyaltyapp.adapter.PaymentTransactionAdapter
import com.zeal.loyaltyapp.data.PaymentTransaction
import com.zeal.loyaltyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PaymentTransactionAdapter
    private var transactionList: MutableList<PaymentTransaction> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PaymentTransactionAdapter(this, transactionList)
        binding.recyclerView.adapter = adapter

        binding.refreshButton.setOnClickListener {
            refreshData()
        }

        refreshData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        val contactUri = Uri.parse("content://com.zeal.transaction.provider/transactions")

        val projection = null

        val selectionClause = null
        val selectionArguments = null
        val sortOrder = null
        val cursor = contentResolver.query(
            contactUri,
            projection,
            selectionClause,
            selectionArguments,
            sortOrder
        )

        transactionList.clear()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val total = cursor.getString(cursor.getColumnIndexOrThrow("total"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                val cardNumber = cursor.getString(cursor.getColumnIndexOrThrow("cardNumber"))
                val discountedAmount = cursor.getString(cursor.getColumnIndexOrThrow("discountedAmount"))

                val transaction = PaymentTransaction(id = id, cardNumber = cardNumber, time = time, totalAmount = total, discountedAmount = discountedAmount)
                transactionList.add(transaction)

            } while (cursor.moveToNext())
            cursor.close()
        }

        adapter.notifyDataSetChanged()
    }
}
