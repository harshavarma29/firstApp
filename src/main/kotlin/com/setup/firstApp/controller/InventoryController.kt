package com.setup.firstApp.controller

import com.setup.firstApp.model.Inventory
import com.setup.firstApp.service.InventoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory")
class InventoryController {

    @Autowired
    private lateinit var inventoryService: InventoryService

    @PostMapping
    fun create(@RequestBody inventory: Inventory): ResponseEntity<Inventory> =
        ResponseEntity.ok(inventoryService.create(inventory))

    @GetMapping
    fun getAll(): ResponseEntity<List<Inventory>> =
        ResponseEntity.ok(inventoryService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<Inventory> {
        val item = inventoryService.getById(id)
        return if (item != null) ResponseEntity.ok(item) else ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody inventory: Inventory): ResponseEntity<Inventory> {
        val updated = inventoryService.update(id, inventory)
        return if (updated != null) ResponseEntity.ok(updated) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        return if (inventoryService.delete(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}