package com.zeal.loyaltyapp.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zeal.loyaltyapp.data.PaymentTransaction
import com.zeal.loyaltyapp.databinding.ItemPaymentTransactionBinding

class PaymentTransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<PaymentTransaction>
) : RecyclerView.Adapter<PaymentTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    inner class ViewHolder(val binding: ItemPaymentTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: PaymentTransaction) {
            binding.tvTransactionID.text = transaction.id
            binding.tvTransactionTime.text = transaction.time
            binding.tvCardNumber.text = transaction.cardNumber

            val totalAmount = transaction.totalAmount.toDouble()
            val discountedAmount = transaction.discountedAmount.toDoubleOrNull() ?: 0.0
            val remainingAmount = totalAmount - discountedAmount

            binding.tvTotalAmount.text = remainingAmount.toString()

            binding.btnApplyDiscount.setOnClickListener {
                showDiscountDialog(transaction)
            }
        }

        private fun showDiscountDialog(transaction: PaymentTransaction) {
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("Apply Discount")

            val input = EditText(binding.root.context)
            input.hint = "Enter discount amount"
            builder.setView(input)

            builder.setPositiveButton("Apply") { dialog, _ ->
                val discountAmount = input.text.toString().toDoubleOrNull() ?: 0.0
                applyDiscount(transaction, discountAmount)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        private fun applyDiscount(transaction: PaymentTransaction, discountAmount: Double) {
            if (discountAmount <= 0) {
                Toast.makeText(context, "Transaction ${transaction.id} not updated because discount amount is 0", Toast.LENGTH_SHORT).show()
                return
            }

            val totalAmount = transaction.totalAmount.toDouble()
            var discountedAmount = transaction.discountedAmount.toDoubleOrNull() ?: 0.0
            discountedAmount += discountAmount
            transaction.discountedAmount = discountedAmount.toString()
            notifyItemChanged(transactions.indexOf(transaction))

            updateTransactionInContentProvider(transaction.id, totalAmount, discountedAmount, transaction.cardNumber, transaction.time)
        }

        private fun updateTransactionInContentProvider(id: String, totalAmount: Double, discountedAmount: Double, cardNumber: String, time: String) {
            val values = ContentValues().apply {
                put("id", id)
                put("total", totalAmount.toString()) // Update total amount (if needed)
                put("discountedAmount", discountedAmount.toString())
                put("cardNumber", cardNumber)
                put("time", time)
            }

            val uri = Uri.parse("content://com.zeal.transaction.provider/transactions")
            context.contentResolver.update(uri, values, "id=?", arrayOf(id))

            Toast.makeText(context, "Transaction $id updated with discounted amount: $discountedAmount", Toast.LENGTH_SHORT).show()
        }
    }
}
