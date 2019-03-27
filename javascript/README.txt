# Javascript starter bot

## Environment requirements

Install Node.js 8.x.x or above for your environment here: 
https://nodejs.org/en/download/

Install IntelliJ IDEA from here: 
https://www.jetbrains.com/idea/download/
The community edition is free.

## Making changes

Copy the javascript bot folder and rename it to "my-javascript-bot".
In this folder, edit the "bot.json" file to match your personal information (author, email and nickName)
Open IntelliJ and use it to open the "my-javascript-bot" directory. Make your modifications to the starter bot in the "StarterBot.js" file.

## Running 

Go to the starter-pack and edit the config.json file, so that "player-a" points to "my-javascript-bot" directory.
Then run the "run.bat" file on windows or the "run.sh" file for unix.

## Troubleshooting - Debugging

In IntelliJ, at the directory structure to the left, right-click on the "StarterBot.js" file, then select "Create StarterBot.js..."(NodeJs icon) and choose "OK".

On the top toolbar you should now have a green play icon. This will run your bot, so make sure it has the "state.json" file in the same folder.

In the "StarterBot.js" file, click on the column to the left of the code. This will create a red circle icon, which is called a "breakpoint". These will pause your bot at that line of code, and provide you with valuable info like the current value of any variables at that point in time (Only works in Debug mode).

Click on the green "Debug" button (On the right side of the green play icon, there is a green bug icon). This will run your bot in Debug mode, meaning it will pause at the breakpoints you set. On the bottom of your screen you will see the debug window pop up, with information about each variable in your bot.

Click on one of these variables, and then press Alt+F8 to open up the "Evaluation" window. Here you can type any javascript code and test it on the current state of variables, a fantastic way to test your hypothesis for why the bot might be behaving differently than expected.

Debug your bot on different rounds by swapping the contents of the "state.json" file with that of the "JsonMap.json" file from the "./starter-pack/tower-defence-matches/{date}/round xxx" directory. Here you can choose the round you are interested in.
