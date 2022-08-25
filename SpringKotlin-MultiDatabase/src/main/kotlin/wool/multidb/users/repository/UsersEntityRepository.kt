package wool.multidb.users.repository

import org.springframework.data.jpa.repository.JpaRepository
import wool.multidb.users.domain.UsersEntity

interface UsersEntityRepository: JpaRepository<UsersEntity, Int> {
}
