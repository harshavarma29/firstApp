package com.setup.firstApp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("outlet")
data class Outlet (

    @Id
    val id: String? = null,
    val name: String,
    val location: String,
    val isActive: Boolean = true

)