package ms.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.entities.Phone;
import ms.repository.PhoneRepository;

@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    public Optional<Phone> getPhoneById(Long id) {
        return phoneRepository.findById(id);
    }

    public List<Phone> getPhonesByUserId(UUID userId) {
        return phoneRepository.findByUserId(userId);
    }

    public Phone createPhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    public Phone updatePhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    public void deletePhone(Long id) {
        phoneRepository.deleteById(id);
    }

}
