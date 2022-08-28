# Hottub Web application

A simple Angular application acting as a front end for the Hot Tub.
This app is packaged and shipped with the spring boot app.

Start local development server with `ng serve`

Package app with `ng build --configuration production`

API and interface layer generated with `npx @openapitools/openapi-generator-cli generate -i ../hottub/src/main/resources/openapi/hottub-api-v1.0.yml -g typescript-angular -o src/generated`
