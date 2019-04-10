# .Net Core starter bot

## Environment requirements

Install Visual Studio 2017 from here:
https://www.visualstudio.com/downloads/

Install .Net core 2.2 from here:
https://dotnet.microsoft.com/download/dotnet-core/2.2

Open up the "StarterBot.sln" file in "./starter-bots/dotnetcore".

## Building

Make your modifications to the starter bot and once you are happy with your changes, right click in the project structure on the project, select `Build`.
This  will create a "StarterBot.dll" file in the folder "./dotnetcore/StarterBot/bin/Release/netcoreapp2.0".

## Running 

To run the bot, make sure the "bot.json" has a "botLocation" that points to the "netcoreapp2.0" folder. Then go to the starter-pack and edit the config.json file accordingly.
Then run the "run.bat" file on windows or the "run.sh" file for unix.