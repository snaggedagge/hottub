// Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Amazon Software License
// http://aws.amazon.com/asl/

/* eslint-disable  func-names */
/* eslint-disable  no-console */
/* eslint-disable  no-restricted-syntax */
const Alexa = require('ask-sdk');
const axios = require('axios');

async function getAddress() {
    var address = '';
    await axios.get('http://dkarlsso.com/data/websites.json')
      .then(websitesResponse => {
        const hottubData = websitesResponse.data.find(function(value, index) {
          return value.websiteId === 'hottub';
        });
        console.log(`Link to hottub is : ${hottubData.websiteLink}`);
        address = hottubData.websiteLink;
      })
    return address;
}

async function changeSettings(handlerInput, changeSettingsFunction, responseMethod) {
    var response = handlerInput.responseBuilder
          .speak("Unknown error ocurred. Shits fucked")
          .getResponse();

    const errorCallback = error => {
        console.error(error);
        response = handlerInput.responseBuilder
          .speak("Could not ask hottub for current settings, probably an network error. Have you tried turning it on and off again?")
          .getResponse();
      };

    // No auth yet, cause im hardcore
    const address = await getAddress();
    axios.get(`${address}/api/settings`)
      .then(settingsResponse => {
        const currentSettings = settingsResponse.data;
        response = changeSettingsFunction(currentSettings);
        axios.put(`${address}/api/settings`, currentSettings)
          .then(settingsResponse => {})
          .catch(errorCallback);
      })
      .catch(errorCallback);
    return response;
}

async function getStats() {
    var response;
    const address = await getAddress();
    axios.get(`${address}/api/stats`)
      .then(statsResponse => {
        response = statsResponse.data;
      });
    return response;
}

const HelpIntent = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.HelpIntent';
  },
  handle(handlerInput) {
    return handlerInput.responseBuilder
      .speak("You can increase temperature by asking for it or decrease it." +
             "You can also say things like, 'Alexa, ask hot tub for stats', or, 'Alexa, ask hot tub to turn on lights'")
      .getResponse();
  },
};

const IncreaseTemperature = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'IncreaseTemperature';
  },
  async handle(handlerInput) {
    console.log(`Increase temperature`);
    return changeSettings(handlerInput, (settings => {
        const oldTemp = settings.temperatureLimit;
        settings.temperatureLimit = settings.temperatureLimit + 1;
        const newTemp = settings.temperatureLimit;
        return handlerInput.responseBuilder
              .speak(`Lets boil some sunds boor. Increased temperature limit from ${oldTemp} degrees to ${newTemp} degrees`)
              .getResponse();
    }));
  },
};

const DecreaseTemperature = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'DecreaseTemperature';
  },
  async handle(handlerInput) {
    console.log(`Decrease temperature`);
    return changeSettings(handlerInput, (settings => {
        const oldTemp = settings.temperatureLimit;
        settings.temperatureLimit = settings.temperatureLimit - 1;
        const newTemp = settings.temperatureLimit;
        return handlerInput.responseBuilder
              .speak(`Time to cool off. Decreased temperature limit from ${oldTemp} degrees to ${newTemp} degrees`)
              .getResponse();
    }));
  },
};

const LightsIntent = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'Lights';
  },
  async handle(handlerInput) {
    console.log(`Switch lights`);
    return changeSettings(handlerInput, (settings => {
        const lightsOn = settings.lightsOn;
        settings.lightsOn = !lightsOn;

        if (lightsOn) {
            return handlerInput.responseBuilder
                  .speak('Switched off lights so y\'all can get real close and cosy')
                  .getResponse();
        }
        return handlerInput.responseBuilder
              .speak('On the 2nd day, Dag Said: Let there be lights')
              .getResponse();
    }));
  },
};


const StatsIntent = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'Stats';
  },
  async handle(handlerInput) {
    try {
        const stats = getStats();
        if (stats.temperature > 34) {
            return handlerInput.responseBuilder
              .speak(`The hottub is hot, and the ducks are ready. If you must know, temperature is ${stats.temperature} degrees.`)
              .getResponse();
        }
            return handlerInput.responseBuilder
              .speak(`The ducks are not yet ready, since temperature is only ${stats.temperature} degrees.`)
              .getResponse();
    }
    catch (error) {
        return handlerInput.responseBuilder
              .speak("Could not ask hottub for stats, probably an network error")
              .getResponse();
    }
  },
};

const EastereggIntent = {
  canHandle(handlerInput) {
    return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
      && Alexa.getIntentName(handlerInput.requestEnvelope) === 'Easteregg';
  },
  async handle(handlerInput) {
    return handlerInput.responseBuilder
          .speak("Hey, hey, hey, hey, hey, hey, hey, hey, Hey, hey, hey, hey, hey, hey, hey, hey, Hey, hey, hey, hey, hey, hey, hey, hey, There lived a certain man, in Russia long ago, He was big and strong, in his eyes a flaming glow. Most people looked at him, with terror, and with fear, But to Moscow chicks, he was such a lovely dear, He could preach the Bible like a preacher, Full of ecstasy and fire, But he also was the kind of teacher, Women would desire. Ra, ra Rasputin, Lover of the Russian queen. There was a cat that really was gone, Ra ra Rasputin, Russia's greatest love machine, It was a shame how he carried on. As my old mama always used to say, get oss my couch niggér!")
          .getResponse();
  },
};


const ErrorHandler = {
  canHandle() {
    return true;
  },
  handle(handlerInput, error) {
    console.log(`Error handled: ${error.message}`);
    console.log(`Error stack: ${error.stack}`);
    const requestAttributes = handlerInput.attributesManager.getRequestAttributes();

    return handlerInput.responseBuilder
      .speak("You have reached an unkind error handler")
      .getResponse();
  },
};

const FallbackHandler = {
  canHandle(handlerInput) {
    return true;
  },
  handle(handlerInput) {
    console.log(`Request type : ${Alexa.getRequestType(handlerInput.requestEnvelope)}`);
    console.log(`Intent name : ${Alexa.getIntentName(handlerInput.requestEnvelope)}`);
    return handlerInput.responseBuilder
      .speak("Fallback intent")
      .getResponse();
  },
};


const skillBuilder = Alexa.SkillBuilders.custom();

exports.handler = skillBuilder
  .addRequestHandlers(
    HelpIntent,
    IncreaseTemperature,
    DecreaseTemperature,
    StatsIntent,
    EastereggIntent,
    LightsIntent,
    FallbackHandler,
  )
  .addErrorHandlers(ErrorHandler)
  .lambda();
