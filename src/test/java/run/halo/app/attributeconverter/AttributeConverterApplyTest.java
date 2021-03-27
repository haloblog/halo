package run.halo.app.attributeconverter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import run.halo.app.config.JpaConfiguration;

/**
 * Attribute converter apply result test.
 *
 * @author johnniang
 */
@DataJpaTest
@Import(JpaConfiguration.class)
class AttributeConverterApplyTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    CityRepository cityRepository;

    @Test
    void shouldAutoAppliedForAttributeConverter() {
        final var city = new City();
        city.setName("ChengDu");
        city.setLevel(CityLevel.CITY);
        cityRepository.save(city);

        final var cityId = city.getId();

        Query nativeQuery =
            entityManager.createNativeQuery("SELECT level from city where id = " + cityId);
        Object level = nativeQuery.getSingleResult();
        Assertions.assertEquals(CityLevel.PROVINCE.getValue(), level);
    }

    @SpringBootApplication
    static class Application {
    }
}
