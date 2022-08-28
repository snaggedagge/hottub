import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {StatsComponent} from "./pages/stats/stats.component";
import {TimersComponent} from "./pages/timers/timers.component";
import {LogsComponent} from "./pages/logs/logs.component";
import {StatisticsComponent} from "./pages/statistics/statistics.component";

const routes: Routes = [
  { path: '',   redirectTo: '/stats', pathMatch: 'full' },
  { path: 'stats', component: StatsComponent, },
  { path: 'timers', component: TimersComponent },
  { path: 'logs', component: LogsComponent },
  { path: 'statistics', component: StatisticsComponent },
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
