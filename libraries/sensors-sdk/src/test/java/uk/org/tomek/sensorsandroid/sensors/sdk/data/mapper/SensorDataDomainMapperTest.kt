package uk.org.tomek.sensorsandroid.sensors.sdk.data.mapper

import android.hardware.Sensor
import android.hardware.SensorEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Field

class SensorDataDomainMapperTest {

    private val mapper = SensorDataDomainMapper()

    @Test
    fun `toDomain should map SensorEvent to SensorData`() {
        // Given
        // Sensor and SensorEvent are difficult to mock with MockK because they have final methods or public fields
        // in the Android stub JAR. We use a combination of mocking and reflection.
        
        val mockSensor = mockk<Sensor>(relaxed = true)
        // Try to mock getters if possible, otherwise we might need reflection for these too
        try {
            every { mockSensor.id } returns 1
            every { mockSensor.type } returns Sensor.TYPE_ACCELEROMETER
            every { mockSensor.name } returns "Mock Sensor"
        } catch (e: Exception) {
            // If mocking methods fails, we'll rely on the fact that we might need to set internal fields
            // but usually getId() etc are mockable if they are not final.
        }

        val mockEvent = mockk<SensorEvent>(relaxed = true)
        val expectedValues = floatArrayOf(1.0f, 2.2f, 3.5f)
        val expectedTimestamp = 1000L

        // SensorEvent fields (sensor, values, timestamp) are public fields, not properties.
        // MockK cannot mock field access with 'every'. We must use reflection.
        setField(mockEvent, SensorEvent::class.java, "sensor", mockSensor)
        setField(mockEvent, SensorEvent::class.java, "values", expectedValues)
        setField(mockEvent, SensorEvent::class.java, "timestamp", expectedTimestamp)

        // When
        val result = mapper.toDomain(mockEvent)

        // Then
        assertEquals(1, result.sensorId)
        assertEquals(Sensor.TYPE_ACCELEROMETER, result.sensorType)
        assertEquals("Mock Sensor", result.sensorName)
        assertEquals(expectedValues.toList(), result.sensorValues)
    }

    private fun setField(obj: Any, clazz: Class<*>, fieldName: String, value: Any?) {
        val field: Field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }
}
