package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.repository.LocationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(UUID id) {
        return locationRepository.findById(id).orElseThrow(() -> new NotFoundException("Location with id " + id));
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location updateLocation(UUID id, Location location) {
        Location existingLocation = locationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Location with id " + id));

        existingLocation.setName(location.getName());
        existingLocation.setAddress(location.getAddress());
        return locationRepository.save(existingLocation);
    }

    public boolean existsById(UUID id) {
        return locationRepository.existsById(id);
    }

    public void deleteLocation(UUID id) {
        locationRepository.deleteById(id);
    }
}
