using System;
using System.IO;
using Newtonsoft.Json;
using StarterBot.Entities;

namespace StarterBot
{
    public class Program
    {
        private const string StateFileName = "state.json";

        private static void Main(string[] args)
        {
            while (true) 
            {
                int.TryParse(Console.ReadLine(), out var roundNumber);
                var stateFileLocation = $"rounds/{roundNumber}/{StateFileName}";

                var gameState = JsonConvert.DeserializeObject<GameState>(File.ReadAllText(stateFileLocation));
                var command = new Bot(gameState).Run();

                Console.WriteLine($"C;{roundNumber};{command}");
                roundNumber += 1;
            }
        }
    }
}