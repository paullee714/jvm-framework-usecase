package com.example.springkotlinmongodbsecure.repository

import com.example.springkotlinmongodbsecure.domain.Friends
import org.springframework.data.mongodb.repository.MongoRepository

interface FriendsRepository : MongoRepository<Friends, String> {

}
