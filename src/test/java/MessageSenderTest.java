import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import org.mockito.Mockito;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import static ru.netology.sender.MessageSenderImpl.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageSenderTest {
    @BeforeEach
    public void initGeoserviceTest() {
        System.out.println("Начало теста");
    }

    @AfterEach
    public void finishGeoserviceTest() {
        System.out.println("\nКонец теста");
    }

    @CsvFileSource(files = "src/test/resources/messagesender.csv")
    @ParameterizedTest
    public void messageSenderTest(String ipaddr, String mssg) {
        System.out.println("Тестируем класс MessageSenderImpl ===================");

        Location loc = new Location("Moscow", Country.RUSSIA, null, 0);

        GeoService geoService = Mockito.mock(GeoService.class);
        Mockito.when(geoService.byIp(ipaddr))
                .thenReturn(loc);

        LocalizationService localizationService = Mockito.mock(LocalizationService.class);
        Mockito.when(localizationService.locale(loc.getCountry()))
                .thenReturn(mssg);

        MessageSender sender = new MessageSenderImpl(geoService, localizationService);

        Map<String, String> header = new HashMap<>();
        header.put("x-real-ip", ipaddr);

        assertEquals(mssg, sender.send(header));
    }


//Тестируем метод byIp() класса GeoServiceImpl - определение адреса по ip

    @CsvFileSource(files = "src/test/resources/geoservice.csv")
    @ParameterizedTest
    public void testOfLocation(String ip, String city, Country country, String street, int building) {
        System.out.println("Тестируем метод byIp() класса GeoServiceImpl ===================");

        GeoServiceImpl ipaddr = new GeoServiceImpl();
        Location loc = ipaddr.byIp(ip);

        Assertions.assertAll(
                () -> assertEquals(city, loc.getCity()),
                () -> assertEquals(country, loc.getCountry()),
                () -> assertEquals(street, loc.getStreet()),
                () -> assertEquals(building, loc.getBuiling())
        );
    }

    @CsvFileSource(files = "src/test/resources/locale.csv")
    @ParameterizedTest
    public void testOfLocale(Country country, String mssg) {
        System.out.println("Тестируем метод locale() класса LocalizationService ===================");

        LocalizationService loc = new LocalizationServiceImpl();
        assertEquals(mssg, loc.locale(country));
    }
}
