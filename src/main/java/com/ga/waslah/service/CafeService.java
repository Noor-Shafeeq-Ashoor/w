package com.ga.waslah.service;

import com.ga.waslah.dto.CafeRequest;
import com.ga.waslah.dto.CafeResponse;
import com.ga.waslah.exception.ForbiddenException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.Cafe;
import com.ga.waslah.model.Role;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.CafeRepository;
import com.ga.waslah.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CafeService {

    private final CafeRepository cafeRepository;
    private final UserRepository userRepository;

    public CafeService(
            CafeRepository cafeRepository,
            UserRepository userRepository
    ) {
        this.cafeRepository = cafeRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE CAFE
    // =========================

    public CafeResponse createCafe(
            String username,
            CafeRequest request
    ) {

        User manager = getUser(username);

        // Only Cafe Managers can create cafes.
        checkCafeManager(manager);

        Cafe cafe = new Cafe();

        cafe.setName(request.getName());
        cafe.setDescription(request.getDescription());
        cafe.setLocation(request.getLocation());

        // New cafes are active by default.
        cafe.setActive(true);

        // The authenticated Cafe Manager becomes the owner/manager.
        cafe.setManager(manager);

        return toResponse(cafeRepository.save(cafe));
    }

    // =========================
    // GET ALL ACTIVE CAFES
    // =========================

    public List<CafeResponse> getAllActiveCafes() {

        return cafeRepository.findAll()
                .stream()
                .filter(Cafe::getActive)
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // GET CAFE BY ID
    // =========================

    public CafeResponse getCafeById(Long id) {

        Cafe cafe = getCafe(id);

        return toResponse(cafe);
    }

    // =========================
    // GET MANAGER'S CAFES
    // =========================

    public List<CafeResponse> getManagerCafes(
            String username
    ) {

        User manager = getUser(username);

        // Only Cafe Managers can access their managed cafes.
        checkCafeManager(manager);

        return cafeRepository.findByManager(manager)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // UPDATE CAFE
    // =========================

    public CafeResponse updateCafe(
            Long id,
            String username,
            CafeRequest request
    ) {

        User manager = getUser(username);

        checkCafeManager(manager);

        Cafe cafe = getCafe(id);

        // A manager can only update cafe's they manage.
        checkOwnership(cafe, manager);

        cafe.setName(request.getName());
        cafe.setDescription(request.getDescription());
        cafe.setLocation(request.getLocation());

        return toResponse(cafeRepository.save(cafe));
    }

    // =========================
    // DEACTIVATE CAFE
    // =========================

    public void deactivateCafe(
            Long id,
            String username
    ) {

        User manager = getUser(username);

        checkCafeManager(manager);

        Cafe cafe = getCafe(id);

        // Only the cafe's manager can deactivate it.
        checkOwnership(cafe, manager);

        cafe.setActive(false);

        cafeRepository.save(cafe);
    }

    // =========================
    // ACTIVATE CAFE
    // =========================

    public void activateCafe(
            Long id,
            String username
    ) {

        User manager = getUser(username);

        checkCafeManager(manager);

        Cafe cafe = getCafe(id);

        checkOwnership(cafe, manager);

        cafe.setActive(true);

        cafeRepository.save(cafe);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private Cafe getCafe(Long id) {

        return cafeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cafe not found"
                        ));
    }

    private void checkCafeManager(User user) {

        if (user.getRole() != Role.CAFE_MANAGER) {
            throw new ForbiddenException(
                    "Only Cafe Managers can manage cafes"
            );
        }
    }

    private void checkOwnership(
            Cafe cafe,
            User manager
    ) {

        if (cafe.getManager() == null ||
                !cafe.getManager()
                        .getId()
                        .equals(manager.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to manage this cafe"
            );
        }
    }

    private CafeResponse toResponse(Cafe cafe) {

        return new CafeResponse(
                cafe.getId(),
                cafe.getName(),
                cafe.getDescription(),
                cafe.getLocation(),
                cafe.getActive(),
                cafe.getManager().getUsername()
        );
    }
}