package ax.dkarlsso.hottub.service;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.model.settings.TimerSettings;
import dkarlsso.commons.repository.settings.SettingsFilesystemRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service which is thread safe to deal with settings and stats from hottub
 */
public class OperationsService {
    private final Settings settings;

    private final OperationalData operationalData = new OperationalData();

    private final List<TimerSettings> timerSettings = new ArrayList<>();

    /** Actions that should be performed when settings has changed */
    private final List<Runnable> settingsChangedActions = new ArrayList<>();

    private final SettingsFilesystemRepository<Settings> settingsRepository;

    public OperationsService(final SettingsFilesystemRepository<Settings> settingsRepository) {
        this.settings = settingsRepository.read();
        this.settingsRepository = settingsRepository;
    }

    public Settings getSettings() {
        synchronized (this) {
            return settings.clone();
        }
    }

    public void updateSettings(final Settings settings) {
        synchronized (this) {
            this.settings.applySettings(settings);
            settingsChangedActions.forEach(Runnable::run);
            settingsRepository.save(this.settings);
        }
    }

    public OperationalData getOperationalData() {
        synchronized (this) {
            return this.operationalData.clone();
        }
    }

    public void updateOperationalData(final OperationalData operationalData) {
        synchronized (this) {
            this.operationalData.apply(operationalData);
        }
    }

    public List<TimerSettings> getTimers() {
        synchronized (this) {
            return new ArrayList<>(timerSettings).stream()
                    .sorted(Comparator.comparing(TimerSettings::getStartHeatingTime))
                    .collect(Collectors.toList());
        }
    }

    public void addTimer(final TimerSettings timerSettings) {
        synchronized (this) {
            this.timerSettings.add(timerSettings);
        }
    }

    public void updateTimer(final TimerSettings timerSettings) {
        synchronized (this) {
            this.timerSettings.remove(timerSettings);
            this.timerSettings.add(timerSettings);
        }
    }

    public void deleteTimer(final UUID id) {
        synchronized (this) {
            this.timerSettings.remove(new TimerSettings(id, null, null));
        }
    }

    public void registerSettingsUpdatedAction(final Runnable action) {
        synchronized (this) {
            settingsChangedActions.add(action);
        }
    }
}
