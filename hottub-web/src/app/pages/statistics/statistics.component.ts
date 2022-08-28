import {Component, OnInit} from '@angular/core';
import {Statistics, StatisticsService} from "../../../generated";

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
  }
}
