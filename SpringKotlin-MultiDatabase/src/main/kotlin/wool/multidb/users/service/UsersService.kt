package wool.multidb.users.service

import org.springframework.stereotype.Service
import wool.multidb.users.domain.UsersEntity
import wool.multidb.users.repository.UsersEntityRepository

@Service
class UsersService(
        private val usersEntityRepository: UsersEntityRepository
) {
    fun getUsers(): List<UsersEntity> = usersEntityRepository.findAll()
}
