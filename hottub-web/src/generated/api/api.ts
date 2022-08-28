export * from './logs.service';
import { LogsService } from './logs.service';
export * from './settings.service';
import { SettingsService } from './settings.service';
export * from './statistics.service';
import { StatisticsService } from './statistics.service';
export * from './stats.service';
import { StatsService } from './stats.service';
export * from './timers.service';
import { TimersService } from './timers.service';
export const APIS = [LogsService, SettingsService, StatisticsService, StatsService, TimersService];
