package com.example.ilink_beauty

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


data class User(val username: String? = null, val description: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

data class Item(
    val username: String = "",
    val description: String,
)

data class SelectedItem(
    val itemName: String,
    val quantity: Int
)

class MainActivity : AppCompatActivity() {

    private lateinit var buttonContainer: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var showItemListButton: Button

    private val itemList = mutableListOf<Item>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)


        buttonContainer = findViewById(R.id.leftContainer)
        recyclerView = findViewById(R.id.recyclerView)
        showItemListButton = findViewById(R.id.showItemListButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter(this, itemList)
        recyclerView.adapter = itemAdapter

        // Firebase 데이터베이스 참조 설정
        databaseReference = FirebaseDatabase.getInstance().reference.child("TEST").child("ITEM")

        readItemsFromFirebase()

        showItemListButton.setOnClickListener {
            showSelectedItems()
        }

    }


    fun writeNewUser(userId: String, name: String, description: String) {
        val user = User(name, description)

        databaseReference.child("users").child(userId).setValue(user)
    }


    private fun readItemsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryName = categorySnapshot.key ?: continue
                    // 기타, 실, 약물 버튼 생성
                    addButton(categoryName)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 읽기 실패 시 처리
                println("Database error: ${databaseError.message}")
            }
        })
    }
    private fun addButton(categoryName: String) {
        val newButton = Button(this)
        newButton.text = categoryName
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.gravity = Gravity.CENTER
        newButton.layoutParams = layoutParams
        buttonContainer.addView(newButton)

        newButton.setOnClickListener {
            // 클릭한 버튼에 해당하는 데이터를 오른쪽 화면에 표시하는 처리
            displayCategoryItems(categoryName)
        }


    }

    private fun displayCategoryItems(categoryName: String) {
        itemList.clear() // 기존 데이터 초기화

        databaseReference.child(categoryName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapshot in dataSnapshot.children) {
                    val name = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = itemSnapshot.child("description").getValue(String::class.java) ?: ""

                    // 데이터 추가
                    val newItem = Item(name, description)
                    itemList.add(newItem)
                }

                // RecyclerView 갱신
                itemAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 읽기 실패 시 처리
                println("Database error: ${databaseError.message}")
            }
        })
    }

    fun showSelectedItems() {
        val selectedItems = itemAdapter.getSelectedItems()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("선택된 아이템 리스트")

        val items = selectedItems.map { "${it.itemName}: ${it.quantity}개" }.toTypedArray()
        builder.setItems(items) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}