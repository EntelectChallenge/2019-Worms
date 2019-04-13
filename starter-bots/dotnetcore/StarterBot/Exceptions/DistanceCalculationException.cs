using System;

namespace StarterBot.Exceptions
{
    [Serializable]
    public class DistanceCalculationException : Exception
    {
        public DistanceCalculationException()
        {
        }

        public DistanceCalculationException(string message) : base(message)
        {
        }

        public DistanceCalculationException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}