package com.setup.firstApp.service

import com.setup.firstApp.model.Outlet
import com.setup.firstApp.repository.OutletRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OutletService {

    @Autowired
    private lateinit var outletRepo: OutletRepo

    fun create(outlet: Outlet): Outlet = outletRepo.save(outlet)

    fun getAll(): List<Outlet> = outletRepo.findAll()

    fun getById(id: String): Outlet? = outletRepo.findById(id).orElse(null)

    fun update(id: String, updated: Outlet): Outlet? {
        return if (outletRepo.existsById(id)) {
            outletRepo.save(updated.copy(id = id))
        } else null
    }

    fun delete(id: String): Boolean {
        return if (outletRepo.existsById(id)) {
            outletRepo.deleteById(id)
            true
        } else false
    }
}