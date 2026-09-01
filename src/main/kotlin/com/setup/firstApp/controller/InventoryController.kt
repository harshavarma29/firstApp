package com.setup.firstApp.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class InventoryController {

    @GetMapping("/get-outlet/{id}")
    fun getOutlet(@PathVariable("id") id: Int): ResponseEntity<String> {
        return ResponseEntity.ok("No data available for Id: ${id}")
    }

}