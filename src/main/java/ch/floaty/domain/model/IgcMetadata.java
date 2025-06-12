package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class IgcMetadata {

    @Column(name = "uploaded_at")
    Instant uploadedAt;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_size")
    Long fileSize;

    @Column(name = "checksum")
    String checksum;
}
