package com.exampleLDAP.demoLDAP.Controller;

import com.exampleLDAP.demoLDAP.Service.ServiceImpl.LdapService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ldap")
public class LdapController {

    private final LdapService ldapService;
    public LdapController(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    @Data
    public static class ActionRequest {
        private String username; // sAMAccountName
        private String userDn;   // opcional
    }

    @PostMapping("/block")
    public ResponseEntity<?> block(@Validated @RequestBody ActionRequest req) {
        try {
            if (req.getUserDn() != null && !req.getUserDn().isBlank()) {
                ldapService.blockAccountByDn(req.getUserDn());
            } else {
                ldapService.blockByUsername(req.getUsername());
            }
            return ResponseEntity.ok().body("Bloqueado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblock(@Validated @RequestBody ActionRequest req) {
        try {
            if (req.getUserDn() != null && !req.getUserDn().isBlank()) {
                ldapService.unblockAccountByDn(req.getUserDn());
            } else {
                ldapService.unblockByUsername(req.getUsername());
            }
            return ResponseEntity.ok().body("Desbloqueado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}