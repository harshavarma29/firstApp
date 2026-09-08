package com.setup.firstApp.service

import com.setup.firstApp.model.Inventory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.setup.firstApp.repository.InventoryRepo;

@Service
class InventoryService {

    @Autowired
    private lateinit var inventoryRepo: InventoryRepo

    fun create(inventory: Inventory): Inventory = inventoryRepo.save(inventory)

    fun getAll(): List<Inventory> = inventoryRepo.findAll()

    fun getById(id: String): Inventory? = inventoryRepo.findById(id).orElse(null)

    fun update(id: String, updated: Inventory): Inventory? {
        return if (inventoryRepo.existsById(id)) {
            inventoryRepo.save(updated.copy(id = id))
        } else null
    }

    fun delete(id: String): Boolean {
        return if (inventoryRepo.existsById(id)) {
            inventoryRepo.deleteById(id)
            true
        } else false
    }
}
