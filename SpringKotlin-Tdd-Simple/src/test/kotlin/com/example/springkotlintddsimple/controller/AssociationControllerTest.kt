package com.example.springkotlintddsimple.controller

import com.example.springkotlintddsimple.repository.AssociationRepository
import com.example.springkotlintddsimple.service.AssociationService
import com.google.gson.Gson
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class AssociationControllerTest {

    @MockK
    var associationRepository: AssociationRepository = mockk<AssociationRepository>(relaxed = true)

    @InjectMockKs
    private var associationController: AssociationController = AssociationController()

    @InjectMockKs
    private var associationService: AssociationService = AssociationService(associationRepository)

    private lateinit var mockMvc: MockMvc

    private lateinit var gson: Gson


    @BeforeEach
    fun init() {
        mockMvc = MockMvcBuilders.standaloneSetup(associationController).build()
    }

    @Test
    fun mockMvcIsNotNull() {

        assertThat(associationController).isNotNull
        assertThat(mockMvc).isNotNull

    }
}
