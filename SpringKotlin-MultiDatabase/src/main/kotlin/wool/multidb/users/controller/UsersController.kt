package wool.multidb.users.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wool.multidb.users.domain.UsersEntity
import wool.multidb.users.service.UsersService


@RestController
class UsersController(
    private val usersService: UsersService
) {
    @GetMapping("/users")
    fun getAllUsers(): List<UsersEntity> {
        return usersService.getUsers()
    }
}
