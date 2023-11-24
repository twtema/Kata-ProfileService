package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarNotFoundException;
import org.kata.service.AvatarService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/getActual")
    public ResponseEntity<AvatarDto> getAvatar(@RequestParam String icp, @RequestParam String uuid) {
        if (icp != null && uuid != null) {
            return new ResponseEntity<>(avatarService.getActualAvatar(icp, uuid), HttpStatus.OK);
        } else if (icp != null) {
            return new ResponseEntity<>(avatarService.getActualAvatar(icp), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AvatarNotFoundException.class)
    public ErrorMessage getAvatarHandler(AvatarNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
