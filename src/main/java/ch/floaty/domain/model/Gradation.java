package ch.floaty.domain.model;

/**
 * An optional informal refinement of a {@link CertificationClass}, as in "Low B".
 *
 * <p>This is community vocabulary from reviews and forums, not part of EN 926-2 and not
 * published by manufacturers. It is only meaningful alongside a certification class.
 */
public enum Gradation {
    LOW,
    MID,
    HIGH
}
