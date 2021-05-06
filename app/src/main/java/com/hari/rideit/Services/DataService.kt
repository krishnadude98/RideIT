package com.hari.rideit.Services

import com.hari.rideit.model.Category

object DataService {
    val categories= listOf(
        Category("Share a ride","cat1"),
        Category("Rent a Ride","cat2"),
        Category("Rent a Rider","cat3"),
        Category("Suggestions?","cat4")
    )
    var latitude:String=""
    var longitude:String=""
    val jwttoken:String=""
}