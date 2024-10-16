package com.example.swen3001groupproject

class ChildEvent {
    var day = 0
    var title: String? = null
    var date: String? = null
    var time: String? = null
    var location: String? = null
    var isIndoors = false
    var isOutdoors = false
    var isOnline = false
    var shareWithKids = false

    constructor(
        day: Int, title: String?, date: String?, time: String?, location: String?,
        isIndoors: Boolean, isOutdoors: Boolean, isOnline: Boolean, shareWithKids: Boolean
    ) {
        this.day = day
        this.title = title
        this.date = date
        this.time = time
        this.location = location
        this.isIndoors = isIndoors
        this.isOutdoors = isOutdoors
        this.isOnline = isOnline
        this.shareWithKids = shareWithKids
    }
}