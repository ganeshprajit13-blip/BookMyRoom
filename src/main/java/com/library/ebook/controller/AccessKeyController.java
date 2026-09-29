package com.library.ebook.controller;

import com.library.ebook.dto.ApiResponse;
import com.library.ebook.entity.AccessKey;
import com.library.ebook.service.AccessKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ebooks/access-keys")
@CrossOrigin(origins = "*")
public class AccessKeyController {

    private final AccessKeyService accessKeyService;

    public AccessKeyController(AccessKeyService accessKeyService) {
        this.accessKeyService = accessKeyService;
    }

    @GetMapping
    public ResponseEntity<List<AccessKey>> getAllAccessKeys() {
        return ResponseEntity.ok(accessKeyService.getAllAccessKeys());
    }

    @GetMapping("/{code}")
    public ResponseEntity<AccessKey> getAccessKeyByCode(@PathVariable String code) {
        return ResponseEntity.ok(accessKeyService.getAccessKeyByCode(code));
    }

    @PostMapping("/revoke-expired")
    public ResponseEntity<ApiResponse<Map<String, Object>>> revokeExpired() {
        int revokedCount = accessKeyService.revokeExpiredAccessKeys();
        return ResponseEntity.ok(ApiResponse.ok("Revocation process completed", Map.of(
                "revokedCount", revokedCount,
                "message", revokedCount > 0 ? "Revoked " + revokedCount + " expired digital keys." : "No expired keys found."
        )));
    }
}
