package com.example.springkotlintddsimple.controller

import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import com.example.springkotlintddsimple.domain.dto.AssociationRequest
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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
        gson = Gson()
        mockMvc = MockMvcBuilders.standaloneSetup(associationController).build()
    }

    @Test
    fun mockMvcIsNotNull() {

        assertThat(associationController).isNotNull
        assertThat(mockMvc).isNotNull

    }

    @Test
    fun associationRegisterFailWithNoHeader() {
        val reqUrl: String = "/api/v1/association"

        val associationRequest: AssociationRequest = AssociationRequest(5000, AssociationName.SUBWAY)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(reqUrl)
                .content(gson.toJson(associationRequest))
                .contentType(MediaType.APPLICATION_JSON)
        )

        resultActions.andExpect(status().isBadRequest)
    }

}
