package uk.org.tomek.sensorsandroid.domain

interface SensorsRepository {

    fun startSensors()

    fun stopLeasingSensors()
}