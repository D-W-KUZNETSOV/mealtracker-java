package com.e.mealtracker.service;

import com.e.mealtracker.dto.RegisterRequest;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.exception.UserAlreadyExistsException;
import com.e.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.e.mealtracker.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Р’РђР–РќРћ: Р·РґРµСЃСЊ РІ СЃРєРѕР±РєР°С… СѓРєР°Р·Р°РЅ РїР°СЂР°РјРµС‚СЂ RegisterRequest request
    @Transactional
    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ СЃ С‚Р°РєРёРј РёРјРµРЅРµРј СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ СЃ С‚Р°РєРёРј email СѓР¶Рµ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°РЅ");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);

        // РЎРѕР·РґР°С‘Рј РїСЂРѕС„РёР»СЊ СЃСЂР°Р·Сѓ СЃ РґР°С‚РѕР№ СЂРѕР¶РґРµРЅРёСЏ
        UserProfile profile = new UserProfile();
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ РЅРµ РЅР°Р№РґРµРЅ: " + username));
    }

    public void save(User user) {
        userRepository.save(user);

    }
}
