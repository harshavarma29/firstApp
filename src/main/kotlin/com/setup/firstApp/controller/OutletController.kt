package com.setup.firstApp.controller

import com.setup.firstApp.model.Outlet
import com.setup.firstApp.service.OutletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/outlet")
class OutletController {

    @Autowired
    private lateinit var outletService: OutletService

    @PostMapping
    fun create(@RequestBody outlet: Outlet): ResponseEntity<Outlet> =
        ResponseEntity.ok(outletService.create(outlet))

    @GetMapping
    fun getAll(): ResponseEntity<List<Outlet>> =
        ResponseEntity.ok(outletService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<Outlet> {
        val item = outletService.getById(id)
        return if (item != null) ResponseEntity.ok(item) else ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody outlet: Outlet): ResponseEntity<Outlet> {
        val updated = outletService.update(id, outlet)
        return if (updated != null) ResponseEntity.ok(updated) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        return if (outletService.delete(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}