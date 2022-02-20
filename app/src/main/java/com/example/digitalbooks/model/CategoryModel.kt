package com.example.digitalbooks.model

class CategoryModel(
    val name:String ? = null,
    val icon_url:String ? =  null,
    val book_image:String? = null,
    val category:String? =null,
    val description:String? = null,
    val link:String? = null,
    val price:String? = null,
    val seller:String? = null,
    val title:String? = null,
    val recommended:Boolean? = null,
    val views:Int? = null,
    var bookID:String? = null

) {
}