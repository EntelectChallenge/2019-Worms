using System;
using System.IO;
using Newtonsoft.Json;
using StarterBot.Entities;

namespace StarterBot
{
    public class Program
    {
        private static string _stateFileName = "state.json";

        static void Main(string[] args)
        {
            while (true) 
            {
                Int32.TryParse(Console.ReadLine(), out var roundNumber);
                var stateFileLocation = $"rounds/{roundNumber}/{_stateFileName}";

                var gameState = JsonConvert.DeserializeObject<GameState>(File.ReadAllText(stateFileLocation));
                var command = new Bot(gameState).Run();

                Console.WriteLine($"C;{roundNumber};{command}");
            }
        }
    }
}