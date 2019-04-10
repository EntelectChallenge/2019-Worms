using System;
using System.Collections.Generic;
using System.Text;

namespace StarterBot.Exceptions
{
    [Serializable]
    public class InvalidWormIdException : Exception
    {
        public InvalidWormIdException()
        {
        }

        public InvalidWormIdException(string message) : base(message)
        {
        }

        public InvalidWormIdException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}
