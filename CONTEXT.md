# Floaty

A flight log for paraglider pilots: pilots record flights, the wings they flew, and the
sites they launched from and landed at.

## Language

**Pilot**:
A person who logs flights. Modelled as `User`, since that is also the authentication
identity.
_Avoid_: flyer, member.

**Flight**:
One recorded flight, from launch to landing, flown by one pilot on one glider.

**Glider**:
A single paraglider wing owned by one pilot, at one specific size. Each pilot's wing is its
own record, so two pilots flying the same model have two gliders.
_Avoid_: wing, canopy, paraglider (as an entity name).

**Size**:
The manufacturer's size designation for a wing, as printed by that manufacturer. An opaque
label, not a measurement: Advance ships the Sigma DLS as 20/22/24/26/28 while Skywalk ships
the Cumeo2 as 75/85/95/105/115, and neither is an area in m². Values are therefore not
comparable across manufacturers and are never parsed or sorted numerically.

**Certification Class**:
The EN 926-2 / LTF class a wing is rated at: A, B, C or D. `NONE` means the wing is
genuinely uncertified. `CCC` is the CIVL competition class, which is likewise not
EN-certified but is a distinct fact worth recording. Absent means not recorded, which is
different from `NONE`.
_Avoid_: rating, category, EN class.

**Gradation**:
An optional informal refinement of a certification class — Low, Mid or High, as in "Low B".
This is community vocabulary from reviews and forums, not part of EN 926-2 and not published
by manufacturers. Only meaningful alongside a certification class.
_Avoid_: sub-class, level.

**Spot**:
A named location a flight starts or ends at. A spot may be a launch site, a landing site, or
both.

**Track**:
The recorded GPS path of a flight, uploaded as an IGC file.
