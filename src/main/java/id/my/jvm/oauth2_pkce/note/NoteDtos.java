package id.my.jvm.oauth2_pkce.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
public final class NoteDtos {

    private NoteDtos() {
    }

    public record NoteRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 10_000) String content) {
    }

    public record NoteResponse(
            Long id,
            String title,
            String content,
            Instant createdAt,
            Instant updatedAt) {

        public static NoteResponse from(Note note) {
            return new NoteResponse(note.getId(), note.getTitle(), note.getContent(),
                    note.getCreatedAt(), note.getUpdatedAt());
        }
    }
}
