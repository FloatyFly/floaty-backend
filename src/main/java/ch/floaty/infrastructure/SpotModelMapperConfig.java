package ch.floaty.infrastructure;

import ch.floaty.domain.model.Spot;
import ch.floaty.generated.SpotDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;


public class SpotModelMapperConfig {

    public static void configure(ModelMapper modelMapper) {

        // Configure Spot to SpotDto mapping
        modelMapper.addMappings(new PropertyMap<Spot, SpotDto>() {
            @Override
            protected void configure() {
                // Map id to spotId
                map().setSpotId(source.getId());

                // Map type based on launch/landing site flags
                using(ctx -> {
                    Spot spot = (Spot) ctx.getSource();
                    if (spot.isLaunchSite() && spot.isLandingSite()) {
                        return SpotDto.TypeEnum.LAUNCH_AND_LANDING_SITE;
                    } else if (spot.isLaunchSite()) {
                        return SpotDto.TypeEnum.LAUNCH_SITE;
                    } else if (spot.isLandingSite()) {
                        return SpotDto.TypeEnum.LANDING_SITE;
                    } else {
                        throw new IllegalArgumentException("Spot must be either a launch site, landing site, or both.");
                    }
                }).map(source, destination.getType());
            }
        });

    }
}