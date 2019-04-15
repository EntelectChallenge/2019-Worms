using System;
using System.ComponentModel;
using System.Linq;

namespace StarterBot.Extensions
{
    public static class EnumExtensions
    {
        public static string GetDescription(this Enum genericEnum)
        {
            var genericEnumType = genericEnum.GetType();
            var memberInfo = genericEnumType.GetMember(genericEnum.ToString());
            if (memberInfo == null || memberInfo.Length <= 0)
            {
                return genericEnum.ToString();
            }

            var attributes = memberInfo[0].GetCustomAttributes(typeof(DescriptionAttribute), false);
            if (attributes != null && attributes.Any())
            {
                return ((DescriptionAttribute)attributes.ElementAt(0)).Description;
            }
            return genericEnum.ToString();
        }
    }
}