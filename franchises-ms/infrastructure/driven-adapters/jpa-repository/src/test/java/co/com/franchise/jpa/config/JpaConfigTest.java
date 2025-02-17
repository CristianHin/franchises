package co.com.franchise.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.env.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaConfigTest {
    @Mock
    private Environment env;

    private JpaConfig jpaConfigUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jpaConfigUnderTest = Mockito.spy(new JpaConfig());
    }

    @Test
    void dbSecretTest() {
        when(env.getProperty("spring.datasource.url")).thenReturn("jdbc:mysql://localhost:3306/franchise_test");
        when(env.getProperty("spring.datasource.username")).thenReturn("sa");
        when(env.getProperty("spring.datasource.password")).thenReturn("sa");

        DBSecret secretResult = jpaConfigUnderTest.dbSecret(env);

        assertEquals("jdbc:mysql://localhost:3306/franchise_test", secretResult.getUrl());
        assertEquals("sa", secretResult.getUsername());
        assertEquals("sa", secretResult.getPassword());
    }

    @Test
    void datasourceTest() {
        DBSecret dbSecret = DBSecret.builder()
                .password("sa")
                .username("sa")
                .url("jdbc:mysql://localhost:3306/franchise_test")
                .build();
        HikariDataSource mockDataSource = Mockito.mock(HikariDataSource.class);
        doReturn(mockDataSource).when(jpaConfigUnderTest).createHikariDataSource(any());
        DataSource result = jpaConfigUnderTest.datasource(dbSecret, "com.mysql.cj.jdbc.Driver");
        assertSame(mockDataSource, result);
    }


    @Test
    void entityManagerFactoryTest() {
        DataSource mockDataSource = Mockito.mock(DataSource.class);
        LocalContainerEntityManagerFactoryBean result = jpaConfigUnderTest.entityManagerFactory(mockDataSource);
        assertNotNull(result);
    }
}
