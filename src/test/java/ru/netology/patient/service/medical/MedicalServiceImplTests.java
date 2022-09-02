package ru.netology.patient.service.medical;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public class MedicalServiceImplTests {

    MedicalService sut;

    @BeforeAll
    public static void startAll() {
        System.out.println("MedicalServiceImpl tests started");
    }

    @BeforeEach
    public void init() {
        System.out.println("MedicalServiceImpl test started");
    }

    @AfterEach
    public void finished() {
        System.out.println("MedicalServiceImpl test completed");
    }

    @AfterAll
    public static void finishedAll() {
        System.out.println("MedicalServiceImpl tests completed");
    }

    @ParameterizedTest
    @MethodSource("sourceForCheckBloodPressureTest")
    public void testSendForCheckBloodPressure(BloodPressure bloodPressure, int expected) {
        // given:
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);
        PatientInfoRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById("1"))
                .thenReturn(new PatientInfo("1", "testName", "testLastname",
                        LocalDate.of(1980, 1, 1),
                        new HealthInfo(new BigDecimal(36), new BloodPressure(120, 70))));
        sut = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        // when:
        sut.checkBloodPressure("1", bloodPressure);
        // then:
        Mockito.verify(sendAlertServiceMock, Mockito.times(expected)).send("Warning, patient with id: 1, need help");
        Mockito.verify(patientInfoFileRepositoryMock, Mockito.times(1))
                .getById(Mockito.anyString());
    }

    @ParameterizedTest
    @MethodSource("sourceForCheckTemperatureTest")
    public void testSendForCheckTemperature(int temperature, int expected) {
        // given:
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);
        PatientInfoRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById("2"))
                .thenReturn(new PatientInfo("2", "testName", "testLastname",
                        LocalDate.of(1980, 1, 1),
                        new HealthInfo(new BigDecimal(36), new BloodPressure(120, 70))));
        sut = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        // when:
        sut.checkTemperature("2", new BigDecimal(temperature));
        System.out.println();
        // then:
        Mockito.verify(sendAlertServiceMock, Mockito.times(expected)).send("Warning, patient with id: 2, need help");
        Mockito.verify(patientInfoFileRepositoryMock, Mockito.times(1))
                .getById(Mockito.anyString());
    }

    @ParameterizedTest
    @MethodSource("sourceForSendArgumentTest")
    public void testSendArgument(int temperature, String expected) {
        // given:
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);
        PatientInfoRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById("3"))
                .thenReturn(new PatientInfo("3", "testName", "testLastname",
                        LocalDate.of(1980, 1, 1),
                        new HealthInfo(new BigDecimal(36), new BloodPressure(120, 70))));
        sut = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when:
        sut.checkTemperature("3", new BigDecimal(temperature));
        System.out.println();
        // then:
        Mockito.verify(sendAlertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    private static Stream<Arguments> sourceForCheckBloodPressureTest() {
        return Stream.of(
                Arguments.of(new BloodPressure(1, 2), 1),
                Arguments.of(new BloodPressure(120, 70), 0));
    }

    private static Stream<Arguments> sourceForCheckTemperatureTest() {
        return Stream.of(
                Arguments.of(0, 1),
                Arguments.of(36, 0));
    }

    private static Stream<Arguments> sourceForSendArgumentTest() {
        return Stream.of(
                Arguments.of(0, "Warning, patient with id: 3, need help"));
    }
}