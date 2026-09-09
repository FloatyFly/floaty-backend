package ch.floaty.infrastructure;

import ch.floaty.domain.model.CertificationClass;
import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.Gradation;
import ch.floaty.generated.GliderDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

/**
 * ModelMapper needs explicit converters for the certification enums: the domain enums and the
 * generated DTO enums are unrelated types, so implicit name matching cannot bridge them.
 *
 * <p>Both converters preserve null. A null certification class means "not recorded", which is a
 * different fact from {@link CertificationClass#NONE}, and collapsing the two would lose it.
 */
public class GliderMappingConfig {

    public static void configure(ModelMapper modelMapper) {
        modelMapper.addMappings(new PropertyMap<Glider, GliderDto>() {
            @Override
            protected void configure() {
                using(ctx -> {
                    CertificationClass source = (CertificationClass) ctx.getSource();
                    return source == null
                            ? null
                            : GliderDto.CertificationClassEnum.fromValue(source.name());
                }).map(source.getCertificationClass(), destination.getCertificationClass());

                using(ctx -> {
                    Gradation source = (Gradation) ctx.getSource();
                    return source == null
                            ? null
                            : GliderDto.GradationEnum.fromValue(source.name());
                }).map(source.getGradation(), destination.getGradation());
            }
        });
    }
}
