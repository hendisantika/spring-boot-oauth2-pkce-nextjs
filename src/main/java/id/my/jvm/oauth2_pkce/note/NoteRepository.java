package id.my.jvm.oauth2_pkce.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByOwnerUsername(String username, Pageable pageable);

    Optional<Note> findByIdAndOwnerUsername(Long id, String username);
}
