package com.setup.firstApp.repository

import com.setup.firstApp.model.Outlet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OutletRepo: MongoRepository<Outlet, String>