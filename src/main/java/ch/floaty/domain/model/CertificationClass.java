package ch.floaty.domain.model;

/**
 * The EN 926-2 / LTF certification class a glider is rated at.
 *
 * <p>A, B, C and D are the classes defined by the standard. {@link #NONE} means the wing is
 * genuinely uncertified. {@link #CCC} is the CIVL competition class, which is likewise not
 * EN-certified but is a distinct fact worth recording.
 *
 * <p>A null certification class means "not recorded", which is different from {@link #NONE}.
 */
public enum CertificationClass {
    A,
    B,
    C,
    D,
    NONE,
    CCC
}
