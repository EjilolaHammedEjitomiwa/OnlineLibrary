package com.kineticdevelopers.digitalbooks.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.adaper.CategoryAdapter
import com.kineticdevelopers.digitalbooks.adaper.ImageSliderAdapter
import com.kineticdevelopers.digitalbooks.helper.Constants
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.model.CategoryModel
import com.kineticdevelopers.digitalbooks.model.ImageListModel
//import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
    //for home display
    private var imageList = ArrayList<ImageListModel>()
    private var imageSliderAdapter: ImageSliderAdapter? = null

    private var categoryLists = ArrayList<CategoryModel>()
    private var categoryAdapter: CategoryAdapter? = null

    private var recommendedBooksList = ArrayList<CategoryModel>()
    private var recommendedBooksAdapter: CategoryAdapter? = null

    private var popularBooksList = ArrayList<CategoryModel>()
    private var popularBooksAdapter: CategoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        imageSliderAdapter = ImageSliderAdapter(requireContext(), imageList)
        view.home_fragment_imageSlider.setSliderAdapter(imageSliderAdapter!!)


        categoryAdapter = CategoryAdapter(requireContext(), categoryLists,"categories")
        view.home_fragment_categories.setHasFixedSize(true)
        val categoryManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        view.home_fragment_categories.layoutManager = categoryManager
        view.home_fragment_categories.adapter = categoryAdapter

        // for recommended books

        recommendedBooksAdapter = CategoryAdapter(requireContext(), recommendedBooksList,"recommended")
        view.home_fragment_recommendedList.setHasFixedSize(true)
        val recommendedBookManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        view.home_fragment_recommendedList.layoutManager = recommendedBookManager
        view.home_fragment_recommendedList.adapter = recommendedBooksAdapter

        // for popular books

        popularBooksAdapter = CategoryAdapter(requireContext(), popularBooksList,"recommended")
        view.home_fragment_popularBooksRecyclerview.setHasFixedSize(true)
        val popularBookManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        view.home_fragment_popularBooksRecyclerview.layoutManager = popularBookManager
        view.home_fragment_popularBooksRecyclerview.adapter = popularBooksAdapter

        getImageLists()
        getCategories()
        getRecommendedBooks()
        getPopularBooks()

        return view

    }

    private fun getPopularBooks() {
        Utils.database()
            .collection(Constants.books)
            .orderBy("views",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    popularBooksList.clear()
                    for (data in it.documents){
                        val item = data.toObject(CategoryModel::class.java)
                        item!!.bookID = data.id
                        if (item.recommended!!){
                            popularBooksList.add(item)
                        }
                    }
                    popularBooksAdapter!!.notifyDataSetChanged()
                    Utils.dismissLoader()
                }
            }
    }

    private fun getRecommendedBooks() {
        Utils.database()
            .collection(Constants.books)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    recommendedBooksList.clear()
                    for (data in it.documents){
                        val item = data.toObject(CategoryModel::class.java)
                        item!!.bookID = data.id
                        if (item.recommended!!){
                            recommendedBooksList.add(item)
                        }
                    }
                    recommendedBooksAdapter!!.notifyDataSetChanged()
                }
            }
    }

    private fun getCategories() {
        Utils.database()
            .collection(Constants.categories)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    categoryLists.clear()
                    for (data in it.documents){
                        val item = data.toObject(CategoryModel::class.java)
                        categoryLists.add(item!!)
                    }
                    categoryAdapter!!.notifyDataSetChanged()
                }
            }
    }

    private fun getImageLists() {
        Utils.showLoader(requireContext(),"Loading...")
        Utils.database()
            .collection(Constants.homeSliderImages)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    imageList.clear()
                    for (data in it.documents){
                        val item = data.toObject(ImageListModel::class.java)
                        imageList.add(item!!)
                    }
                    imageSliderAdapter!!.notifyDataSetChanged()
//                    try {
//                      //  home_fragment_imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
//                    } catch (e: NullPointerException) {
//                    }
                    try {
                        home_fragment_imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    } catch (e: NullPointerException) {
                    }
                    try {
                        home_fragment_imageSlider.startAutoCycle()
                    } catch (e: NullPointerException) {
                    }
                }
            }
    }
}