# Python Readme

## Python Installation:

	Simply download the desired version of Python and OS from https://www.python.org/downloads/ 
		- 3.6.x for Python 3 Bots
		- 2.7.x for Python 2 Bots
		
	If you plan on using Anaconda, or any other python distribution,
	 ensure that the anaconda installation paths have been added to your environment variables.
	
	Once Installed ensure python has been installed correctly and works bt following these instructions :
		- In command line enter the command "python" (without quotes)
		- You should get a similar output to the following
		
			Python 3.6.2 (v3.6.2:5fd33b5, Jul  8 2017, 04:57:36) [MSC v.1900 64 bit (AMD64)] on win32
			Type "help", "copyright", "credits" or "license" for more information.
			>>>|
			
		
## Python Dependencies:

	Dependencies will be handled using PIP, all dependencies should be supplied in a requirements.txt file in the same location as your bot.json. 
	If you require any dependencies list them within the requirements.txt file and run
		
		pip install -r requirements.txt
		

## Running the sample bot:

	Run the following:
		
		python StarterBot.py
