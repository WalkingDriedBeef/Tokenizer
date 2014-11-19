// FindLetterAndNumber.cpp : Defines the entry point for the console application.

#include "FindLetterAndNumber.h"

char *Globle;

//确认字符是否是数字
bool TTSIsShuZiChar(const char *szBuf)
{
	unsigned char c1,c2;
	c1 = (unsigned char)szBuf[0];
	if (c1 >= '0' && c1 <= '9')
	{
		return true;
	}    
	if (c1 == 0xA3)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 >= 0xB0 && c2 <=0xB9)
		{
			return true;
		}
	}
	return false;
}

//确定字符是否是一个空格，tab键

bool TTSIsSpaceChar(const char *szBuf)
{
	unsigned char c1,c2;
	c1 = (unsigned char)szBuf[0];
	if (c1=='\t' || c1 == ' ' || c1 == 0x80)
	{
		return true;
	}
	if (c1 >= 0xA1 && c1 <= 0xA7)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 == 0xA0)
		{
			return true;
		}
	}
	return false;    
}


//确定是否是字母a~z,A_Z,         
bool TTSIsZiMuChar(const char *szBuf)
{
	unsigned char c1,c2;
	c1 = (unsigned char)szBuf[0];
	if (c1 >= 'A' && c1 <= 'Z')
	{
		return true;
	}    

	if (c1 >= 'a' && c1 <= 'z')
	{
		return true;
	}
	if (c1 == 0xA3)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 >= 0xC1 && c2 <= 0xDA)
		{
			return true;
		}
		if (c2 >= 0xE1 && c2 <= 0xFA)
		{
			return true;
		}
	}
	return false;
}

int JustGetStepSize(const char *szBuf)
{
	unsigned char c1;   //定义一个字符
	if (szBuf[0] == '\0') //空串
	{
		return 0;  //返回0
	}
	c1 = (unsigned char)szBuf[0];
	if (szBuf[1] == '\0' || c1 <= 0x80 || c1 == 0xFF)    //一个字符
	{                 
		return 1;             
	}
	return 2;
}

//确定是否是汉字
bool TTSIsHanZiChar(const char *szBuf)
{
	unsigned char c1,c2;
	c1 = (unsigned char)szBuf[0];
	if (c1 >= 0x80 && c1 <= 0xA0 || c1 >= 0xB0 && c1 <= 0xF7)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 >= 0x40 && c2 <= 0xFE && c2 != 0x7F)
		{
			return true;
		}
	}
	if (c1 >= 0xAA && c1 <= 0xAF || c1 >= 0xF8 && c1 <= 0xFE)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 >= 0x40 && c2 <= 0xA0 && c2 != 0x7F)
		{
			return true;
		}
	}    
	return false;
}

//确定是否是GB2312汉字
bool TTSIsGB2312HanZiChar(const char *szBuf)
{
	unsigned char c1,c2;
	c1 = (unsigned char)szBuf[0];
	if (c1 >= 0xB0 && c1 <= 0xF7)
	{
		c2 = (unsigned char)szBuf[1];
		if (c2 >= 0xA1 && c2 <= 0xFE)
		{
			return true;
		}
	}        
	return false;
}
char * ConvertBanjiao2Quanjiao(char * szQuanjiao, char * szBanjiao)
{
	if(szBanjiao == NULL)
	{
		szQuanjiao[0] = '\0';
		return NULL;
	}
	char * p = szBanjiao;
	while(*p)
	{
		if(JustGetStepSize(p) == 1)
		{
			if(isupper(*p))
			{
				char tch = tolower(*p);
				*p = tch;
			}
		}
		p += JustGetStepSize(p);
	}
	char * pOut = szQuanjiao;
	int i;
	for(i = 0; i < strlen(szBanjiao); i++)
	{
		pOut[i * 2] = 0xa3;
		pOut[i * 2 + 1] = 0x80 + szBanjiao[i];
	}
	pOut[i*2] = '\0';
	return szQuanjiao;
}
char * ConvertQuanjiao2Banjiao(char *szBanjiao, char *szQuanjiao)
{
	if(szQuanjiao == NULL)
	{
		szBanjiao[0] = '\0';
		return NULL;
	}
	char * pOut = szBanjiao;
	int i;
	for(i = 0; i < strlen(szQuanjiao); i += 2)
	{
		pOut[i / 2] = szQuanjiao[i + 1] - 0x80;
	}
	pOut[i/2] = '\0';
	return szBanjiao;
}
int WhichChar(const char *szBuf)
{
	if (TTSIsShuZiChar(szBuf))
	{
		return SHUZI;
	}
	else if (TTSIsSpaceChar(szBuf))
	{
		return SPACE;
	}
	else if (TTSIsZiMuChar(szBuf))
	{
		return ZIMU;
	}
	else if (TTSIsHanZiChar(szBuf))
	{
		return HANZI;
	}
	else if (TTSIsGB2312HanZiChar(szBuf))
	{
		return HANZI;
	}
	else
	{
		return UNKNOWN;
	}
}

int bDelChar(int nCharType,CharOperater& Oper,char *szbuf)
{
	if (nCharType == HANZI && Oper.KeepHanzi != 0)
	{
		return 1;
	}
	if (nCharType == SHUZI && Oper.KeepShuZi != 0)
	{
		return 1;
	}
	if (nCharType == SPACE && Oper.KeepSpace != 0)
	{
		return 1;
	}
	if (nCharType == ZIMU && Oper.KeepZimu != 0)
	{
		return 1;
	}
	if (nCharType == UNKNOWN)
	{
		if (szbuf[0] != '\n')
		{
			return 1;
		}
	}
	return 0;
}

