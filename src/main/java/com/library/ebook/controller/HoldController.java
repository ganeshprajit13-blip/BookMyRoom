package com.library.ebook.controller;

import com.library.ebook.dto.ApiResponse;
import com.library.ebook.dto.HoldRequest;
import com.library.ebook.entity.EbookHold;
import com.library.ebook.service.HoldService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ebooks/holds")
@CrossOrigin(origins = "*")
public class HoldController {

    private final HoldService holdService;

    public HoldController(HoldService holdService) {
        this.holdService = holdService;
    }

    @GetMapping
    public ResponseEntity<List<EbookHold>> getAllHolds(@RequestParam(required = false, defaultValue = "false") boolean waitingOnly) {
        if (waitingOnly) {
            return ResponseEntity.ok(holdService.getWaitingHolds());
        }
        return ResponseEntity.ok(holdService.getAllHolds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EbookHold> getHoldById(@PathVariable Long id) {
        return ResponseEntity.ok(holdService.getHoldById(id));
    }

    @PostMapping
    public ResponseEntity<EbookHold> placeHold(@Valid @RequestBody HoldRequest request) {
        EbookHold hold = holdService.placeHold(request);
        return new ResponseEntity<>(hold, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<EbookHold>> cancelHold(@PathVariable Long id) {
        EbookHold cancelled = holdService.cancelHold(id);
        return ResponseEntity.ok(ApiResponse.ok("Digital hold cancelled successfully", cancelled));
    }
}
