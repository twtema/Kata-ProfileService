package org.kata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarNotFoundException;
import org.kata.service.AvatarService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Avatar", description = "The avatar API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @Operation(summary = "Get profile avatar")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The Avatar is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = AvatarDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The Avatar is NOT found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
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