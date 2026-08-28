package com.alexhiz.hexagonal.helpdesk;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RedisSerializerTest {

    private GenericJacksonJsonRedisSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(Object.class)
                                .build()
                )
                .build();
    }

    @Test
    void testDepartmentListWithCollectorsToList() {
        Department dept = Department.builder()
                .id(UUID.randomUUID())
                .name("IT Support")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Department> departments = Stream.of(dept).collect(Collectors.toList());

        byte[] serialized = serializer.serialize(departments);
        assertNotNull(serialized);

        Object deserialized = serializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertInstanceOf(List.class, deserialized);

        @SuppressWarnings("unchecked")
        List<Department> resultList = (List<Department>) deserialized;
        assertEquals(1, resultList.size());
        assertInstanceOf(Department.class, resultList.get(0));
        assertEquals("IT Support", resultList.get(0).getName());
        assertEquals(dept.getId(), resultList.get(0).getId());
    }

    @Test
    void testSingleDepartment() {
        Department dept = Department.builder()
                .id(UUID.randomUUID())
                .name("HR")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        byte[] serialized = serializer.serialize(dept);
        assertNotNull(serialized);

        Object deserialized = serializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertInstanceOf(Department.class, deserialized);
        assertEquals("HR", ((Department) deserialized).getName());
    }
}
