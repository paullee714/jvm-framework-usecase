package com.example.springkotlinmongodbsecure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class MongoDBConfig {

    @Bean
    fun mappingMongoConverter(
        mongoDatabaseFactory: MongoDatabaseFactory,
        mongoMappingContext: MongoMappingContext
    ): MappingMongoConverter {
        val dbRefResolver = DefaultDbRefResolver(mongoDatabaseFactory)
        val mappingMongoConverter = MappingMongoConverter(dbRefResolver, mongoMappingContext)
        mappingMongoConverter.setTypeMapper(DefaultMongoTypeMapper(null));
        return mappingMongoConverter
    }
}