int OperaterChar(CharOperater& Oper,char *szbuf, int& nStep,int& nCharType,char *pDest)
{
	Globle = pDest;
	if (Oper.KeepHanzi == 2) //只保留汉字
	{
		if (Oper.DelLine == 1 && nCharType != HANZI)
		{
			if (szbuf[0] == '\n')
			{
				pDest[0] = szbuf[0];
				Globle += 1;
				return 0;
			}
			return -1;
		}
		else if (nCharType != HANZI)
		{
			if (szbuf[0] == '\n')
			{
				pDest[0] = szbuf[0];
				Globle += 1;
				return 0;
			}
			return 1;
		}
		else
		{
			pDest[0] = szbuf[0];
			pDest[1] = szbuf[1];
			Globle += 2;
			return 1;
		}

	}
	else if (Oper.DelLine == 1)
	{
		switch(nCharType)
		{
		case SHUZI:
			if (Oper.KeepShuZi == 1)
			{
				return -1;
			}
			break;
		case HANZI:
			if (Oper.KeepHanzi == 1)
			{
				return -1;
			}
			break;
		case SPACE:
			if (Oper.KeepSpace == 3)
			{
				return -1;
			}
			else if(Oper.KeepSpace == 2 && *szbuf == '\t')
			{
				return -1;
			}
			else if(Oper.KeepSpace == 1 && *szbuf == ' ' )
			{
				return -1;
			}
			break;
		case ZIMU:
			if (Oper.KeepZimu == 1)
			{
				return -1;
			}
			break;
		case UNKNOWN:
			break;
		}
		for (int jj = 0; jj < nStep; jj++)
		{
			pDest[jj] = szbuf[jj];
		}
		Globle += nStep;
		return 0;
	}
	else if (Oper.KeepElse == 1)           //删除其它
	{
		//判断是否删除

		if (bDelChar(nCharType,Oper,szbuf))  //删除该字符
		{
			return  1;
		}
		else
		{
			for (int jj = 0; jj < nStep; jj++)
			{
				pDest[jj] = szbuf[jj];
			}
			Globle += nStep;
			return 0;
		}
	}
	else
	{
		int ReturnVal = 0;
		switch(nCharType)
		{
		case SHUZI:
			if (Oper.KeepShuZi == 1)
			{
				ReturnVal = 1;

			}
			else
			{
				if (Oper.QuanToBan == 1 && nStep == 2)
				{
					ConvertQuanjiao2Banjiao(pDest,szbuf);
					Globle += 1;
					return 1;
				}
				else if (Oper.QuanToBan == 2 && nStep == 1)
				{
					ConvertBanjiao2Quanjiao(pDest,szbuf);
					Globle += 2;
					return 1;
				}
				if (Oper.nReplace == 1)
				{
					if (Oper.cRpl.Src == szbuf[0])
					{
						pDest[0] = Oper.cRpl.Dst;
						Globle += 1;
						return 1;
					}
				}
			}
			break;
		case HANZI:
			if (Oper.KeepHanzi == 1)
			{
				ReturnVal = 1;
			}
			break;
		case SPACE:
			if (Oper.KeepSpace == 3)
			{
				ReturnVal = 1;
			}
			else if(Oper.KeepSpace == 2  && nStep == 1 && szbuf[0] == '\t')
			{
				ReturnVal = 1;
			}
			else if(Oper.KeepSpace == 1 && nStep == 1 && szbuf[0] == ' ')
			{
				ReturnVal = 1;
			}
			else
			{
				if (Oper.QuanToBan == 1 && nStep == 2)
				{
					ConvertQuanjiao2Banjiao(pDest,szbuf);
					Globle += 1;
					return 1;
				}
				else if (Oper.QuanToBan == 2 && nStep == 1)
				{
					ConvertBanjiao2Quanjiao(pDest,szbuf);
					Globle += 2;
					return 1;
				}
				if (Oper.nReplace == 1)
				{
					if (Oper.cRpl.Src == szbuf[0])
					{
						pDest[0] = Oper.cRpl.Dst;
						Globle += 1;
						return 1;
					}
				}
			}
			break;
		case ZIMU:
			if (Oper.KeepZimu == 1)
			{
				ReturnVal = 1;
			}
			else
			{
				if (Oper.QuanToBan == 1 && nStep == 2)
				{
					ConvertQuanjiao2Banjiao(pDest,szbuf);
					Globle += 1;
					return 1;
				}
				else if (Oper.QuanToBan == 2 && nStep == 1)
				{
					ConvertBanjiao2Quanjiao(pDest,szbuf);
					Globle += 2;
					return 1;
				}
				if (Oper.nReplace == 1)
				{
					if (Oper.cRpl.Src == szbuf[0])
					{
						pDest[0] = Oper.cRpl.Dst;
						Globle += 1;
						return 1;
					}
				}
			}
			break;
		case UNKNOWN:
			if (nStep == 1 && Oper.nReplace == 1)
			{
				if (Oper.cRpl.Src == szbuf[0])
				{
					pDest[0] = Oper.cRpl.Dst;
					Globle += 1;
					return 1;
				}
			}
			break;
		}
		if (ReturnVal == 0)
		{
			for (int jj = 0; jj < nStep; jj++)
			{
				pDest[jj] = szbuf[jj];
			}
			Globle += nStep;
		}
		return ReturnVal;
	}
}
