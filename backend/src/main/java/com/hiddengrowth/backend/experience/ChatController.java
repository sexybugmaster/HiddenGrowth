package com.hiddengrowth.backend.experience;

import com.hiddengrowth.backend.experience.dto.ExperienceCreateRequest;
import com.hiddengrowth.backend.experience.dto.ExperienceCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ExperienceRepository experienceRepository;

    @PostMapping("/chat")
    public ExperienceCreateResponse create(@RequestBody @Valid ExperienceCreateRequest req){
        Experience saved = experienceRepository.save(new Experience(req.content()));
        return new ExperienceCreateResponse(saved.getId());
    }
}
