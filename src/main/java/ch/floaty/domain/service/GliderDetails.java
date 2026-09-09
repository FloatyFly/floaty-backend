package ch.floaty.domain.service;

import ch.floaty.domain.model.CertificationClass;
import ch.floaty.domain.model.Gradation;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * The user-supplied properties of a glider, carried as one object rather than as positional
 * parameters. Five positional arguments — three of them adjacent strings — is the shape that
 * produces silent transposition bugs.
 *
 * <p>Every field except manufacturer and model may be null: a null {@code size} or
 * {@code certificationClass} means the pilot has not recorded it.
 */
@Value
@AllArgsConstructor
public class GliderDetails {
    String manufacturer;
    String model;
    String size;
    CertificationClass certificationClass;
    Gradation gradation;
}
