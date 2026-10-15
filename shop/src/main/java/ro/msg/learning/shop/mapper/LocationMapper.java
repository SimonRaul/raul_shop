package ro.msg.learning.shop.mapper;

import ro.msg.learning.shop.dto.response.LocationResponse;
import ro.msg.learning.shop.model.Location;

public class LocationMapper {

    public LocationResponse toResponseDto(Location location) {
        return LocationResponse.builder()
                .name(location.getName())
                .country(location.getAddress().getCountry())
                .city(location.getAddress().getCity())
                .county(location.getAddress().getCounty())
                .streetAddress(location.getAddress().getStreetAddress())
                .build();
    }
}
