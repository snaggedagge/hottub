import {Component, OnDestroy, OnInit} from '@angular/core';
import {Settings, SettingsService, Stats, StatsService} from "../../../generated";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {interval, Subscription} from "rxjs";

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.sass']
})
export class StatsComponent implements OnInit, OnDestroy{

  updateStatsSubscription: Subscription = interval(10000).subscribe(val => {
    this.statsService.getStats().subscribe(stats => {
      this.statsDataSource = [stats];
    })
  });

  statsDataSource: Stats[] = [];
  settingsDataSource: Settings[] = [];
  setting: Settings | any;

  settingsForm = new FormGroup({
    temperatureLimit: new FormControl('', {validators: [Validators.required, Validators.min(5), Validators.max(45)]}),
    temperatureDelta: new FormControl('', {validators: [Validators.required, Validators.min(-2), Validators.max(8)]}),

    heatingPanTemperatureLimit: new FormControl('', {validators: [Validators.required, Validators.min(20), Validators.max(60)]}),
    circulationTimeCycle: new FormControl('', {validators: [Validators.required, Validators.min(2), Validators.max(15)]}),
    lightsOn: new FormControl(''),
    debugMode: new FormControl(''),
  });

  public constructor(private statsService: StatsService,
                     private settingsService: SettingsService) {
  }

  ngOnDestroy(): void {
    this.updateStatsSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.statsService.getStats().subscribe(stats => {
      this.statsDataSource = [stats];
    })

    this.settingsService.getSettings().subscribe(settings => {
      this.setting = settings
      this.settingsDataSource = [this.setting]
      this.settingsForm.setValue(this.setting)
    })
  }

  updateSettings() {
    const settings: Settings = (this.settingsForm.getRawValue() as unknown as Settings);
    settings.lightsOn = this.setting.lightsOn;
    settings.debugMode = this.setting.debugMode;
    this.settingsService.updateSettings(settings).subscribe();
  }
}
