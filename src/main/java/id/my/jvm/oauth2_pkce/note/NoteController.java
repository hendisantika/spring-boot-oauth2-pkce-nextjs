package id.my.jvm.oauth2_pkce.note;

import id.my.jvm.oauth2_pkce.note.NoteDtos.NoteRequest;
import id.my.jvm.oauth2_pkce.note.NoteDtos.NoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public PagedModel<NoteResponse> list(@AuthenticationPrincipal Jwt jwt,
                                         @PageableDefault(size = 20, sort = "updatedAt",
                                                 direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NoteResponse> page = noteService.findAll(jwt.getSubject(), pageable).map(NoteResponse::from);
        return new PagedModel<>(page);
    }

    @GetMapping("/{id}")
    public NoteResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return NoteResponse.from(noteService.findOne(jwt.getSubject(), id));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody NoteRequest request) {
        Note note = noteService.create(jwt.getSubject(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(note.getId()).toUri();
        return ResponseEntity.created(location).body(NoteResponse.from(note));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                               @Valid @RequestBody NoteRequest request) {
        return NoteResponse.from(noteService.update(jwt.getSubject(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        noteService.delete(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }
}
