package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.model.settings.TimerSettings;

import java.time.*;
import java.util.UUID;

public final class MapperUtils {


    public static ax.dkarlsso.hottub.interfaces.model.hottub_api.Settings toExternalSettings(final Settings settings) {
        return new ax.dkarlsso.hottub.interfaces.model.hottub_api.Settings()
                .temperatureLimit(settings.getHottubTemperatureLimit())
                .temperatureDelta(settings.getTemperatureDiff())
                .heatingPanTemperatureLimit(settings.getHeaterTemperatureLimit())
                .circulationTimeCycle(settings.getCirculationTimeCycle().toMinutesPart())
                .lightsOn(settings.isLightsOn())
                .debugMode(settings.isDebug());
    }


    public static Settings toInternalSettings(final ax.dkarlsso.hottub.interfaces.model.hottub_api.Settings settings) {
        return Settings.builder()
                .hottubTemperatureLimit(settings.getTemperatureLimit())
                .temperatureDiff(settings.getTemperatureDelta())
                .heaterTemperatureLimit(settings.getHeatingPanTemperatureLimit())
                .circulationTimeCycle(Duration.ofMinutes(settings.getCirculationTimeCycle()))
                .lightsOn(settings.getLightsOn())
                .debug(settings.getDebugMode())
                .build();
    }


    public static ax.dkarlsso.hottub.interfaces.model.hottub_api.TimerEntity toExternalTimer(final TimerSettings timer) {
        return new ax.dkarlsso.hottub.interfaces.model.hottub_api.TimerEntity()
                .id(timer.getUuid())
                .settings(toExternalSettings(timer.getSettings()))
                .time(OffsetDateTime.ofInstant(timer.getStartHeatingTime(), ZoneId.systemDefault()));
    }


    public static TimerSettings toInternalTimer(final ax.dkarlsso.hottub.interfaces.model.hottub_api.Timer timer) {
        return toInternalTimer(timer, UUID.randomUUID());
    }

    public static TimerSettings toInternalTimer(final ax.dkarlsso.hottub.interfaces.model.hottub_api.Timer timer, final UUID id) {
        return TimerSettings.builder()
                .settings(toInternalSettings(timer.getSettings()))
                .startHeatingTime(timer.getTime().toInstant())
                .uuid(id)
                .build();
    }
}
