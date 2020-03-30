package com.cdph.covid19tracker.util;

public final class Utils
{
	public static final String formatNumber(int number)
	{
		String strNum = String.valueOf(number);
		int len = strNum.length();

		if(len <= 6 && len >= 4)
		{
			int thou = number / 1000;
			int hund = number % 1000;

			String strTh = String.valueOf(thou);
			String strHn = String.valueOf(hund);

			if(hund > 99)
				strNum = strTh + "." + (strHn.subSequence(0, 1));
			else
				strNum = strTh;

			strNum += "K";
		}

		if(len <= 9 && len >= 7)
		{
			int mill = number / 1000000;
			int thou = number % 1000000;

			String strMl = String.valueOf(mill);
			String strTh = String.valueOf(thou);

			if(thou > 99000)
				strNum = strMl + "." + strTh.subSequence(0, 1);
			else
				strNum = strMl;

			strNum += "M";
		}

		return strNum;
	}
}
