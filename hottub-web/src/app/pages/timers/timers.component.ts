import {Component, OnInit} from '@angular/core';
import {Settings, SettingsService, Stats, StatsService, Timer, TimerEntity, TimersService} from "../../../generated";
import {FormControl, FormGroup, Validators} from "@angular/forms";

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

@Component({
  selector: 'app-timers',
  templateUrl: './timers.component.html',
  styleUrls: ['./timers.component.sass']
})
export class TimersComponent implements OnInit {
  timersDataSource: TimerEntity[] = [];
  setting: Settings | any;

  settingsForm = new FormGroup({
    temperatureLimit: new FormControl('', {validators: [Validators.required, Validators.min(5), Validators.max(45)]}),
    temperatureDelta: new FormControl('', {validators: [Validators.required, Validators.min(-2), Validators.max(8)]}),

    heatingPanTemperatureLimit: new FormControl('', {validators: [Validators.required, Validators.min(20), Validators.max(60)]}),
    circulationTimeCycle: new FormControl('', {validators: [Validators.required, Validators.min(2), Validators.max(15)]}),
    lightsOn: new FormControl(''),
    debugMode: new FormControl(''),
  });
  time = new FormControl('');


  public constructor(private timersService: TimersService,
                     private settingsService: SettingsService) {
  }

  ngOnInit(): void {
    this.timersService.getTimers().subscribe(timers => {
      this.timersDataSource = timers;
    })

    this.settingsService.getSettings().subscribe(settings => {
      this.setting = settings
      this.settingsForm.setValue(this.setting)
    })
  }

  updateSettings() {
    // Format: 2022-08-28T16:43:58.010+00:00
    console.log(this.time);
    let timeZoneHour = Math.abs(new Date().getTimezoneOffset() / 60);
    const timer: Timer = {time: `${this.time}:00.000+0${timeZoneHour}:00`,
      settings: this.settingsForm.getRawValue() as unknown as Settings}
    this.timersService.addTimer(timer).subscribe(value => {
      this.ngOnInit();
    });
  }

  deleteTimer(id: string) {
    this.timersService.deleteTimer(id).subscribe(value => {
      this.ngOnInit();
    })
  }
}
