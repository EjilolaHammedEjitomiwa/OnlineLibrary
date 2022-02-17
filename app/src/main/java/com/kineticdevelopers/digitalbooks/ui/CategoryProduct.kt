package com.kineticdevelopers.digitalbooks.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.adaper.AllProductAdapter
import com.kineticdevelopers.digitalbooks.helper.Constants
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.model.CategoryModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_category_product.*
import kotlinx.android.synthetic.main.fragment_books.view.*

class CategoryProduct : AppCompatActivity() {
    private var bookList = ArrayList<CategoryModel>()
    private var bookAdapter: AllProductAdapter? = null

    var category:String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_product)

        category = intent.getStringExtra(Constants.category)

        category_book_title.text = "${category} Books"

        bookAdapter = AllProductAdapter(this, bookList)
        category_book_recyclerview.setHasFixedSize(true)
        val bookManager = LinearLayoutManager(this)
        category_book_recyclerview.layoutManager = bookManager
        category_book_recyclerview.adapter = bookAdapter

        getBooks()
    }

    private fun getBooks() {
        Utils.showLoader(this,"Loading...")
        Utils.database()
            .collection(Constants.books)
            .whereEqualTo(Constants.category,category)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    bookList.clear()
                    for (data in it.documents){
                        val item = data.toObject(CategoryModel::class.java)
                        item!!.bookID = data.id
                        bookList.add(item)
                    }
                    bookAdapter!!.notifyDataSetChanged()
                    Utils.dismissLoader()
                }else{
                    Utils.dismissLoader()
                    Toasty.info(this,"${category}  has empty books ",Toasty.LENGTH_LONG).show()
                }
            }
    }
}