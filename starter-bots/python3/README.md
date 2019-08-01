# Python Readme

## Python Installation:

	Simply download Python from https://www.python.org/downloads/ for the required OS
		- 3.7.2 for Python 3 Bots
	Alternatively you can also use a different distribution of python e.g. Anaconda.
		
	If you plan on using Anaconda, or any other python distribution,
	 ensure that the required paths have been added to your environment variables correctly.
	
	Once Installed ensure python has been installed correctly and works bt following these instructions :
		- In command line enter the command "python" (without quotes)
		- You should get a similar output to the following

			Python 3.7.2 (default, Feb 21 2019, 17:35:59) [MSC v.1900 64 bit (AMD64)] on win32
			Type "help", "copyright", "credits" or "license" for more information.
			>>>|

**Note**: if you are using Anaconda you will still need to have a standalone installation of the Python 3.7. Anaconda does not expose the `python` or `py` commands required for the game-runner to start-up the python bots.

## Python Dependencies:

	Dependencies will be handled using PIP ( or conda if using Anaconda ), all dependencies should be supplied in a 
	requirements.txt file in the same location as your bot.json. 
	If you require any dependencies list them within the requirements.txt file and run
		
		pip install -r requirements.txt
		
		For Anaconda Users:
		    conda install --file requirements.txt
		

## Running the sample bot:

	Run the following:
		
		python PythonStarterBot.py

**Note**: The game runner will resolve the python command that needs to be run on the current platform. 
For example, it will attempt to run `python3`, if that fails it will move on to trying `py -3`. 
The list of python commands that will be tried can be found in the `PythonBotRunner` in the game runner. 
If any python commands were not included simply contact the Entelect Challenge team or create a Pull Request for the new command. 