package com.sloyardms.trackerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.exception.ResourceDuplicatedException;
import com.sloyardms.trackerapi.exception.ResourceNotFoundException;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

@WebMvcTest(UserController.class)
public class UserControllerSliceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Get user by ID - invalid user ID")
    void testGetById_whenInvalidIdProvided_returnsNotFound () throws Exception {
        //Arrange
        UUID uuid = UUID.randomUUID();
        Mockito.when(userService.findByUuid(uuid)).thenThrow(new ResourceNotFoundException("User with UUID " + uuid + " not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/users/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //Assert
        Assertions.assertEquals(404, mvcResult.getResponse().getStatus(), "Response status should be 404");
    }

    @Test
    @DisplayName("Create user - Username already in DB")
    void testCreate_whenDuplicatedUserProvided_returnsResourceDuplicatedException() throws Exception {
        //Arrange
        UUID uuid = UUID.randomUUID();
        UserCreateDto invalidDto  = new UserCreateDto();
        invalidDto .setUuid(uuid);
        invalidDto .setUsername("duplicatedUsername");

        Mockito.when(userService.create(Mockito.any(UserCreateDto.class))).thenThrow(new ResourceDuplicatedException("Username already exists"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int responseStatus = mvcResult.getResponse().getStatus();

        //Assert
        Assertions.assertEquals(HttpStatus.CONFLICT.value(),responseStatus, "Response status should be 409");
    }

    @Test
    @DisplayName("Create user - Username already in DB")
    void testCreate_whenUsernameIsMissing_returnsBadRequest() throws Exception {
        //Arrange
        UUID uuid = UUID.randomUUID();
        UserCreateDto invalidDto  = new UserCreateDto();
        invalidDto .setUuid(uuid);
        //no username

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int responseStatus = mvcResult.getResponse().getStatus();

        //Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),responseStatus, "Response status should be 400");
    }

    @Test
    @DisplayName("Update user - Invalid UUID")
    void testUpdate_whenInvalidUUIDIsProvided_returnsBadRequest() throws Exception{
        //Arrange
        String invalidUuid = "invalidUuid";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("newUsername");
        userUpdateDto.setDarkMode(true);
        userUpdateDto.setKeepOriginalImage(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/"+invalidUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDto));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int responseStatus = mvcResult.getResponse().getStatus();

        //Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseStatus, "Response status should be 400");

    }

    @Test
    @DisplayName("Delete user - Invalid UUID")
    void testDelete_whenInvalidUUIDIsProvided_returnsNotFound() throws Exception {
        // Arrange
        UUID invalidUuid = UUID.randomUUID();
        Mockito.doThrow(new ResourceNotFoundException("User not found")).when(userService).delete(invalidUuid);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/users/" + invalidUuid);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(404, mvcResult.getResponse().getStatus(), "Response status should be 404 (Not Found)");
    }

}
