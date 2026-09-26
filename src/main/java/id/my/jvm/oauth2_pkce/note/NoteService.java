package id.my.jvm.oauth2_pkce.note;

import id.my.jvm.oauth2_pkce.common.ResourceNotFoundException;
import id.my.jvm.oauth2_pkce.note.NoteDtos.NoteRequest;
import id.my.jvm.oauth2_pkce.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<Note> findAll(String username, Pageable pageable) {
        return noteRepository.findByOwnerUsername(username, pageable);
    }

    @Transactional(readOnly = true)
    public Note findOne(String username, Long id) {
        return noteRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Note %d not found".formatted(id)));
    }

    @Transactional
    public Note create(String username, NoteRequest request) {
        Note note = new Note();
        note.setOwner(userService.getByUsername(username));
        note.setTitle(request.title());
        note.setContent(request.content());
        return noteRepository.saveAndFlush(note);
    }

    @Transactional
    public Note update(String username, Long id, NoteRequest request) {
        Note note = findOne(username, id);
        note.setTitle(request.title());
        note.setContent(request.content());
        return noteRepository.saveAndFlush(note);
    }

    @Transactional
    public void delete(String username, Long id) {
        noteRepository.delete(findOne(username, id));
    }
}
