package ms.controller;

import ms.entities.Phone;
import ms.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/phones")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @GetMapping
    public ResponseEntity<List<Phone>> getAllPhones() {
        List<Phone> phones = phoneService.getAllPhones();
        return ResponseEntity.ok(phones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phone> getPhoneById(@PathVariable Long id) {
        Optional<Phone> phone = phoneService.getPhoneById(id);
        return phone.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Phone> createPhone(@RequestBody Phone phone) {
        Phone createdPhone = phoneService.createPhone(phone);
        return new ResponseEntity<>(createdPhone, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Phone> updatePhone(@PathVariable Long id, @RequestBody Phone phone) {
        phone.setId(id);
        Phone updatedPhone = phoneService.updatePhone(phone);
        return ResponseEntity.ok(updatedPhone);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        phoneService.deletePhone(id);
        return ResponseEntity.noContent().build();
    }



}
