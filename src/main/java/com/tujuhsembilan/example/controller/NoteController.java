package com.tujuhsembilan.example.controller;

import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tujuhsembilan.example.controller.dto.NoteDto;
import com.tujuhsembilan.example.model.Note;
import com.tujuhsembilan.example.repository.NoteRepo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

  private final NoteRepo repo;
  private final ModelMapper mdlMap;

  @GetMapping
  public ResponseEntity<?> getNotes() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) auth.getPrincipal();
    String username = jwt.getSubject(); // Assuming the subject contains the username

    return ResponseEntity
        .ok(repo.findAll()
            .stream()
            .filter(note -> note.getUsername().equals(username))
            .map(o -> mdlMap.map(o, NoteDto.class))
            .collect(Collectors.toSet()));
  }

  @PostMapping
  public ResponseEntity<?> saveNote(@RequestBody NoteDto body) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) auth.getPrincipal();
    String username = jwt.getSubject(); // Assuming the subject contains the username

    var newNote = mdlMap.map(body, Note.class);
    newNote.setUsername(username);
    newNote = repo.save(newNote);
    return ResponseEntity.status(HttpStatus.CREATED).body(newNote);
  }

}
