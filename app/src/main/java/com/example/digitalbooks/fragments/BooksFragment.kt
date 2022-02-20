package com.example.digitalbooks.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalbooks.adaper.AllProductAdapter
import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.CategoryModel
import example.digitalbooks.R
import kotlinx.android.synthetic.main.fragment_books.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class BooksFragment : Fragment() {
    private var bookList = ArrayList<CategoryModel>()
    private var bookAdapter: AllProductAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_books, container, false)

        bookAdapter = AllProductAdapter(requireContext(), bookList)
        view.books_frag_bookListRecyclerview.setHasFixedSize(true)
        val bookManager = LinearLayoutManager(requireContext())
        view.books_frag_bookListRecyclerview.layoutManager = bookManager
        view.books_frag_bookListRecyclerview.adapter = bookAdapter

        loadBooks()

        return view
    }

    private fun loadBooks() {
        Utils.showLoader(requireContext(),"Loading...")
        Utils.database()
            .collection(Constants.books)
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
                }
            }
    }

}






