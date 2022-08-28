import {Component, OnInit} from '@angular/core';
import {LogEntry, LogsService, Settings, SettingsService, Stats, StatsService} from "../../../generated";
import {FormControl, FormGroup, Validators} from "@angular/forms";

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

@Component({
  selector: 'app-timers',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.sass']
})
export class LogsComponent implements OnInit {
  logEntries: LogEntry | undefined;

  public constructor(private logsService: LogsService) {
  }

  ngOnInit(): void {
    this.logsService.getLogs().subscribe(logs => {
      this.logEntries = logs;
    })
  }
}
