import {Configuration, ConfigurationParameters} from "../generated";

export const environment = {
  production: true
};
export function apiConfigFactory (): Configuration {
  const params: ConfigurationParameters = {
    basePath: "/api"
  }
  return new Configuration(params);
}
