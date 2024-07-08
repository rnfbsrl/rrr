package com.example.ilink_beauty

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView




class ItemAdapter(private val context: Context, private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION
    private val selectedItems: MutableList<SelectedItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item,position)


    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(item: Item, position: Int) {
            itemName.text = item.username

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                // 선택된 아이템의 배경색을 업데이트
                showDialog(item)
                // 클릭된 아이템에 대한 다른 로직 추가 가능
            }

            // 클릭 애니메이션 설정
            itemView.isClickable = true
            itemView.isFocusable = true


        }


        private fun showDialog(item: Item) {
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_item_detail, null)

            val itemNameTextView: TextView = view.findViewById(R.id.dialogItemNameTextView)
            val decrementButton: Button = view.findViewById(R.id.decrementButton)
            val incrementButton: Button = view.findViewById(R.id.incrementButton)
            val quantityTextView: TextView = view.findViewById(R.id.quantityTextView)

            itemNameTextView.text = item.username

            var quantity = 1
            quantityTextView.text = quantity.toString()

            decrementButton.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    quantityTextView.text = quantity.toString()
                }
            }

            incrementButton.setOnClickListener {
                quantity++
                quantityTextView.text = quantity.toString()
            }

            builder.setView(view)
            builder.setPositiveButton("담기") { dialog, _ ->
                selectedItems.add(SelectedItem(item.username, quantity))
                dialog.dismiss()
            }

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    fun getSelectedItems(): List<SelectedItem> {
        return selectedItems
    }


}