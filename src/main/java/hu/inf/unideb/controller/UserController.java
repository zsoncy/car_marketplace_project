package hu.inf.unideb.controller;

import hu.inf.unideb.DTOs.BasicUserDto;
import hu.inf.unideb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping
    public ResponseEntity<BasicUserDto> saveUser(@NonNull @RequestBody BasicUserDto basicUserDto){
        System.out.println(basicUserDto);
        return ResponseEntity.ok(userService.saveUser(basicUserDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicUserDto> getUserById(@NonNull @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<BasicUserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasicUserDto> updateUser(@NonNull @RequestBody BasicUserDto updatedUser,
                                                   @NonNull @PathVariable Long id){
        return ResponseEntity.ok(userService.updateUser(id,updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@NonNull @PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User with the id of: " + id + "has been deleted");
    }
}
