import {Component, OnInit} from '@angular/core';
import {Statistics, StatisticsService} from "../../../generated";
import {startOfDay, isSameDay, isSameMonth} from 'date-fns';
import { Subject } from 'rxjs';
import {CalendarEvent, CalendarView} from 'angular-calendar';
import { EventColor } from 'calendar-utils';


const colors: Record<string, EventColor> = {
  red: {
    primary: '#ad2121',
    secondary: '#FAE3E3',
  },
  blue: {
    primary: '#1e90ff',
    secondary: '#D1E8FF',
  },
  yellow: {
    primary: '#e3bc08',
    secondary: '#FDF1BA',
  },
};

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.sass']
})
export class StatisticsComponent implements OnInit {

  statisticsDataSource: Statistics[] = []

  public constructor(private statisticsService: StatisticsService) {
  }

  ngOnInit(): void {
    this.statisticsService.getStatistics().subscribe(statistics => {
      this.statisticsDataSource = [statistics];
    })
    this.statisticsService.getBathDates().subscribe(bathDates => {
      bathDates.forEach(bathDate => {
        this.events.push({
          start: startOfDay(new Date(Date.parse(bathDate))),
          end: new Date(Date.parse(bathDate)),
          title: 'Some serious bathing went down that day',
          color: colors['blue'] ,
          allDay: true,
        });
      });
      this.refresh.next(undefined);
    })
  }

  // Most stuff hijacked from https://mattlewis92.github.io/angular-calendar/#/kitchen-sink
  view: CalendarView = CalendarView.Month;

  CalendarView = CalendarView;

  viewDate: Date = new Date();

  refresh = new Subject<void>();

  events: CalendarEvent[] = [];

  activeDayIsOpen: boolean = true;

  dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
    if (isSameMonth(date, this.viewDate)) {
      if ((isSameDay(this.viewDate, date) && this.activeDayIsOpen === true) || events.length === 0) {
        this.activeDayIsOpen = false;
      } else {
        this.activeDayIsOpen = true;
      }
      this.viewDate = date;
    }
  }

  closeOpenMonthViewDay() {
    this.activeDayIsOpen = false;
  }
}
